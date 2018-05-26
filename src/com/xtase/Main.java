package com.xtase;

import com.xtase.websandbox.StaticServerManager;
import com.xtase.websandbox.WebServerListener;
import com.xtase.websocket.WebSocketToTelnet;

public class Main {

    protected static String currentBridge = null;

    public static void connectToXtsuBasicViaWifi( String ip ) {
        if ( currentBridge != null && currentBridge.equalsIgnoreCase(ip) ) {
            // notify user !
            System.out.println("########## Server already started ########");
            return;
        }
        new Thread() { public void run() {
        try {
            currentBridge = ip;
            // to Xts-uBASIC WIFI telnetd
            WebSocketToTelnet.main(new String[]{"9878", "23", ip});
            // notify user !
            System.out.println("########## Server closed ########");
            currentBridge = null;
        } catch (Exception ex) {
            currentBridge = null;
            // notify user !
            ex.printStackTrace();
        }
        }}.start();
    }


    public static void main(String[] args) throws Exception {
        StaticServerManager.setListener(new WebServerListener() {

            @Override
            public void toast(Object o) {
                System.err.println("(toast) " + o);
            }

            @Override
            public void onReady() {
                System.out.println("(ready) " + "GO!");
            }
        });

        // connectToXtsuBasicViaWifi("192.168.1.11");
        connectToXtsuBasicViaWifi("127.0.0.1");

        StaticServerManager.start( args != null && args.length > 0 ? args[0] : null );
    }
}
