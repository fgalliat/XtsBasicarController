package com.xtase.websocket;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class SimpleWebServer {
  
  public SimpleWebServer(){}
  
  static int sskPort = 8080; 
  
  public static void main(String[] args) throws Exception {
     if ( args!= null && args.length > 0 ) {
       try { sskPort = Integer.parseInt(args[0]); } catch(Exception ex) {}
     }
    _("Starting "+sskPort);
    new SimpleWebServer().start();
  }
  
   // TODO : multiThread multiClient                               
  public void start() throws Exception {
    ServerSocket ssk = new ServerSocket(sskPort);
                                 
   while( !new File("halt").exists() ) {
     Socket sk = null;
     try {              
       _("accepting");
       sk = ssk.accept();
       _("accepted");
      
       BufferedReader reader = new BufferedReader( new InputStreamReader( sk.getInputStream() ) );
       PrintStream ps = new PrintStream( sk.getOutputStream() );  
         
       String contentFileName = "./data/index.html";                          
                                 
                                 
       // read input http connection
       String line;
String host = null;
boolean first = true;
                                 
boolean consolePage = false;
  boolean consolePage_console1 = false;// simple telnet
boolean consolePage_console2 = false;//sh
                                 
                                 
       while( (line = reader.readLine() ) != null) {
 if ( first ) {
  first = false;
  if ( line.indexOf("console.html") > -1 ) {
    contentFileName = "./data/console.html";
    consolePage = true;
  }
  if ( consolePage && line.indexOf("?console1") > -1 ) {
    consolePage_console1 = true;
  } else if ( consolePage && line.indexOf("?console2") > -1 ) {
    consolePage_console2 = true;
  }
 }
if (line.toLowerCase().contains("host:") && host == null) {
host =line.substring("host:".length()).trim();
if ( host.contains(":") ) { host = host.substring(0, host.indexOf(":")); }                                 
_("<H< "+host);
}
         //_("<< "+line);
         if ( line.trim().isEmpty() ) {
           break;
         }
       }
if ( host == null ) { host = "localhost"; }                                 

       //String contentFileName = "./data/index.html";
       File   contentFile     = new File(contentFileName);
       String content = cat(contentFileName);                                   
              
       //===========================================
       content = replaceBy(content, "ws://localhost:", "ws://"+host+":");
                                 
         if (consolePage) {
           if (consolePage_console1) {
             // erase console 2
             content = replaceBy(content, "document.write( new LiteTelnet(9878, true).getConsole() );", "");
           } else if (consolePage_console2) {
             // erase console 1
             content = replaceBy(content, "document.write( new LiteTelnet(9877).getConsole() );", "");
           }
         }
       //===========================================                          
                                 
                                 
       String br = "\r\n";                          
                                 
       Date date = new java.util.Date();
       try {
          date = new Date( contentFile.lastModified());
       } catch (Exception e) {
       }
       String mimeTypeDescriptor = "text/html";
       String serverID = "FUCK-OFF FAKED HTTP MINI SERVER";
                                 
                                 
       ps.print("HTTP/1.1 200 OK"+br+
				"Server: " + serverID + br+
				"Date: " + new java.util.Date() + br+
				"Content-Type: " + mimeTypeDescriptor + br+
				"Content-Location: " + contentFileName + br+
				"Accept-Ranges: bytes" + br+
                "Last-Modified: " + date.toString() + br+
				"Content-Length: " + contentFile.length() + br+ br);
       ps.print(content);
                                 
                                 
      _("sent");
  
     } catch(Exception ex) {
       ex.printStackTrace();
     } finally {
       try { sk.close(); } catch(Exception ex) {}
     }
    }
    try { ssk.close(); } catch(Exception ex) {}
    _("Halting");
  }
    
 static String cat(String filename) throws Exception {
   File f = new File(filename);
   if ( !f.exists() ) { throw new FileNotFoundException(filename); }
   String result = "";
   BufferedReader reader = new BufferedReader( new FileReader(f));
   String line;
   while( (line = reader.readLine()) != null ) {
     result += line +"\n";
   }
   reader.close();
 return result;
 }
                                 
 static void _(Object o) { System.out.println("WWW> "+o); }                          
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

  
