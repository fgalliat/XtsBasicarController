package com.xtase.websandbox.www.pack.url;

import java.net.*;

public class URLCoder {

	// MAIN - volountary...
	public static String encodingType = "ISO-8859-1";
	public static String encodingType2 = "UTF-8";

	// ---- 1.4 ---

	public static String urlEncode(String s) {
		try {
			return (URLEncoder.encode(s, encodingType));
		} catch (Exception ex) {
			return (null);
		}
	}

	public static String urlDecode(String s) {
		try {
			return (URLDecoder.decode(s, encodingType));
		} catch (Exception ex) {
			try {
				return (URLDecoder.decode(s, encodingType2));
			} catch (Exception ex2) {
				ex2.printStackTrace();
				return (null);
			}
		}
	}

	// ---- 1.2 ---
	// public static String urlEncode(String s) { try { return(
	// URLEncoder.encode(s) ); } catch(Exception ex) { return(null); } }
	// public static String urlDecode(String s) { try { return(
	// URLDecoder.decode(s) ); } catch(Exception ex) { return(null); } }

	// ---- 1.1 ---
	/*
	 * public static String urlEncode(String s) { try { return(
	 * URLEncoder.encode(s) ); } catch(Exception ex) { return(null); } } public
	 * static String urlDecode(String s) { String ret = ""; char ch; for(int
	 * i=0; i < s.length(); i++) { ch = s.charAt(i); if ( ch == '+' ) { ret +=
	 * ' '; } else if ( ch == '%' ) { i++; String tmp = ""+s.charAt(i) +
	 * s.charAt(i+1); int ch0 = Integer.parseInt(tmp, 16); ret += (char)ch0;
	 * i++; } else ret += ch; } return( ret ); }
	 */

}
