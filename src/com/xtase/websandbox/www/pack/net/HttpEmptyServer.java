package com.xtase.websandbox.www.pack.net;

import java.io.File;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.LinkedList;

import com.xtase.websandbox.www.pack.common.data.string.StringUtils;

public class HttpEmptyServer {

    static String br = "\r\n";
    public boolean CLIENT_DEBUG_ALLOWED = true;

    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    // =
    // = =

    public static void main(String[] args) {
        new HttpEmptyServer(80) {

            @Override
            public void HttpRequestReceived(HttpReq req, Cookie cookie) {
                try {
                    req.pipe.printText("World on '" + req.urlRequested
                            + "' :<BR> " + req.queryString.toString() + "<BR> "
                            + (req.isGET() ? "GET" : "POST"));
                    // File img = new File("carto.gif");
                    // req.pipe.sendResponse(HTTP_OK,
                    // img.length(), null, new FileInputStream(img), IMG_GIF);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }.start();
    }

    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    // =
    // = =

    public static boolean verbose = !true;

    public static long now() {
        return System.currentTimeMillis();
    }

    // ##### !! Attention les types inconnus renvoient 404 !! #####
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String IMG_GIF = "image/gif";
    public static final String IMG_JPEG = "image/jpeg";
    public static final String IMG_PNG = "image/png";
    public static final String URL_FORM = "application/x-www-form-urlencoded";

    public static final String[][] HTTP_RESPONSE_TYPE = {{"200", "OK"},
            {"302", "Redirect"}, {"404", "Not Found"},
            {"403", "Not Allowed"}, {"500", "Internal Error"}};

    public static final int HTTP_OK = 0;
    public static final int HTTP_REDIRECT = 1;
    public static final int HTTP_NOT_FOUND = 2;
    public static final int HTTP_NOT_ALLOWED = 3;
    public static final int HTTP_INTERNAL_ERROR = 4;

    // public static final String majServer = "Tue, 13 Sep 2004";
    public static final String majServer = "Wed, 20 Apr 2005";
    public static final String serverID = "Generic Web Server v1.1a";

    public static final String HTTP_SIGNER = "http://";
    public static String CONTENT_LENGTH = "content-length";
    public static String GET_MODE = "get";
    public static String POST_MODE = "post";

    ServerThread server = null;
    public boolean TURBO_MODE = false;

    public HttpEmptyServer(int port) {
        server = new ServerThread(port);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.kill();
    }

    /**
     * to reimplement.
     */
    public void HttpRequestReceived(HttpReq req, Cookie cookie) {
        try {
            req.pipe.printText("Hello World on '" + req.urlRequested
                    + "' :<BR> " + req.queryString.toString() + "<BR> "
                    + (req.isGET() ? "GET" : "POST"));
            // File img = new File("carto.gif"); req.pipe.sendResponse(HTTP_OK,
            // img.length(), null, new FileInputStream(img), IMG_GIF);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // =========================================================================================================

    public void error(Exception ex) {
        ex.printStackTrace();
    }

    public void bindError(Exception ex) {
        ex.printStackTrace();
    }

    public void ready() {

    }

    public/* static */void err(Object o) {
        System.err.println("" + o);
    }

    // =========================================================================================================

    public class ServerThread extends Thread {
        int port = -1;
        boolean inRun = false;
        LinkedList<ClientThread> connecteds = new LinkedList<ClientThread>();
        ServerSocket ssk = null;

        public ServerThread(int port) {
            System.out.println("start " + port);
            this.port = port;
        }

        @Override
        public void run() {
            inRun = true;// new
            while (inRun) {// new

                try {
                    ssk = new ServerSocket(port);
                } catch (Exception ex) {
                    bindError(ex);
                    inRun = false;
                    break;
                }
                ready();

                Socket newOne = null;
                ClientThread newClt = null;
                inRun = true;
                while (inRun) {
                    try {
                        newOne = ssk.accept();
                        newClt = new ClientThread(this, newOne);
                        connecteds.add(newClt);
                        newClt.start();
                    } catch (Exception ex) {
                        try {
                            newClt.kill();
                        } catch (Exception ex0) {
                        }
                        try {
                            newOne.close();
                        } catch (Exception ex0) {
                        }
                        ex.printStackTrace();
                    }

                    try {
                        sleep(TURBO_MODE ? 50 : 100);
                    } catch (Exception ex) {
                    } // bettween 2 clients
                }
                // inRun = false;
                try {
                    ssk.close();
                } catch (Exception ex) {
                }

            }
            inRun = false;
        }

        public void kill() {
            inRun = false;
            try {
                ssk.close();
            } catch (Exception ex) {
            }
        }
    }

    public class ClientThread extends Thread {
        Socket sk = null;
        ServerThread parent = null;
        OutputStream out = null;
        InputStream in = null;

        Cookie cookie = null;

        public ClientThread(ServerThread parent, Socket sk) {
            this.parent = parent;
            this.sk = sk;
        }

        public synchronized void kill() {
            try {
                parent.connecteds.remove(this);
            } catch (Exception ex) {
                System.err.println("(!!) HttpEmptyServer::kill() " + ex.toString());
            }

            try {
                out.close();
            } catch (Exception ex) {
            }

            try {
                sk.close();
            } catch (Exception ex) {
            }
        }


// tmp try @ 31/03/2017
// BufferedReader reader = null;

        protected String readLine(InputStream in) throws Exception {
            int ch;
            String str = "";
    /* -- removed @ 26/05/2018
			// since 31/03/2017
			if ( reader == null ) {
				reader = new BufferedReader( new InputStreamReader( in ) );
			}
			try {
			  return reader.readLine();
			} catch(Exception ex) {
			  return null;	
			}
			// =================
	*/
            // -- old safest method --
            while ((ch = in.read()) != -1) {
                if (ch == 10) {
                    break;
                }
                if (ch == 13) {
                    continue;
                }
                str += (char) ch;
            }
            return ch == -1 ? null : str;//.trim();
            //
        }

        @Override
        public void run() {
            try {
                out = sk.getOutputStream();
                in = sk.getInputStream();
                Cookie localCookie = null;
                HttpReq req = new HttpReq(this);
                String line;
                String firstHeaderLine = null;
                boolean onFirstLine = false;
                while ((line = readLine(in)) != null) { // HTTP HEADERS
                    if (line.equals("")) {
                        break;
                    }
                    onFirstLine = false;
                    // System.out.println( "## "+ line );

                    if (line.startsWith("User-Agent")) {
                        System.out.println("--|" + line + "|--|");
                    } else {
                        // System.out.println( "## "+ line );
                    }

                    boolean tmpInGet = false;
                    if (req.mode == null
                            && ((tmpInGet = line.toLowerCase().startsWith(
                            GET_MODE + " ")) || line.toLowerCase()
                            .startsWith(POST_MODE + " "))) {
                        req.mode = tmpInGet ? GET_MODE : POST_MODE;
                        firstHeaderLine = line;
                        onFirstLine = true;
                    } else if (req.contentLength == -1
                            && line.toLowerCase().startsWith(CONTENT_LENGTH)) { // !!
                        // bourin

                        // System.out.println("> POST-BODY => "+line+" bytes STR.");

                        req.contentLength = Integer.parseInt(StringUtils.split(
                                line, " ")[1].trim());

                        // System.out.println("> POST-BODY => "+req.contentLength+" bytes.");
                    }

                    if (!onFirstLine && line.indexOf(":") != -1) {
                        String[] tokens = StringUtils.cutFirst(line, ":");
                        tokens[0] = tokens[0].trim();
                        tokens[1] = tokens[1].trim();
                        req.serverVariables.put(tokens[0], tokens[1]);

                        if (tokens[0].equalsIgnoreCase("cookie")) {
                            localCookie = Cookie.fromString(tokens[1]);
                        }

                        // err("--|"+ tokens[0] +"|--|"+ tokens[1] +"|--");
                    } else {
                    }
                }

                // ----------------------- dump URI href
                String uri = null;
                try {
                    uri = firstHeaderLine.substring(req.isGET() ? 4 : 5,
                            firstHeaderLine.toUpperCase().indexOf("HTTP/") - 1)
                            .trim();
                } catch (Exception ex) {
                    System.err.println("(!!) uri>> " + firstHeaderLine);
                }

                // System.err.println("(ii) uri>> " + uri);


                if (uri == null) {
                    System.out.println("== KILL REQUEST ==");
                    kill();
                    return;
                }

                int qsPos = uri.indexOf("?");
                req.urlRequested = qsPos == -1 ? new String(uri) : uri
                        .substring(0, qsPos);
                if (req.urlRequested.startsWith(HTTP_SIGNER)) {
                    req.urlRequested = req.urlRequested.substring(HTTP_SIGNER
                            .length());
                }

                if (verbose) {
                    System.err.println(req.urlRequested);
                }

                // ----------------------- dump content

                String params = null;
                // err("<ContentLen=" + req.contentLength);
                // if (req.contentLength < 0) {
                if (req.isGET()) {
                    req.contentLength = 0;
                    if (qsPos > -1) {
                        params = uri.substring(qsPos + 1);
                        // err("<params=" + params);
                    }
                } else {
                    params = "";
                    // if (req.contentLength > 0) {

                    // since 31/03/2017
                    //byte[] datas = new byte[4 * 1024];
                    byte[] datas = new byte[128 * 1024];

                    int totalReaded = 0;
                    int readed = 0;

                    // System.out.println("##>>>> "+in.available());

                    while ((readed = in.read(datas)) > 0) {

                        // System.out.println("## "+readed);

                        params += new String(datas, 0, readed);
                        totalReaded += readed;
                        if (totalReaded >= req.contentLength) {
                            break;
                        }
                    }
                    // System.out.println("## "+readed);
                    // }
                    params = params.trim();
                    // err("<params=" + params);
                }

                // System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$");
                // System.out.println( "" + params );
                // System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$");

                if (params != null && params.length() > 0) {
                    req.queryString = new QueryString(params);
                }

                HttpRequestReceived(req, localCookie);

            } catch (Throwable ex) {
                if (CLIENT_DEBUG_ALLOWED) {
                    ex.printStackTrace();
                }
                error(new Exception(ex));
            }
            kill(); // si pas kill, IE ne fini pas de charger ???? - a verif
        }

        // ============== ANSWER ROUTINES =================
        public void printText(String str) throws Exception {
            printText(str, true);
        }

        public void printText(String str, boolean flush) throws Exception {
            try {
                out.write(str.getBytes("ISO-8859-1"));
                if (flush) out.flush();
            } catch (Exception ex) {
                // System.err.println("Couldn't send '"+ str +"'");
            }
        }

        public void netPrintln(String str) throws Exception {
            netPrintln(str, true);
        }

        public void netPrintln(String str, boolean flush) throws Exception {
            printText(str + br, flush);
        }

        public void sendResponse(int responseCode, long length,
                                 String extraHead, String file, InputStream content,
                                 String mimeTypeDescriptor) throws Exception {
            byte[] data = null;
            if (content != null) {

                data = new byte[(int) length];
                content.read(data, 0, data.length);

//				ReadableByteChannel rc = java.nio.channels.Channels.newChannel( content );
//				java.nio.ByteBuffer buff = java.nio.ByteBuffer.allocate/*Direct*/( (int)length );
//				int len = rc.read(buff);
//				data = buff.array();

//				byte[] buff = new byte[96 * 1024];
//
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				int readed;
//
//				while ((readed = content.read(buff, 0, buff.length)) > 0) {
//					baos.write(buff, 0, readed);
//				}
//
//				data = baos.toByteArray();
//
//				if (mimeTypeDescriptor.equals("application/pdf")) {
//					System.out.println("" + length + " : " + data.length);
//				}
//
//				// data = new byte[(int) length];
//				// content.read(data, 0, data.length);
            }
            try {
                content.close();
            } catch (Exception ex) {
            }

            sendResponse(responseCode, length, extraHead, file, data,
                    mimeTypeDescriptor);
        }

        public void sendResponse(int responseCode, String extraHead,
                                 String resp, String mimeTypeDescriptor) throws Exception {

            byte[] data = resp == null ? new byte[0] : resp.getBytes();

            sendResponse(responseCode, data.length, extraHead, null, data,
                    mimeTypeDescriptor);

        }

        public void sendResponse(int responseCode, long length,
                                 String extraHead, String file, byte[] data,
                                 String mimeTypeDescriptor) throws Exception {
            boolean autoFlush = false;

            netPrintln("HTTP/1.1 " + HTTP_RESPONSE_TYPE[responseCode][0] + " "
                    + HTTP_RESPONSE_TYPE[responseCode][1], autoFlush);
            if (data != null) { // cf redirect
                StringBuffer respHead = new StringBuffer();
                respHead.append("Server: " + serverID + br);
                respHead.append("Date: " + new java.util.Date() + br);
                respHead.append("Content-Type: " + mimeTypeDescriptor + br);
                respHead.append("Content-Location: " + file + br);
                respHead.append("Accept-Ranges: bytes" + br);

                Date date = new java.util.Date();
                try {
                    date = new Date(new File(file).lastModified());
                } catch (Exception e) {
                }

                respHead.append("Last-Modified: " + date.toString() + br);
                respHead.append("Content-Length: " + length + br);

                if (cookie != null) {
                    respHead.append("Set-Cookie: " + cookie.toString() + br);
                }

                // respHead.append("Content-Length: " + data.length);

                // if ( mimeTypeDescriptor.equals("application/pdf") ) {
                // //System.out.println(""+length+" : "+data.length);
                // respHead.append("Accept-Ranges: bytes");
                // respHead.append("ETag: W/\"338492-1304588534795\"");
                // // taille
                //
                // }

                String str = respHead.toString();
                str = str.substring(0, str.length() - br.length());
                netPrintln(str, autoFlush);
            }
            if (extraHead != null) {
                netPrintln(extraHead, autoFlush);
            }
            netPrintln("", autoFlush);
            //out.flush();
			/*
			 * byte[] data = new byte[(int)length]; content.read(data, 0,
			 * data.length);
			 */

            // int i=0,len=4 * 1024;
            // for(i=0; i < data.length; i+= 4 * 1024) {
            // if ( i + len > data.length ) {
            // len = data.length - i;
            // }
            // out.write(data, i, len); // even if it could not read all..
            // out.flush();
            // }

            out.write(data, 0, data.length); // even if it could not read all..
            // out.write(0);
            out.flush();

            // if ( mimeTypeDescriptor.equals("application/pdf") ) {
            // try { out.close(); } catch(Exception ex) {}
            // }

            try {
                Thread.sleep(TURBO_MODE ? 10/*20*/ : 300);
            } catch (Exception e) {
            }
        }

        public void setCookie(Cookie cookie) {
            this.cookie = cookie;
        }

    } // ------- end of ClientThread class def. -----------------------------

    // Content-Length
    public class HttpReq {
        public int contentLength = -1;
        public String mode = null;
        public String body = null;
        public QueryString queryString = new QueryString();
        public QueryString serverVariables = new QueryString(); // referer ....
        public String urlRequested = null;
        public ClientThread pipe = null;

        public HttpReq(ClientThread pipe) {
            this.pipe = pipe;
        }

        public boolean isGET() {
            return mode == GET_MODE;
        } // !!!! ==
    }

    // #####################################################""

    // protected static Map<String,WebSession> sessions = new
    // HashMap<String,WebSession>();
    // protected static class WebSession {
    // }

}