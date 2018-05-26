package com.xtase.websocket;

import java.io.*;
import java.net.*;
import java.util.*;

public class Launcher {
  
  public Launcher(){}
  
     static void dodo(long milis) { try { Thread.sleep(milis); } catch(Exception ex) {} }                                  

  
  static int sskPort = 8080; 
  
  public static void main(String[] args) throws Exception {
    _("Starting WHOLE ");
                                 
    boolean testPiped = true;                             
                                 
    // WebSocket
    //if (!testPiped) {
                                 new Thread() { public void run() { try {
    WebSocketHandler.main(null);
                                 } catch(Exception ex) { ex.printStackTrace(); } } }.start();
    //}
    // WebServer                             
                                 new Thread() { public void run() { try {
    SimpleWebServer.main(null);
                                 } catch(Exception ex) { ex.printStackTrace(); } } }.start();

    // Telnet Server                             
                                 new Thread() { public void run() { try {
    SimpleTelnetServer.main(null);
                                 } catch(Exception ex) { ex.printStackTrace(); } } }.start();
    dodo(1000L);                                 
    
    if (!testPiped) {
      // TelnetConnector                             
                                 new Thread() { public void run() { try {
      TelnetConnector telnet = new TelnetConnector("localhost", 1025/*1024*/) {
           boolean first = true;
           @Override
           public void handleReceiveText(String msg) throws Exception {
             _("received : "+msg);
             if (first) {
               first = false;
               this.sendText("ls\r\n");
             }
           }
      };
      telnet.handle();
                                 } catch(Exception ex) { ex.printStackTrace(); } } }.start();
    } else {
        // WebSocketToTelnet
                                 new Thread() { public void run() { try {
        // to my telnetd
        WebSocketToTelnet.main(new String[]{"9877", "1025"});
                                 } catch(Exception ex) { ex.printStackTrace(); } } }.start();
                                 new Thread() { public void run() { try {
        // to system telnetd
        WebSocketToTelnet.main(new String[]{"9878", "1024"});
                                 } catch(Exception ex) { ex.printStackTrace(); } } }.start();
    }
  }
                      
  static void _(Object o) { System.out.println("LNH> "+o); }                                 
}

  
