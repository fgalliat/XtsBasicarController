package com.xtase.xtsubasic;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * Telnet server that connects to Xts-Pocket daemon
 */

public class MiniTelnetBridge {

    public static void main(String[] args) throws Exception {
        new MiniTelnetBridge().doWork(23);
    }

    // ==============================
    protected Socket espSk = null;
    protected boolean espConn = false;
    protected InputStream espIn = null;
    protected OutputStream espOut = null;
    protected String espHost = null;


    protected boolean isPocketConnected() {

        if (espSk != null && espSk.isClosed()) {
            espConn = false;
        }

        return espConn;
    }

    protected boolean connectToPocket(String host) {
        try {

            espSk = new Socket(host, 23);
            this.espHost = host;
            this.espIn = espSk.getInputStream();
            this.espOut = espSk.getOutputStream();
            this.espConn = true;

            // Greeting sequence .....
            pocketWrite("Hello \n");
            // pocketReadline();

            return true;
        } catch (Exception ex) {
            this.espConn = false;
            ex.printStackTrace();
            return false;
        }
    }

    protected void disconnectFromPocket() {
        //if (!this.isPocketConnected()) {
        //    return;
        //}
        try {
            this.espOut.write("/quit\n".getBytes());
            this.espOut.flush();

            this.espSk.close();

            // this.espConn = false;
        } catch (Exception ex) {
        }
        this.espSk = null;
        this.espConn = false;
    }

    protected boolean pocketWrite(String str) {
        try {
            this.espOut.write(str.getBytes());
            this.espOut.flush();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    protected String pocketReadline() {
        try {
            String str = "";

            int ch;
            while (true) {
                ch = espIn.read();
                if (ch == -1) {
                    break;
                } else if (ch == '\r') {
                    continue;
                } else if (ch == '\n') {
                    break;
                } else {
                    str += (char) ch;
                }
            }

            if (str.length() == 0 && ch == -1) {
                return null;
            }

            return str;
        } catch (Exception ex) {
            return null;
        }
    }

    // ==============================


    public void doWork(int port) throws Exception {
        ServerSocket ssk = new ServerSocket(port);
        _("listening on :" + port);

        boolean kill = false;
        while (!kill) {
            Socket sk = ssk.accept();
            _("connected on :" + port);

            final BufferedReader in = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            final PrintStream out = new PrintStream(sk.getOutputStream());

            new Thread() {
                public void run() {
                    while (true) {
                        if (!isPocketConnected()) {
                            Zzz(300);
                        } else {
                            String answer = pocketReadline(); // beware w/ that (on real xtsPck)
                            System.out.println( "$$ "+answer+" $$" );
                            out.println(">" + answer);
                        }
                    }
                }
            }.start();


            out.println("Hi, please connect to XtsPocket");
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("/connect ")) {
                    String espHost = line.trim();
                    espHost = espHost.substring("/connect ".length()).trim();
                    out.println("Ok, let try to connect " + espHost + ":23...");

                    connectToPocket(espHost);

                } else if (line.equals("/quit")) {
                    disconnectFromPocket();
                    // break;
                } else if (line.equals("/kill")) {
                    kill = true;
                    break;
                }
                System.out.println("> " + line);

                pocketWrite(line + "\n");

            }

            sk.close();
        }

        ssk.close();
    }


    static void _(Object o) {
        System.out.println(o);
    }

    static void Zzz(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception ex) {
        }
    }
}

