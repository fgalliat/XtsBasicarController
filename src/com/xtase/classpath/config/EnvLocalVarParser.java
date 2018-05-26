package com.xtase.classpath.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvLocalVarParser {

	public static interface VariableProvider {
		public String getProperty(String key);

		public Set<Object> keySet();

		public Object setProperty(String key, String value);
	}

	// =============================================

	protected static String MASK_ENV_VAR = "%(\\w+)%";
	protected String MASK_LOCAL_VAR = "\\$\\{(\\w+)\\}";
	protected VariableProvider vars = null;

	public EnvLocalVarParser(VariableProvider variables, String localVariableMask) {
		this(variables);
		MASK_LOCAL_VAR = localVariableMask;
	}

	public EnvLocalVarParser(VariableProvider variables) {
		this.vars = variables;
	}

	public String getValue(String key) {
		return getValue(key, 0);
	}

	protected String getValue(String key, int recurseCpt) {
		String value = vars.getProperty(key);
		if (value == null) {
			return "";
		}

		value = replaceEnvVar(value);
		value = replacePropVar(value, vars, recurseCpt);

		return value;
	}

	// =============================================

	protected void resolveAllKeys() {
		for (Object keyObj : vars.keySet()) {
			String key = (String) keyObj;
			vars.setProperty(key, getValue(key));
		}
	}

	// =============================================
	public static final String MASK_OUR_VARS = "\\$\\[(\\w+)\\]";

	// =============================================

	public static ExtProps loadProps(String file) throws Exception {
		return loadProps(file, MASK_OUR_VARS);
	}

	public static ExtProps loadProps(InputStream in) throws Exception {
		return loadProps(in, MASK_OUR_VARS);
	}

	public static ExtProps loadProps(Reader rd) throws Exception {
		return loadProps(rd, MASK_OUR_VARS);
	}

	// ===============

	public static ExtProps loadProps(String file, String mask) throws Exception {
		ExtProps props = new ExtProps(file);
		return loadProps(props, mask);
	}

	public static ExtProps loadProps(InputStream in, String mask) throws Exception {
		ExtProps props = new ExtProps(in);
		return loadProps(props, mask);
	}

	public static ExtProps loadProps(Reader rd, String mask) throws Exception {
		ExtProps props = new ExtProps(rd);
		return loadProps(props, mask);
	}

	public static ExtProps loadProps(ExtProps props, String mask) throws Exception {
		EnvLocalVarParser parser = new EnvLocalVarParser(props, mask);
		parser.resolveAllKeys();
		return props;
	}

	// =============================================
	// just a simple Utility impl.
	public static class ExtProps extends Properties implements VariableProvider {
		private static final long serialVersionUID = -375700747763129888L;

		protected String propFileName = null;
		protected InputStream in = null;
		protected Reader reader = null;

		public ExtProps(String fileName) throws Exception {
			this.propFileName = fileName;
			this.in = new FileInputStream(this.propFileName);
			reload();
		}

		public ExtProps(InputStream in) throws Exception {
			this.in = in;
			reload();
		}

		public ExtProps(Reader reader) throws Exception {
			this.reader = reader;
			reload();
		}

		public void reload() throws Exception {
			if (this.in != null) {
				load(this.in);
				this.in.close();
			} else {
				load(this.reader);
				this.reader.close();
			}
		}

	}

	// =============================================

	// private static final String REGEX_VARENV = "%(\\w+)%|\\$\\{(\\w+)\\}";
	// String env = null == m.group(1) ? m.group(2) : m.group(1);

	// '%OS%' => 'WINDOWS....'
	protected static String replaceEnvVar(String str) {
		Pattern p = Pattern.compile(MASK_ENV_VAR);
		Matcher m = p.matcher(str);

		StringBuffer res = new StringBuffer();
		while (m.find()) {
			String env = m.group(1);
			// JRE prop
			String envValue = System.getProperty(env);
			if (envValue == null || envValue.equals("")) {
				// SystemEnv Prop
				envValue = System.getenv(env);
			}
			if (envValue == null) {
				envValue = "";
			}

			// il n'y a que le PATH sous Windows dont les '\' sont remplaces par de
			// '/'
			// les autres chemins ne sont pas impactes
			if (env.equalsIgnoreCase("path") && ("" + System.getenv("OS")).toLowerCase().indexOf("windows") > -1) {
				envValue = envValue.replaceAll(Pattern.quote("/"), Matcher.quoteReplacement("\\"));
			}

			envValue = envValue.replace("\\", "\\\\");
			if (envValue == null || envValue.equals("")) {
				// throw new NullPointerException("Variable d'environnement '" + env +
				// "' inconnue.");
				return "";
			}
			m.appendReplacement(res, envValue);
		}
		m.appendTail(res);
		return res.toString();
	}

	// '${System.name}' => 'Win 10 moa'
	protected String replacePropVar(String str, VariableProvider variables, int recurseCpt) {
		Pattern p = Pattern.compile(MASK_LOCAL_VAR);
		Matcher m = p.matcher(str);

		StringBuffer res = new StringBuffer();
		while (m.find()) {
			String env = m.group(1);
			// prop's prop
			// String envValue = variables.getProperty(env);

			recurseCpt++;
			if (recurseCpt >= 15) {
				return null;
			}
			String envValue = this.getValue(env, recurseCpt);

			if (envValue == null) {
				envValue = "";
			}
			envValue = envValue.replace("\\", "\\\\");
			if (envValue == null || envValue.equals("")) {
				return "";
			}
			m.appendReplacement(res, envValue);
		}
		m.appendTail(res);
		return res.toString();
	}

}
