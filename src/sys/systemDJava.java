package sys;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import koala.dynamicjava.interpreter.TreeInterpreter;

import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.WrappedException;

/*
 * Dynamic Java - (koala)
 * 
 */

public class systemDJava {

	protected boolean quitting = false;
	protected String scriptName = null;

	protected String bootPath = null;
	protected TreeInterpreter interpreter = null;

	boolean debug = false;
	coupleStream current_console = null;

	public systemDJava(TreeInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	public void init() {
	}

	public void close() {
	}

	public double version() {
		return (1.1);
	}

	public void boot(String bootPath) {
		this.bootPath = bootPath;
		System.gc();
		load(bootPath + "boot.djava");
	}

	public void reboot() {
		if (current_console != null)
			current_console.close();
		boot(bootPath);
	}

	public void loadClass_forName(String class2load) throws Exception {
		Class.forName(class2load);
	}

	// -----------| Consoles |------------------
	public coupleStream getDefaultConsole() {
		return (current_console);
	}

	public void setDefaultConsole(coupleStream new_console) {
		current_console = new_console;
		// TODO
		// current_console.setSysParent(this);
	}

	// -----------| Tasks |--------------------

	public void gc() {
		System.gc();
	}

	public void createThread(String script, String name) { // NYD
	}

	public void kill(String name) { // NYD
	}

	public void kill_all_tasks() {
		if (current_console != null) {
			current_console.print("All Tasks were killed\n");
		}
	}

	public void quit() {
		quitting = true;
		kill_all_tasks();
		if (current_console != null)
			current_console.close();
		System.exit(0);
	}

	public void load(String filename) {
		BufferedReader in = null;
		try {
			Reader reader = null;
			if (filename == null)
				reader = new InputStreamReader(current_console.getInputStream());
			else {
				File f = new File(filename);
				if (!f.exists()) {
					errorPrint("No such file");
					return;
				}
				// ===== BY-PASS =====
				FileInputStream fin = new FileInputStream(f);
				try {
					Object ret = interpreter.interpret(fin, f.getName());
				} catch (Exception e) {
					errorPrint("javaError: " + e.getMessage() + "\n");
				} finally {
					try {
						fin.close();
					} catch (Exception ee) {
					}
				}
				return;
				// ===================
			}
			in = new BufferedReader(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sourceName = filename;
		int lineno = 1;
		boolean hitEOF = false;
		do {
			int startline = lineno;
			try {
				String source = ""; // Collect lines of source to compile.
				while (true) {
					String newline;
					newline = in.readLine();
					if (getDebug())
						System.out.println("->[" + newline + "]<-");
					if (newline == null) {
						hitEOF = true;
						break;
					} else {
					}
					source = source + newline + "\n";
					lineno++;
					// if (cx.stringIsCompilableUnit(source))
					// break;
				}
				Object result = evaluteExpression(source);
			} catch (WrappedException we) {
				errorPrint(we.getWrappedException().toString() + "\n");
				we.printStackTrace();
			} catch (EvaluatorException ee) {
				errorPrint("javaError: " + ee.getMessage() + "\n");
			} catch (JavaScriptException jse) {
				errorPrint("javaError: " + jse.getMessage() + "\n");
			} catch (IOException ioe) {
				errorPrint("javaError: " + ioe.toString() + "\n");
			} catch (Exception ioe) {
				errorPrint("Error: " + ioe.toString() + "\n");
			}
			if (quitting) {
				break;
			}
		} while (!hitEOF);
		errorPrint("\n");
		System.gc();
	}

	public String evaluteExpression(String expr) throws Exception {
		// System.out.println("<"+ expr +">");
		ByteArrayInputStream is = new ByteArrayInputStream(expr.getBytes());
		Object ret = interpreter.interpret(is, "StringScript");
		if (ret != null)
			return (ret.toString());
		return ("");
	}

	protected void hardPrint(String s) {
		System.out.print(s);
	}

	protected void errorPrint(String s) {
		System.err.print(s);
	}

	protected void errorFlush() {
		System.err.flush();
	}

	public static String replaceBy(String str, String oldE, String newE) {
		String str1 = new String(str);
		int index = -1;
		while ((index = str1.indexOf(oldE)) > -1) {
			String gauche = str1.substring(0, index);
			String droite = str1.substring(index + oldE.length());
			str1 = new String(gauche + newE + droite);
		}
		return (str1);
	}

	// -----------| Misc |------------------
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean getDebug() {
		return (debug);
	}

}
