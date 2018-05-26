package com.xtase.websandbox.www;

import koala.dynamicjava.interpreter.TreeInterpreter;
import koala.dynamicjava.parser.wrapper.JavaCCParserFactory;
import sys.systemDJava;

public class DynamicJavaParser {

	protected TreeInterpreter interpreter = null;
	protected boolean quitting = false;
	protected String scriptPath = null;

	protected systemDJava systemX = null;

	public DynamicJavaParser(String scriptPath) throws Exception {
		this.scriptPath = scriptPath;
		interpreter = new TreeInterpreter(new JavaCCParserFactory());
		systemX = new systemDJava(interpreter);
		systemX.setDebug(false);
		interpreter.defineVariable("systemX", systemX);
	}

	public void addReference(String ref, Object dest) {
		interpreter.defineVariable(ref, dest);
	}

	public String evaluate(String str) throws Exception {
		return (systemX.evaluteExpression(str));
	}

	public void launch() throws Exception {
		systemX.boot(scriptPath);
		// interpreter.exit();
	}

	public void dispose() {
		try {
			// interpreter.exit();
		} catch (Exception ex) {
		}
	}

}
