package com.xtase.websocket;

import java.io.*;
import java.net.*;
import java.util.*;

/*
WebSocket to Telnet bridge

STILL HAVE TODO:
- MultiThread MultiClient

*/
  
public class WebSocketToTelnet {
  
  public WebSocketToTelnet(){}
  
  //static int sskPort = 9877; 
  
  public static void main(String[] args) throws Exception {
    int sskPort = 9877;
    if (args != null && args.length > 0) {
     sskPort = Integer.parseInt(args[0]);
    }
    int telnetPort = 1025; // mine
    if (args != null && args.length > 1) {
     telnetPort = Integer.parseInt(args[1]);
    }
    String telnetHost = "127.0.0.1";
      if (args != null && args.length > 2) {
          telnetHost = args[2];
      }

    _("Starting WebSocketToTelnet:"+sskPort+" => "+ telnetHost +":"+telnetPort);
    new WebSocketToTelnet().start(sskPort, telnetHost, telnetPort);
  }
                                 
  protected WebSocket wsk = null;
  protected TelnetConnector tsk = null;
  
  // TODO : multiThread multiClient                               
  public void start(int sskPort, String telnetHost, int telnetPort) throws Exception {
    ServerSocket ssk = new ServerSocket(sskPort);
                                 
   while( !new File("halt").exists() ) {
     Socket sk = null;
     try {              
      _("accepting");
      sk = ssk.accept();
      _("accepted");
      
      tsk = new TelnetConnector(telnetHost, telnetPort) {
        @Override
        public void handleReceiveText(String txt) throws Exception {
          //_("received from telnetd : "+txt+" send it to webSocket browser client");
          
          // CHUNKY TO NOT SEND A 0x88
          /*String tmp = "";
          for(int i=0; i < txt.length(); i++) {
            char ch = txt.charAt(i);
            if ( ch != 0x88 ) { tmp += ch; }
          }
          txt = tmp;*/
          // CHUNKY ........
          
          wsk.sendText(txt);
        }

        @Override
        public void handleClose() {
          _("Closing Socket");
          // if ... wsk.close();
          wsk.close();
        }
      };
                                 
      wsk = new WebSocket(sk, "http://$host$:"+ SimpleWebServer.sskPort, sskPort) {
        @Override
        public void handleReceiveText(String txt) throws Exception {
          //_("received from webclient : "+txt+" send it to telnet");
          _("< "+txt);
          tsk.sendText(txt);
        }
                                 
        @Override
        public void handleClose() {
          _("Closing Socket");
          // if ... tsk.close();
          //try { wsk.close(); } catch(Exception ex) {}                       
          //try { sk.close(); } catch(Exception ex) {}
        }
      };
        
      wsk.handle();
      tsk.handle();
      //wsk.handle();
  
      /*dodo(1000L);
      wsk.sendText("Hello");
                                 
      dodo(1000L);
      // TODO : client Browser receives ONLY 5 1st chars !!
      //wsk.sendText("Duke Version 2 !");
      wsk.sendText("HelloWorldHellBoy");
      _("sent");
      dodo(10000L);*/
                                 
     } catch(Exception ex) {
       ex.printStackTrace();
     } finally {
       /*try { wsk.close(); } catch(Exception ex) {}
       // fallback....
       try { sk.close(); } catch(Exception ex) {}*/
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
