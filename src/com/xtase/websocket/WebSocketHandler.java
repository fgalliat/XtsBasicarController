package com.xtase.websocket;

import java.io.*;
import java.net.*;
import java.util.*;

/*

Small WebSocket standAlone server (but need an http one for Sec-WebSocket-Origin:)
Tested on :
Atrix 3G (android 2.3.4) webtop Firefox6 => WORKS
Atrix 3G (android 2.3.4) internal webkit => DOESN'T WORKS (lookat js wrapper lib, it might be the cause)
PC WindowsXP3 modern Chrome (18.0.1025) => WORKS

STILL HAVE TODO:
- MultiThread MultiClient

*/
  
public class WebSocketHandler {
  
  public WebSocketHandler(){}
  
  static int sskPort = 9876; 
  
  public static void main(String[] args) throws Exception {
    _("Starting "+sskPort);
    new WebSocketHandler().start();
  }
                                 
  protected WebSocket wsk = null;
  
  // TODO : multiThread multiClient                               
  public void start() throws Exception {
    ServerSocket ssk = new ServerSocket(sskPort);
                                 
   while( !new File("halt").exists() ) {
     Socket sk = null;
     try {              
      _("accepting");
      sk = ssk.accept();
      _("accepted");
          
      wsk = new WebSocket(sk, "http://$host$:"+ SimpleWebServer.sskPort, sskPort);
      wsk.handle();
  
      dodo(1000L);
      wsk.sendText("Hello");
                                 
      dodo(1000L);
      // TODO : client Browser receives ONLY 5 1st chars !!
      //wsk.sendText("Duke Version 2 !");
      wsk.sendText("HelloWorldHellBoy");
      _("sent");
      dodo(10000L);
                                 
     } catch(Exception ex) {
       ex.printStackTrace();
     } finally {
       try { wsk.close(); } catch(Exception ex) {}
       // fallback....
       try { sk.close(); } catch(Exception ex) {}
     }
   }
   try { ssk.close(); } catch(Exception ex) {}
   _("Halting");
  }

  protected static Random rnd = new Random();
  static int rnd(int max) { return rnd.nextInt(max); }
    
  static void _(Object o) { System.out.println("WSS> "+o); }                          
  static void dodo(long milis) { try { Thread.sleep(milis); } catch(Exception ex) {} }                                  

}
