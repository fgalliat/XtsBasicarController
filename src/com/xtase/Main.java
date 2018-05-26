package com.xtase;

import com.xtase.websandbox.StaticServerManager;
import com.xtase.websandbox.WebServerListener;

public class Main {

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

        StaticServerManager.start( args != null && args.length > 0 ? args[0] : null );
    }
}
