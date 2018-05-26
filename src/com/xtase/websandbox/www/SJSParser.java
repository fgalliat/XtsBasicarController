package com.xtase.websandbox.www;


import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xtase.websandbox.www.pack.net.HttpEmptyServer;

/**
 * Parser de fichier '.SJS'<BR>
 * <B>NB:</B> Les fichiers SJS sont des fichiers de script JavaScript coted serveur
 *
 * @author Franck Galliat
 */

public class SJSParser {

    /**
     * le contenu Html de la page (celui qui sera affiched)
     */
    String pageContentHtml = "";
    /**
     * le Parser Javascript - voir (./lib/js/JS.JAR)
     */
    JavascriptParser jsParser = null;
    /**
     * activer le debugage ?
     */
    boolean trace = !true;
    /**
     * le type mime du document retourned apres execution (par defaut 'text/html')
     */
    String mimeType = "text/html";

    // ---------------- document.* routines --------------------------------------
    /**
     * entetes additionnels
     */
    String extraHead = null;

    /**
     * -> document.write()
     */
    public void write(String str) {
        pageContentHtml += str;
    }

    /**
     * -> document.writeln()
     */
    public void writeln(String str) {
        pageContentHtml += str + "<BR>";
    }

    /**
     * -> document.setContentType() fixe le type mime de sortie
     */
    public void setContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * -> document.addHeader() ajoute une entete Http au document
     */
    public void addHeader(String header) {
        if (extraHead == null) {
            extraHead = "" + header;
        } else {
            extraHead += "\n" + header;
        }
    }
    // ---------------- document.* routines --------------------------------------

    /**
     * l'instance du WebServer (utilised pour le controle des Sessions)
     */
    protected WebServer server = null;


    /**
     * methode d'execution du script
     *
     * @param kernel l'instance de Kernel (utilised pour avoir acces au Controllers & aux Providers)
     * @param uri    l'URL du script
     * @param server l'instance du WebServer (utilised pour le controle des Sessions)
     * @param req    la requette Http associede
     * @throws Exception
     */
    public void exec(String uri, WebServer server, HttpEmptyServer.HttpReq req) throws Exception {

        //System.out.println( (char)7 );
        //System.out.println( "--->"+uri+"<--" );

        this.server = server;
        //long t0exec, t1exec;
        //t0exec = now();
        jsParser = new JavascriptParser(null);
        jsParser.addReference("document", this);
        jsParser.addReference("HttpRequest", req);
        jsParser.addReference("WebServer", server);

        //jsParser.addReference("context", AndroWebServerActivity.getInstance());

        try {
            String content = server.getDocumentRoot().getContent(uri);
            String sourceCode = getPageCode(content, uri);

            // System.out.println("------------------------------------------");
            // System.out.println(sourceCode);
            // System.exit(0);

            if (trace) {
                try {
                    PrintStream sourceOut = new PrintStream(new FileOutputStream("trace.log.txt"));
                    sourceOut.println(sourceCode);
                    sourceOut.flush();
                    sourceOut.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            try {
                jsParser.evaluate(sourceCode);
            } catch (Exception ex) {
                pageContentHtml += "<DIV style='background-color:red;color:white;font-family=verdana,helvetica,sans-serif'>" + ex.toString() + "</DIV>";
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            pageContentHtml += "<DIV style='background-color:red;color:white;font-family=verdana,helvetica,sans-serif'>" + ex.toString() + "</DIV>";
            ex.printStackTrace();
        }
        jsParser.dispose();
        //t1exec = now();

        //req.pipe.printText( pageContentHtml );
        //String extraHead = null;

//dynamical mimeType
        if (uri.toLowerCase().contains(".css.")) {
            mimeType = "text/css";
        } else if (uri.toLowerCase().contains(".js.")) {
            mimeType = "text/javascript";
        }

        req.pipe.sendResponse(HttpEmptyServer.HTTP_OK, extraHead, pageContentHtml, mimeType);
    }

    // -------------------------------------------------------------------------------------------

    /**
     * renvoie le code Javascript contenu dans la page definie par content<BR>
     * uri est l'url du script
     */
    public String getPageCode(String content, String uri) throws Exception {
        String enterTag = "<?";
        String exitTag = "?>";
        String sourceCode = "";
        String pageContent = "" + content;
        int posNext;
        String cleanFrag, source;
        while (true) {
            posNext = pageContent.indexOf(enterTag);
            if (posNext == -1) {
                posNext = pageContent.length();
            }
            cleanFrag = pageContent.substring(0, posNext);
            if (cleanFrag.length() > 0) {
                sourceCode += "document.write(\"";
                sourceCode += replaceBy(replaceBy(replaceBy(replaceBy(cleanFrag, "\\", "\\\\"), "\r", " "), "\n", "\\n"), "\"", "\\\"");
                sourceCode += "\");\n";
            }
            pageContent = pageContent.substring(posNext);
            if (pageContent.length() == 0) break;

            posNext = pageContent.indexOf(exitTag);
            if (posNext == -1) {
                throw new Exception("UnBalanced Embedded Tag");
            }
            source = pageContent.substring(enterTag.length(), posNext);
            if (source.startsWith("=")) {    // <?= ... ?>
                sourceCode += "document.write(" + source.substring(1).trim() + ");\n";
            } else {
                // gestion de la directive '#include:<...>#'
                int incStart, incStop;
                String left, right, between;
                while ((incStart = source.indexOf("#include:")) > -1) {
                    incStop = source.indexOf("#", incStart + 1);
                    left = source.substring(0, incStart);
                    right = source.substring(incStop + "#".length());
                    between = source.substring(incStart, incStop + "#".length());
                    between = between.substring(between.indexOf(":") + 1, between.length() - "#".length()).trim();

                    //String content = server.getDocumentRoot().getContent(uri);
                    String includeUri = getParentPath(uri) + between;
                    String tmp = server.getDocumentRoot().getContent(includeUri);
                    if (tmp == null) throw new Exception("Include Introuvable (" + includeUri + ")");
                    String includeContent = getPageCode(tmp, includeUri);

                    source = left + includeContent + right;
                }
                // --------------------------------------------

                sourceCode += source.trim() + "\n";
            }
            pageContent = pageContent.substring(posNext + exitTag.length());
            if (pageContent.length() == 0) break;
        }
        return (sourceCode);
    }

    // -------------------------------------------------------------------------------------------

    /**
     * renvoie le chamin parent d'un chemin defini par 'str'
     */
    public static String getParentPath(String str) {
        int l = str.lastIndexOf("/");
        if (l == -1) return ("");
        return (str.substring(0, l + 1));
    }

    /**
     * renvoie le temps en millisecondes
     */
    public static long now() {
        return (System.currentTimeMillis());
    }

    /**
     * remplace 'oldE' dans la chaine 'str', par 'newE'
     */
    public static String replaceBy(String str, String oldE, String newE) {
        if (str.indexOf(oldE) == -1)
            return (str);    // 1ere mesure a prendre pour ne pas parcourir tt le tableau inutilement

        String regularExpression = Pattern.quote(oldE);
        String replacement = Matcher.quoteReplacement(newE);

        String retBuffer = str.replaceAll(regularExpression, replacement);


//
//  // indexOf etant implemented a peut pres pareil, il ne sert a rien de l'utiliser pour booster le code !
//  String retBuffer = "";
//  char[] toReplace = oldE.toCharArray();
//  //char[] byReplace = newE.toCharArray();
//
//
//  // new !!!!! trash - cf bug dur dernier char s'il correspond au premier char de la recherche
//  char[] support   = (str+ ( toReplace[0] == '.' ? "*" : "." ) ).toCharArray();
//
//
//  int found = 0;
//  int i=0;
//  for(; i < support.length; i++) {
//   if( support[i] == toReplace[found] ) {
//    found++;
//    if (found == toReplace.length ) { retBuffer += newE; found = 0; }
//   }
//   else { i -= found; found = 0; retBuffer += support[i]; }
//  }
//
//  // new !!!!! trash
//  retBuffer = retBuffer.substring( 0, retBuffer.length() -1  );

        return (retBuffer);
    }

}