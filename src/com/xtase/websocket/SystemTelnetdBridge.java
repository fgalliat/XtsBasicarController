package com.xtase.websocket;

import java.io.*;
import java.net.*;
import java.util.*;


/*
Main class for SystemTelnetdBridge system

TODO:
-C alt_config_file

*/

public class SystemTelnetdBridge {
  
  public SystemTelnetdBridge(){}
  
  protected int htmlPort = 8080;
  protected int telnetdPort = 1024;
  protected int webSocketPort = 9878;
     
  public static void main(String[] args) throws Exception {
    new SystemTelnetdBridge().run(args);
  }
                    
  public void run(String[] args) throws Exception {
    _("Starting SystemTelnetdBridge ");
                    
   String defaultProperties = "./config.properties";
   String usedProperties = defaultProperties;
                                 
   if ( args != null && args.length > 0 ) {
      if ( args[0].equals("-C") && args.length > 1 ) {
        usedProperties = args[1];
      } else {
        _("Useage : ./xxx.sh [-C alt_config_file]");
      }
   }

   try {
     Properties props = new Properties();
     props.load( new FileInputStream( usedProperties ) );
     try { htmlPort = Integer.parseInt( props.getProperty("htmlPort") ); } catch(Exception ex) {}
     try { telnetdPort = Integer.parseInt( props.getProperty("telnetdPort") ); } catch(Exception ex) {}
     try { webSocketPort = Integer.parseInt( props.getProperty("webSocketPort") ); } catch(Exception ex) {}
   } catch(Exception ex) {
     _("Error while reading config file");
   }

    _("www:"+htmlPort);
    _("tld:"+telnetdPort);
    _("wss:"+webSocketPort);

    // /data/data/com.spartacusrex.spartacuside/files/system/bin/bash
    if (! exec("utelnetd -p "+telnetdPort+" -d -l /bin/sh") ) {
      _("Could not launch utelnetd on :"+telnetdPort+" maybe it's already launch");
    } else {
      _("started utelnetd on :"+telnetdPort+"");
    }
    
    
    // TODO beware with console selection cf: replaceBy(..."document.write( new LiteTelnet(9878, true).getConsole() );"
    // + automatically set the config's default port in page
    // should make another spe page
    // WebServer                             
    new Thread() { public void run() { try {
      SimpleWebServer.main(new String[]{""+htmlPort});
    } catch(Exception ex) { ex.printStackTrace(); } } }.start();

    
    // WebSocketToTelnet
    new Thread() { public void run() { try {
        // to system telnetd
        WebSocketToTelnet.main(new String[]{""+webSocketPort, ""+telnetdPort});
    } catch(Exception ex) { ex.printStackTrace(); } } }.start();
    
  }

  static boolean exec(String cmd) {
    try {
      Process ps = Runtime.getRuntime().exec(cmd);
      int rc = ps.waitFor();
      return rc == 0;
    } catch(Exception ex) {
      _("(EE) error while launching : "+cmd);
      return false;
    }
  }
  
  static void _(Object o) { System.out.println("STB> "+o); } 
  static void dodo(long milis) { try { Thread.sleep(milis); } catch(Exception ex) {} }                                  
}

  
