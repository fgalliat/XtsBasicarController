package com.xtase.websocket;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
  
/*

Small WebSocket class (but need an http one for Sec-WebSocket-Origin:)
Tested on :
Atrix 3G (android 2.3.4) webtop Firefox6 => WORKS
Atrix 3G (android 2.3.4) internal webkit => DOESN'T WORKS (lookat js wrapper lib, it might be the cause)
PC WindowsXP3 modern Chrome (18.0.1025) => WORKS

cf msg length : une piste ....
Your code to write the message length needs to be extended. The extended payload in the data framing diagram of the protocol spec shows what's missing.

For messages up to 125 bytes, your code is correct. 
For messages > 125 but <= 65536 bytes, you need to write 3 bytes - the first byte is 126; the following 2 bytes give the message length. 
For messages > 65536 bytes, you need to write 9 bytes - the first byte is 127; the following 8 bytes give the message length.

=======================================
13/05/2012 20:45
@this time :
can read up to 128 bytes long message
can send up to 256 bytes long message

have to requalify sendText (no more masked)
have to split sending message if > 256 bytes long

no more paranoid mode because can read len in readMessage (but up to 125-128 B)

=======================================
14/05/2012 13:13
@this time :
split sending message if > 256 bytes long
requalified sendText (no more masked)

STILL HAVE TODO:
* send client closing SEQUENCE (server decision)

=======================================
16/05/2012 11:28
@this time :
AVOIDED the 'ls /sdcard-ext/vm_mnt/www' BUG 
BUT STILL some '32msetIP.sjs' bug (of "^[1;32m") : CLIENT need to concatenate frames for transcode

*/
  
public class WebSocket {
  
  protected Socket sk = null;
  protected OutputStream skOut = null;
  protected InputStream  skIn  = null;
  
  protected static boolean DBUG = false;
  protected static void log(Object o) {
    if (DBUG) _(o);
  }
  
  // httpOriginUrl = "http://localhost:8080" => it may be on the same host
  // as WebSocketServer
  // httpOriginUrl can be == "http://$host$:8080" => $host$ will be substitute 
  // to WebSocketServer host
  // wsSskPort = 9876 => the WebSocketServer ssk port
  public WebSocket(Socket sk, String httpOriginUrl, int wsSskPort) throws Exception {
    this.sk = sk;
    this.skOut = sk.getOutputStream();
    this.skIn = sk.getInputStream();
    
    // ======== Handshaking process ===================
    // TODO : split that code 
    
    final Socket fSk = sk;
    log("accepted");
      
    PrintStream ps = new PrintStream( sk.getOutputStream() );  
      
    // -- read input http connection --
    String line;
    String host = null;
    String webSocketKey = null;

    while( (line = readLine(skIn) ) != null) {
      if (line.toLowerCase().contains("host:") && host == null) {
        host =line.substring("host:".length()).trim();
        if ( host.contains(":") ) { host = host.substring(0, host.indexOf(":")); }                                 
        log("<H< "+host);
      }
                               
      if (line.toLowerCase().contains("websocket-key:") && webSocketKey == null) {
        webSocketKey =line.substring("sec-websocket-key:".length()).trim();
        log("<K< "+webSocketKey);
      }
      //_("<< "+line);
      if ( line.trim().isEmpty() ) {
        break;
      }
    }
                               
    if ( host == null ) { host = "localhost"; }
    
    // ----- .....
    if ( httpOriginUrl.contains("$host$") ) {
      httpOriginUrl = replaceBy(httpOriginUrl, "$host$", host);
    }
    // ----- .....
    

    // -- Server Key Computing --
    //example : webSocketKey = "dGhlIHNhbXBsZSBub25jZQ==";                                 
    String k = webSocketKey.trim();
    log("INPUT-KEY:"+k);
    // Protocol Constant                                 
    k += "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    byte[] sha = SHA1.SHA1toBytes(k, false);
    k = new String( Base64.encode( sha ) );
    log("OUTPUT-KEY:"+k);
    // key -----
           
    // arbitrary, software dependent
    String protocolName = "wsTcp";
      
    // FF6 for atrix uses version 7 (but seems to prefer 12+)
    // Chrome 18 for windows uses version 13
                                 
    // upgrade protocol 12                      
    ps.print("HTTP/1.1 101 Switching Protocols\r\n"+
      "Upgrade: WebSocket\r\n"+
      "Connection: Upgrade\r\n"+

      "Sec-WebSocket-Accept: "+k+"\r\n"+
                               
      "Sec-WebSocket-Origin: "+ httpOriginUrl /*"http://"+ host +":"+ SimpleWebServer.sskPort*/ +"\r\n"+
      "Sec-WebSocket-Location: ws://"+ host +":"+ wsSskPort +"/\r\n"+
                               
      "Sec-WebSocket-Protocol: "+protocolName+"\r\n"+
      "\r\n");                      
    ps.flush();
  }

  // ==============================================
  // ====== Internal behaviour handling ===========
  
   public void handle() {
     new Thread() { public void run() { try {
      int ch;
      InputByteStack _in = new InputByteStack(skIn, 4);
      boolean validProtocol = true;
      while( (ch = _in.read() ) > -1) {
         
        _( "["+ ch +"|0x"+Integer.toHexString(ch)+"]"+( (char)ch )+" " );
  
        // http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-08
        // see bottom of file
        
        if ( ch == 0x81 && validProtocol) {
          // TEXT FRAME
          String txt = null;
          try { txt = readText(_in); }
          catch(Exception ex) { log(""+ex); validProtocol = false; }
          handleReceiveText(txt);
        } else if ( ch == 0x88 && validProtocol) {
          // CLOSING FRAME
          int closingSeq;
          // read all other closing bytes
          // can block process
          //while( (closingSeq = in.read()) != -1) {}
          handleClose();
          close(false);
          break;
        } else {
          validProtocol = false;
        }
        
      }
     } catch(Exception ex) { 
        handleError(ex);
     } } }.start();
   }
         
   // to override....
   public void handleError(Exception ex) {
     _(""+ex);
   }
         
   // to override....
   public void handleReceiveText(String txt) throws Exception {
      _("received : "+txt);
   }
         
   // to override....
   public void handleClose() {
      _("Closing Socket");
   }
         
  // ======/Internal behaviour handling/===========
  // ==============================================
  


    // ==========================================================
    // == WebSocket I/O routines
    // ==========================================================
    
    public void close() {           
      close(false);
    }
           
    public void close(boolean closeClientToo) {           
       if (closeClientToo) {
         // TODO : send client SEQUENCE
       }
       try { skOut.close(); } catch(Exception ex) {}
       try { skIn.close(); } catch(Exception ex) {}
       try { sk.close(); } catch(Exception ex) {}
    }
           
           
    // Assume that you received a 0x81 before 
    protected String readText(InputByteStack in) throws Exception {
       int frameType = in.read();
       // ex: 0X85 masked frame
       // > 0x80 masked frame max 125 bytes long (cf 0x80 = 128) 
       if ( frameType >= 0x80 ) {
          int len = frameType - 0x80;
          //_("recv len"+len);
          return readMaskedTextFrame(in, len);
       } else {
          int ch = frameType;
          System.out.print( "["+ ch +"|0x"+Integer.toHexString(ch)+"]"+( (char)ch )+" " );                           
          throw new Exception("NOT a masked text frame !");
       }
    }
    
    protected int[] readMaskFromFrame(InputByteStack in) throws Exception {
        int[] mask = new int[4];
        mask[0] = in.read();
        mask[1] = in.read();
        mask[2] = in.read();
        mask[3] = in.read();
    return mask;
    }  

    // BEWARE !! sk.in.available() can fail with PC-BROWSER :
    // 2 following queries !!
    // in that case : available isn't enought
    // that's why I use an InputByteStack
    protected String readMaskedTextFrame(InputByteStack in, int len) throws Exception {
        int[] mask = readMaskFromFrame(in);
        String result = "";
        int i=0;
        //boolean paranoidMode = true;

        do {
          int originalByte = in.read();
          
          /*if ( paranoidMode && originalByte == 0x81) {
            int _ch = in.read();
            // 0X85 masked frame
            // 0x91 by button sending
            if ( _ch == 0x85 || _ch == 0x91 ) {
              in.unread();
              in.unread();
              return result;
            } else {
              in.unread();
            }
          }*/
          //System.out.print(".");
          int transcodedByte = originalByte ^ mask[i%4];
          result += (char)transcodedByte;
          i++;
        } while( in.available() > 0 && i < len );
        //_("recv0x1="+result);

    return result;
    }


    public void sendText(String txt) throws Exception {
       //sendTextMasked(skOut, txt);
       sendTextNonMasked(skOut, txt);
    }

    protected void sendTextNonMasked(OutputStream out, String txt) throws Exception {
      if ( txt == null ) { return; }
      
      int maxLen = 256;
      
      // TODO : have to check that
      // to avoid an 0x81 0x80 => that could be interpreted as maskedFrame
      // to avoid an 0x81 0xFF
      //maxLen = 126;
      
      // AVOIDS the 'ls /sdcard-ext/vm_mnt/www' BUG 
      // BUT STILL some '32msetIP.sjs' bug : CLIENT need to concatenate frames for transcode
      maxLen = 112; // before 0xF0 - 0x80 !!!
      
      /*// TODO : !! BETTER !!
      // to avoid an 0x81 0x04 => that could be interpreted as 65536 bytes long maskedFrame
      if ( txt.length() == 4 ) {
        txt += " ";
      }*/
      
      for(int i=0; i < txt.length(); i+= maxLen) {
        int stop = i + maxLen;
        if ( stop > txt.length() ) { stop = txt.length(); }
        int start = i;
        
        sendTextNonMasked256bytes( out, txt.substring(start, stop) );
        // Chrome doesn't support that....
        //sendTextMasked( out, txt.substring(start, stop) );
      }
    }


    protected void sendTextNonMasked256bytes(OutputStream out, String txt) throws Exception {
      if ( txt == null ) { return; }
      
      // TODO : client Browser receives ONLY 256 1st chars !!
      // BEWARE : this method IS NOT MASKED !!!!
      byte[] result = new byte[ 1+1+txt.length() ];
      result[0] = (byte)0x81;
      result[1] = (byte)txt.length();
      
      byte[] tmp = txt.getBytes("UTF-8");
      
      for(int i=0; i < txt.length(); i++) {
        result[2+i] = tmp[i];
      }

      out.write(result);
      out.flush();
      //_("sent : '"+ txt +"'");

      debugFrame(result);
    }

    protected static void debugFrame(byte[] result) {
      //System.out.print("WS sending > ");
      for(int i=0; i < result.length; i++) {
        int ch = result[i];
        if ( ch < 0) { ch = 256 + ch; }
        //System.out.print( "["+ ch +"|0x"+Integer.toHexString(ch)+"]"+( (char)ch )+" " );
        //System.out.print( (char)ch );
        System.out.print( getHexa(ch) + " " );
        
        /*if ( result[i] == 0x88 ) {
          System.err.println( "\n\n========= Hey there was a 0x88 @"+ i +" ==========\n\n" );
        }*/
      }
      System.out.println("\n");                                 
    }
      
    protected static String getHexa(int a) {
      String result = Integer.toHexString(a);
      if ( result.length() < 2 ) { result = "0"+result; }
      return "0x"+result.toUpperCase();
    }
      
// max 127 bytes long
    protected void sendTextMasked(OutputStream out, String txt) throws Exception {
      if ( txt == null ) { return; }
      
      byte[] result = null;
      
      //throw new Exception("NO MORE USED !");

      result = new byte[ 1+1+4+txt.length() ];
      
      int[] mask = new int[4];
      mask[0] = rnd(256); // \
      mask[1] = rnd(256); //  |__ mask
      mask[2] = rnd(256); //  |
      mask[3] = rnd(256); // /
      
      result[0] = (byte)0x81;     // start text
      result[1] = (byte)(0x80 + txt.length());     // masked

      result[2] = (byte)mask[0]; // \
      result[3] = (byte)mask[1]; //  |__ mask
      result[4] = (byte)mask[2]; //  |
      result[5] = (byte)mask[3]; // /
      
      byte[] tmp = txt.getBytes("UTF-8");
      for(int i=0; i < txt.length(); i++) {
           int ch = tmp[i];
      if (ch < 0) { ch = 256 + ch; }
        result[6+i] = (byte)( (int)/*txt.charAt(i)*/ ch ^ mask[i%4] );
      }
      
      out.write(result);
      out.flush();

      debugFrame(result);

      /*_("sent : '"+ txt +"'");*/
    }
    
    // ==========================================================
    // == Low I/O routines
    // ==========================================================

    protected class InputByteStack {
      protected InputStream in = null;
      protected int bufferLength = -1;
      
      protected int[] buffer = null;
      protected int cursor = 0;
      protected int used = 0;              
      
      public InputByteStack(InputStream in, int bufferLength) {
        this.in = in;
        this.bufferLength = bufferLength;
        this.buffer = new int[ this.bufferLength ];       
      }
      
      public int read() throws Exception {
        int ch = -1;
        
        if (cursor == 0) {
          // read in
          ch = in.read();
          // shift buffer right
          int[] tmp = new int[ buffer.length ];
          System.arraycopy(buffer,0,tmp,1,buffer.length-1);
          buffer = tmp;
          // first buffer byte = readed byte
          buffer[0] = ch;
          used++;
          if ( used >= bufferLength ) { used = bufferLength-1; }
        } else {
          // read buffer
          ch = buffer[cursor-1];
          cursor--;
          if ( cursor < 0 ) { cursor = 0; }
        }
      return ch;
      }
      
      // cursor = bufferLength(..+1) cf cursor-1
      public void unread() throws Exception {
        cursor++; if ( cursor >= bufferLength ) { cursor = bufferLength; throw new Exception("can't unread more than capacity"); }
      }
    
      public int available() throws Exception {
        if (cursor == 0) {
          return in.available();
        } else {
          return used - (cursor-1);
        }
      }
    }

   // slow impl.
   public String readLine(InputStream in) throws Exception {
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     int ch;
     while( (ch = in.read()) > -1 ) {
       if ( (char)ch == '\r' ) { continue; }
       if ( (char)ch == '\n' ) { break; }
       baos.write( ch );
     }
     
     return baos.size() == 0 ? null : baos.toString();
   }
  

   protected static Random rnd = new Random();
   static int rnd(int max) { return rnd.nextInt(max); }
    
   static void _(Object o) { System.out.println("WSS> "+o); }                          
   static void dodo(long milis) { try { Thread.sleep(milis); } catch(Exception ex) {} }                                  

  public static String replaceBy(String str, String oldE, String newE) {
		if (str.indexOf(oldE) == -1)
			return (str); // 1ere mesure a prendre pour ne pas parcourir tt le
							// tableau inutilement

		String regularExpression = Pattern.quote(oldE);
		String replacement = Matcher.quoteReplacement(newE);

		String retBuffer = str.replaceAll(regularExpression, replacement);
    return retBuffer;                             
  }



/*

* DONE : client Browser sends ONLY 'Knock Knock Knock' (0x88 0x91) NOT 'Knock Knock Knock Knock !' (0x88 0x99) !!
=> the diff 0x91 to 0x99 = 0x08 -> the same diff as nbChars diff

'Hello'                      0x81 0x85 (0x85 => 0x80 + 5bytes long string)
'Knock Knock Knock'          0x81 0x91 (0x91 => 0x80 + 17bytes long string)
'Knock Knock Knock Knock !'  0x81 0x99 (0x99 => 0x80 + (17 + 9)bytes long string)

So, in Paranoid mode : wait for 0x81 + >= 0x80
ps : no more paranoid mode @ this time
======================================================

4.7. Examples
_This section is non-normative._

   o  A single-frame unmasked text message
      *  0x81 0x05 0x48 0x65 0x6c 0x6c 0x6f (contains "Hello")

   o  A single-frame masked text message
      *  0x81 0x85 0x37 0xfa 0x21 0x3d 0x7f 0x9f 0x4d 0x51 0x58
         (contains "Hello")

   o  A fragmented unmasked text message
      *  0x01 0x03 0x48 0x65 0x6c (contains "Hel")
      *  0x80 0x02 0x6c 0x6f (contains "lo")

   o  Ping request and response
      *  0x89 0x05 0x48 0x65 0x6c 0x6c 0x6f (contains a body of "Hello",
         but the contents of the body are arbitrary)

      *  0x8a 0x05 0x48 0x65 0x6c 0x6c 0x6f (contains a body of "Hello",
         matching the body of the ping)

   o  256 bytes binary message in a single unmasked frame
      *  0x82 0x7E 0x0100 [256 bytes of binary data]

   o  64KiB binary message in a single unmasked frame
      *  0x82 0x7F 0x0000000000010000 [65536 bytes of binary data]
*/

}
