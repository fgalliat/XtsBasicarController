package com.xtase.websandbox.www.pack.net;

import java.util.HashMap;

import com.xtase.websandbox.www.pack.common.data.string.StringUtils;
import com.xtase.websandbox.www.pack.url.URLCoder;

public class QueryString extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public QueryString() {
	}

	public static void err(Object o) {
		//System.err.println(""+o);
	}
	
	public QueryString(String urlEncoded) {
		String[] couples = StringUtils.split(urlEncoded, "&");
		String[] pair;
		for (int i = 0; i < couples.length; i++) {
			pair = StringUtils.split(couples[i], "=");
			String key = URLCoder.urlDecode(pair[0]);
			String value = pair.length > 0 ? URLCoder.urlDecode(pair[1]) : "";
			
			
			err("<<key="+key);
			err("<<val="+value);
			
			put(key,value);
		}
	}

	public QueryString(String[][] values) {
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				if (values[i] != null)
					if (values[i][0] != null)
						put(values[i][0], values[i][1]);
			}
		}
	}

	public String getParam(String param) {
		return (containsKey(param) ? get(param).toString() : null);
	}

	public boolean isDefined(String param) {
		String result = (String) get(param);
		return (result != null && result.length() > 0);
	}

	public String getVal(String paramName) {
		return (isDefined(paramName) ? (String) get(paramName) : null);
	}

	public int getIntVal(String paramName) {
		if (isDefined(paramName))
			try {
				return (Integer.parseInt(getVal(paramName)));
			} catch (Exception ex) {
			}
		return (-1);
	}

	public float getFloatVal(String paramName) {
		if (isDefined(paramName))
			try {
				return (Float.parseFloat(getVal(paramName)));
			} catch (Exception ex) {
			}
		return (-1.0F);
	}

	public double getDoubleVal(String paramName) {
		if (isDefined(paramName))
			try {
				return (Double.parseDouble(getVal(paramName)));
			} catch (Exception ex) {
			}
		return (-1.0D);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	public String getSerialised() {
		String ret = "";
		for (String key : keySet()) {
			ret += URLCoder.urlEncode(key) + "="
					+ URLCoder.urlEncode( String.valueOf(get(key)) /*get(key).toString()*/) + "&";
		}
		if (ret.length() > 0) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return (ret);
	}

	public void show() {
		for (String key : keySet()) {
			System.out.println(key + "=" + get(key).toString());
		}
	}

	public void _dispConsole() {
		System.out.println(getSerialised());
	}

}