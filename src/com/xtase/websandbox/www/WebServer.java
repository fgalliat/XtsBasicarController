package com.xtase.websandbox.www;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import com.xtase.websandbox.www.pack.file.SystemDir;
import com.xtase.websandbox.www.pack.net.Cookie;
import com.xtase.websandbox.www.pack.net.HttpEmptyServer;

public class WebServer extends HttpEmptyServer {

	SystemDir documentRoot = null;

	String INDEX_PAGE = "index.html";
	String INDEX_PAGE_2 = "index.sjs";
	String INDEX_PAGE_3 = "index.sja";

	public final String EXEC_JS = "server/jsApplication";
	public final String EXEC_JA = "server/jaApplication";

	String[][] mimeTypes = { { "txt", TEXT_PLAIN }, { "css", "text/css" },
			{ "pdf", "application/pdf" }, { "js", "text/javascript" },
			{ "html", TEXT_HTML }, { "gif", IMG_GIF }, { "jpg", IMG_JPEG },
			{ "jpeg", IMG_JPEG }, { "png", IMG_PNG }, { "xxx", URL_FORM },
			{ "sjs", EXEC_JS }, { "sja", EXEC_JA } };

	boolean inRun = false;

public static boolean AUTH_COOKIE_DISABLED = false;


	public WebServer(int port, String root) throws Exception {
		super(port);
		documentRoot = new SystemDir(root);
		inRun = true;
		new Thread() {
			@Override
			public void run() {
				while (inRun) {
					try {
						sleep(sessionTimeOut / 2);
					} catch (Exception ex) {
					}
					checkAllSessions();
				}
			}
		}.start();
	}

	// ----------------------------------------
	Map<String, Session> sessions = new HashMap<String, Session>();
	long SECOND = 1000L;
	long MINUTE = 60L * SECOND;
	long sessionTimeOut = 5 * MINUTE;

	protected void checkAllSessions() {
		long t0 = System.currentTimeMillis();
		// Enumeration enum0 = sessions.elements();
		// Session sess;
		// while (enum0.hasMoreElements()) {
		// sess = (Session) enum0.nextElement();
		for (Session sess : sessions.values()) {
			if (t0 > sess.sessTime + sessionTimeOut) {
				ejectSession(sess);
			}
		}
	}

	protected void ejectSession(Session sess) {
		System.err.println("Ejecting WebAdmin Session : " + sess.key);
		sessions.remove(sess.key);
		sess = null;
	}

	public String createSession(String login, String pass) {
		if (login != null && pass != null && login.equals("fgalliat")
				&& pass.equals("fdick")) {
			long time = System.currentTimeMillis();
			String key = "" + time;
			Session session = new Session(login, pass, key);
			session.sessTime = time;
			sessions.put(key, session);
			return (key);
		} else {
			return (null);
		}
	}

	/** verify & refresh session */
	public boolean verifySession(String key) {
		if (key == null) {
			return false;
		}
		boolean found = sessions.containsKey(key);
		if (found) {
			refreshSession(key);
		}
		return found;
	}

	public void refreshSession(String key) {
		Session session = (Session) sessions.get(key);
		session.sessTime = System.currentTimeMillis();
	}

	public class Session {
		String login = null;
		String pass = null;
		String key = null;
		long sessTime = -1L;

		public Session(String login, String pass, String key) {
			this.login = login;
			this.pass = pass;
			this.key = key;
		}
	}

	// ----------------------------------------

	public void addAlias(String source, String dest) throws Exception {
		documentRoot.addAlias(source, dest, false);
	}

	public void addAbsoluteAlias(String source, String dest) throws Exception {
		documentRoot.addAlias(source, dest, true);
	}

	public boolean isLocalhost(String host) {
		return host != null
				&& (host.contains("localhost") || host.contains("127.0.0.1"));
	}

	@Override
	public void HttpRequestReceived(HttpReq req, Cookie cookie) {
		try {
			// req.pipe.printText("World on '"+ req.urlRequested +"' :<BR> "+
			// req.queryString.toString() +"<BR> "+ (req.isGET() ? "GET" :
			// "POST") );
			// File img = new File("carto.gif"); req.pipe.sendResponse(HTTP_OK,
			// img.length(), null, new FileInputStream(img), IMG_GIF);
			String uri = req.urlRequested;

// System.out.println(uri);

			String parsedUri = documentRoot.getFile(uri);

			// ------------- Cookie Managment -----------
			boolean cookieValid = false;
			if (cookie == null || cookie.get("auth") == null
					|| cookie.get("key") == null) {
				cookie = new Cookie();
				cookie.set("auth", false);
				cookie.set("key", -1);

				cookieValid = false;
			}

			String cookieKey = cookie.get("key");
			// err("==============>>" + new Date() + "<<==============");
			// err("COOKIE CONTENT = " + cookie.toString());
			// err("COOKIE KEY = " + cookieKey);
			cookieValid = verifySession(cookieKey);
			
if ( AUTH_COOKIE_DISABLED ) {
	cookieValid = true;
}
			

			req.pipe.setCookie(cookie);

			if (uri.equals("/disconnect")) {
				if (cookieValid) {
					try {
						sessions.remove(cookie.get("key"));
					} catch (Exception ex) {
					}
				}
				cookie.set("key", -1);
				cookie.set("auth", false);

				verifySession(cookie.get("key"));
				req.pipe.setCookie(cookie);
				uri = "/";
				parsedUri = documentRoot.getFile(uri);
			}

			// !localhost because of an unsolved tmp bug
			else if (!cookieValid && !uri.startsWith("/public/") // NON
																	// PROTECTED
																	// DOMAIN
					&& !isLocalhost(req.serverVariables.getParam("Host"))) {
				// /login ~ servlet
				String key = createSession(req.queryString.getParam("login"),
						req.queryString.getParam("pass"));

				// err("COOKIE LOGIN = " + req.queryString.getParam("login"));
				// err("COOKIE PASS = " + req.queryString.getParam("pass"));
				// err("COOKIE K = " + key);
				String origUri = req.queryString.getParam("uri");
				if (origUri == null) {
					origUri = uri;
				}
				// err("COOKIE URL = " + req.queryString.getParam("uri"));
				if (key == null) {
					String resp = "<H1> You are not connected !</H1>"
							+ "<form action='/' method='POST'>"
							+ "login:<input type='text' name='login'><br/>"
							+ "pass:<input type='password' name='pass'><br/>"
							+ "<input type='hidden' name='uri' value='"
							+ origUri + "'>"
							+ "<input type='submit' value='GO !'>" + "</form>";
					req.pipe.sendResponse(HTTP_OK, null, resp, TEXT_HTML);
					return;
				}
				cookie.set("key", key);
				cookie.set("auth", true);

				boolean validKey = verifySession(cookie.get("key"));
				// err("COOKIE VALID K = " + validKey);
				req.pipe.setCookie(cookie);
				uri = origUri;
				parsedUri = documentRoot.getFile(uri);

			}

			// ------------- Cookie Managment -----------

			if (parsedUri == null) {
				req.pipe.printText("<H1>Erreur 404 :</H1><BR> '"
						+ req.urlRequested + "'<BR> ");
						
						
System.out.println( "[404] "+req.urlRequested );
parsedUri = documentRoot.getFile(uri);
System.out.println( "[404] "+parsedUri );
System.out.println( "[404] "+req.queryString.getParam("uri") );
System.out.println( "[404] ");						
						
						
			} else {
				// if documentRoot.startsWith(parsedUri, "/cgi-bin")....
				boolean modeFile = true;
				if (modeFile) {
					File uriFile = new File(parsedUri);
					if (uriFile.isDirectory()) {
						String originalParsedUri = parsedUri;
						parsedUri = originalParsedUri + '/' + INDEX_PAGE;
						if (!new File(parsedUri).exists()) {
							parsedUri = originalParsedUri + '/' + INDEX_PAGE_2;
						}
						if (!new File(parsedUri).exists()) {
							parsedUri = originalParsedUri + '/' + INDEX_PAGE_3;
						}
					}

					// System.out.println( parsedUri );

					String contentType = getContentType(parsedUri);
					// System.out.println( contentType );

					if (contentType == null) {
						contentType = "application/octet-stream";
					}

					File fic = new File(parsedUri);
					if (fic.exists()) {

						if (EXEC_JS.equals(contentType)) {
							serverExecuteJS(parsedUri, req);
						} else if (EXEC_JA.equals(contentType)) {
							serverExecuteJA(parsedUri, req);
						} else {
							if (contentType.equals("application/pdf")) {
								// System.out.println("here");
								// contentType = "application/octet-stream";
							}
							req.pipe.sendResponse(HTTP_OK, fic.length(), null,
									parsedUri, new FileInputStream(fic),
									contentType);
						}
					} else {
						req.pipe.printText("<H1>Erreur 404 :</H1><BR> '"
								+ req.urlRequested + "'<BR> ");

System.out.println( "[404] "+parsedUri);
System.out.println( "[404] "+fic.getAbsolutePath());

					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getContentType(String uri) {
		int pointPos = uri.lastIndexOf(".");
		if (pointPos == -1 || pointPos + 1 >= uri.length())
			return (null);
		String ext = uri.substring(pointPos + 1).toLowerCase();
		for (int i = 0; i < mimeTypes.length; i++) {
			if (mimeTypes[i][0].equals(ext))
				return (mimeTypes[i][1]);
		}
		return (null);
	}

	public void serverExecuteJS(String uri, HttpReq req) throws Exception {
		new SJSParser().exec(uri, this, req);
	}

	public void serverExecuteJA(String uri, HttpReq req) throws Exception {
		new SJAParser().exec(uri, this, req);
	}

	public SystemDir getDocumentRoot() {
		return (documentRoot);
	}

}