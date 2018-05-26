package com.xtase.websandbox;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

public class Logger {

	protected static boolean inited = false;
	protected static PrintStream out = null;

	protected static void init() {
		try {
			out = new PrintStream(new FileOutputStream("/sdcard/log.webserver.txt", true));
		} catch (Exception ex) {

		}
	}

	public static void error(Exception ex) {
		if (!inited) {
			init();
		}
		if (out != null) {
			out.println("##" + new Date().toString());
			out.println(ex.toString());
			ex.printStackTrace(out);
		}
	}

}
