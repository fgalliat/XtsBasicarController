package com.xtase.websandbox.www.pack.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import com.xtase.websandbox.www.pack.common.data.string.StringUtils;

public class SystemDir {

	protected String root = null;
	protected Hashtable aliases = new Hashtable();

	public SystemDir(String root) throws Exception {
		this.root = hard_getFile(root);
	}

	public String getFile(String path) throws Exception {
		String fpath = hard_getFile(root + '/' + path);
		if (!fpath.startsWith(root))
			return (null);

		if (new File(fpath).isDirectory()) {
			fpath += "/";
			fpath = resolveAlias(fpath);
			fpath = fpath.substring(0, fpath.length() - 1);
		} else {
			fpath = resolveAlias(fpath);
		}

		return (fpath /* .substring(root.length() ) */);
	}

	protected String hard_getFile(String path) throws Exception {
		String ret = null;
		ret = new File(path).getCanonicalFile().getAbsolutePath();
		if (ret.indexOf(":") == 1)
			ret = ret.substring(2);
		ret = ret.replace('\\', '/');
		return (ret);
	}

	/**
	 * only directories could be aliased <BR>
	 * source & dest are always absolute to root <BR>
	 * isAbsolute only if dest is upper than root
	 */
	public void addAlias(String source, String dest, boolean isAbsolute) throws Exception {
		if (source == null || dest == null)
			throw new Exception("source or dest. is null !");
		if (!isAbsolute) {
			aliases.put(getFile(source), getFile(dest));
		} else {
			aliases.put(getFile(source), dest);
		}
	}

	protected String resolveAlias(String path) {
		Enumeration aliasesKeys = aliases.keys();
		String key;
		String val;
		String tmpPath = "" + path;
		while (aliasesKeys.hasMoreElements()) {
			key = aliasesKeys.nextElement().toString();
			// System.out.println(key+" - "+aliases.get(key));
			val = aliases.get(key).toString();
			if (!key.endsWith("/"))
				key += "/"; // cf '/env' => '/env/' ou '/envoi/'
			if (!val.endsWith("/"))
				val += "/";

			if (tmpPath.startsWith(key))
				tmpPath = StringUtils.replaceBy(tmpPath, key, val);
		}
		return (tmpPath);
	}

	public String getContent(String path) throws Exception {
		/*
		 * String loc = getFile(path); if (loc == null) throw new
		 * FileNotFoundException(path);
		 */
		InputStream in = new FileInputStream(path);
		//byte[] b = new byte[4 * 1024];
		byte[] b = new byte[96 * 1024];
		int readed = -1;
//		String ret = "";
//		while ((readed = in.read(b)) > -1) {
//			ret += new String(b, 0, readed);
//		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((readed = in.read(b)) > -1) {
//			ret += new String(b, 0, readed);
			baos.write(b,0,readed);
		}
		
		in.close();
		
		String ret = new String( baos.toByteArray() );
		
		return (ret);
	}

}