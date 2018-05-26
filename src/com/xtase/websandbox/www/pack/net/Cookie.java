package com.xtase.websandbox.www.pack.net;

public class Cookie {

	protected QueryString content = new QueryString();

	public static Cookie fromString(String value) {
		Cookie result = new Cookie();
		result.content = new QueryString(value);
		return result;
	}

	public void set(String key, Object value) {
		content.put(key, value);
	}

	public String get(String key) {
		return content.get(key) == null ? null : String.valueOf(content
				.get(key));
	}

	public String toString() {
		return content.getSerialised();
	}

}
