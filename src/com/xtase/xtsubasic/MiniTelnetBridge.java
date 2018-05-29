package com.xtase.xtsubasic;

import com.xtase.file.FileUtils;

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

    protected boolean pocketUploadFile(File src, String dstName) {
        try {
            int size = (int) src.length();

            byte[] buff = new byte[size];
            FileInputStream fis = new FileInputStream(src);
            fis.read(buff);
            fis.close();

            pocketWrite("EXEC \"WIFI\",\"UPLOAD\" \n");
            Zzz(100);
            pocketWrite(dstName + "\n");
            pocketWrite("" + size + "\n");
            Zzz(300);

            espOut.write(buff, 0, size);
            espOut.flush();

            String line;
            while ((line = pocketReadline()) != null) {
                if (line.equalsIgnoreCase("-EOF-")) {
                    break;
                }
            }
            _("Client received the file");

            pocketWrite("\n");
            Zzz(300);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }


    // ==============================

    boolean listenLocked = false;

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
                        if (!isPocketConnected() || listenLocked) {
                            Zzz(300);
                        } else {
                            String answer = pocketReadline(); // beware w/ that (on real xtsPck)
                            System.out.println("$$ " + answer + " $$");
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
                } else if (line.startsWith("/upload ")) {
                    listenLocked = true;
                    // ONLY upload PRGMs @ this time.....
                    // no ext provided

                    String entryName = line.substring("/upload ".length()).trim();

                    if (entryName.contains(".")) {
                        out.println("Sorry only PRGMs are allowed for now");
                    } else {
                        out.println("Ok, let try to upload " + entryName + "...");

                        // TMP : use config file !!!!!!!!!
                        File srcRoot = new File("/vm_mnt/devl/BASIC/XtsBasicarController/www/data/");

                        // BEWARE : case sensitive !
                        File src = new File(srcRoot, entryName + ".TXT");

                        File toSend = null;

                        if (src.exists()) {
                            String parsedSource = PreProcessor.decode(FileUtils.cat(src.getAbsolutePath()));
                            toSend = new File(srcRoot, entryName + ".BAS");
                            FileUtils.write(toSend.getAbsolutePath(), parsedSource+"\n");
                        } else {
                            src = new File(srcRoot, entryName + ".BAS");
                            if (src.exists()) {
                                toSend = new File(srcRoot, entryName + ".BAS");
                            } else {
                                toSend = null;
                            }
                        }

                        if (toSend == null) {
                            out.println("File not found !!");
                        } else {
                            boolean ok = pocketUploadFile(toSend, entryName.toUpperCase() + ".BAS");
                            out.println("success : " + ok);
                        }

                    }
                    listenLocked = false;
                } else if (line.equals("/kill")) {
                    kill = true;
                    break;
                } else {
                    System.out.println("> " + line);
                    pocketWrite(line + "\n");
                }

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

