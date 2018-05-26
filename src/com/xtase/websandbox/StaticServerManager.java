package com.xtase.websandbox;

import java.io.File;
import com.xtase.classpath.config.*;
import java.util.*;

import com.xtase.websandbox.www.WebServer;

public class StaticServerManager {

	static /*final*/ int SERVER_PORT = 8888;
	
	protected static WebServer server;

	// TODO : better
	protected static WebServerListener listener = null;

	public static void setListener(WebServerListener _listener) {
		listener = _listener;
	}

	protected static void toast(Object o) {
		if (listener != null) {
			listener.toast(o);
		}
	}

  static Properties config = null;

  public static void start() throws Exception {
  	start(null);
  }

	public static void start(String webRoot) throws Exception {
	
	    boolean AUTH_COOKIE_DISABLED = false;
	    boolean verbose = false;
	
		if (server != null) {
			stop();
		}
		
		try {
			config = EnvLocalVarParser.loadProps("./config/server.conf");
			try { SERVER_PORT = Integer.parseInt( config.getProperty("server.port") ); } catch(Exception ex) {}
			if (webRoot == null) { webRoot = config.getProperty("server.root"); }
			if ( config.getProperty("server.auth.disabled") != null ) {
				AUTH_COOKIE_DISABLED = "true".equalsIgnoreCase( config.getProperty("server.auth.disabled") );
			}
			if ( config.getProperty("server.verbose") != null ) {
				verbose = "true".equalsIgnoreCase( config.getProperty("server.verbose") );
			}
			
			
		} catch(Exception ex) {
			System.err.println("Failed to load config");
		}
		
		if (server == null) {
			server = new WebServer(SERVER_PORT,
					// for atrix
					// TODO : use OpenSystem
					webRoot != null ? webRoot : (new File("/sdcard-ext/").exists() ? "/sdcard-ext/vm_mnt/www/" : new File("/sdcard/").exists() ? "/sdcard/vm_mnt/www/" : "c:/vm_mnt/share/http/") ) {
				@Override
				public void error(final Exception ex) {
					toast(ex.toString());
				}

				@Override
				public void bindError(final Exception ex) {
					toast(ex.toString());
				}

				@Override
				public void ready() {
					TURBO_MODE = true;
					toast("Server started");

					if (listener != null) {
						listener.onReady();
					}

				}
			};
		}
		server.AUTH_COOKIE_DISABLED = AUTH_COOKIE_DISABLED;
		server.start();
	}

	public static void stop() throws Exception {
		if (server != null) {
			server.stop();
			server = null;
			System.gc();
		}
	}

}
