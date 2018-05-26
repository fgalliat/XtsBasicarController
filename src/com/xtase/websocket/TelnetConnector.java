package com.xtase.websocket;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
  
public class TelnetConnector {
  
  protected int telnetTargetPort = 1024;
  protected Socket sk = null;
  protected OutputStream out = null;
  protected InputStream in = null;
  
  public TelnetConnector(String host, int telnetTargetPort) throws Exception {
    _("Opening telnet "+host+":"+telnetTargetPort);
    this.telnetTargetPort = telnetTargetPort;
    this.sk = new Socket(host, telnetTargetPort);
    this.in  = sk.getInputStream();
    this.out = sk.getOutputStream();
    _("Opened telnet "+host+":"+telnetTargetPort);
  }

  // ==========================================
  
  public void sendText(String txt) throws Exception {
    if ( txt == null ) { return; }
    out.write( txt.getBytes() );
    out.flush();
  }
  
  // slow impl.
  public String readText() throws Exception {
    String result = "";

    boolean DBUG_frame = !true;
    if (DBUG_frame) System.out.print( "TLC ");  
    do {
      int ch = in.read();
      
      if ( ch == -1 ) { break; }
      if (DBUG_frame) System.out.print( "["+ ch +"|0x"+Integer.toHexString(ch)+"]"+( (char)ch )+" " );  
      result += (char)ch;

      // BEWARE !! very pourri
      if ( ch == (int)'\n' ) { break; }
      // BEWARE !! very pourri
      
    } while( in.available() > 0 );
    if (DBUG_frame) System.out.println();
    return result.length() == 0 ? null : result;
  }
  
  
  // ==========================================
  
  public void handle() {
     new Thread() { public void run() { try {
       
       boolean inRun = true;
       while(inRun) {
         String frame = readText();
         if ( frame == null ) { break; /* ???? close ???? */ }
         handleReceiveText(frame);
       }
       
       } catch(Exception ex) { 
        handleError(ex);
     } } }.start();
  }
  
  // ==========================================
  
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
   // ==========================================
  
  public void close() {
    try { in.close(); } catch(Exception ex) {}
    try { out.close(); } catch(Exception ex) {}
    try { sk.close(); } catch(Exception ex) {}
  }
  
  // ==========================================
  // ==========================================
  // ==========================================
  
  protected static boolean DBUG = false;
  protected static void log(Object o) {
    if (DBUG) _(o);
  }
  
  protected static Random rnd = new Random();
   static int rnd(int max) { return rnd.nextInt(max); }
    
   static void _(Object o) { System.out.println("TEL> "+o); }                          
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
  
}
