package com.xtase.xtsubasic;

import com.xtase.file.FileUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import com.xtase.virtualtapemanager.file.FileUtils;

/**
 * X07 BASIC Preprocessor <br/>
 * <br/>
 * 10 REM DFLBL DRAW_RECT <br/>
 * (..) <br/>
 * 100 GOSUB $DRAW_RECT <--> 100 GOSUB 10 <br/>
 * <-- encode <br/>
 * --> decode <br/>
 * <br/>
 * 
 * @author Xtase fgalliat 10/2016 <br/>
 *         TODO : manage multiple GOTO / GOSUB on same line <br/>
 */

public class PreProcessor {

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Usage PreProcessor -e/-d <file{.bas, .txt}>");
			return;
		}

		String op = args[0];
		File toRead = new File(args[1]);
		String source = new String(FileUtils.cat(toRead.getAbsolutePath()));

		String result = null;
		if (op.equals("-d")) {
			result = decode(source);
		} else {
			result = encode(source);
		}

		System.out.println(result);

		// String encoded = encode(source);
		// String decoded = decode(encoded);
		//
		// System.out.println(encoded);
		// System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~");
		// System.out.println(decoded);
	}

	public static String decodeSafe(String source) throws Exception {
		try {
			return decode(source);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}


	protected static void readAllLabelDefs(String source, Map<String, Integer> defMap, Map<Integer, String> defMapInv) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(source.getBytes())));
		String line;

		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}
			if (line.startsWith("##")) {
				continue;
			}

			line = line.replace('\t', ' ');

			String lineNum = line.split(" ")[0];


			String lineRest = null;
			try { lineRest = line.substring((lineNum + " ").length()); }
			catch(Exception ex) {
				System.err.println("(EE) on line => "+line);
				ex.printStackTrace();
			}

			lineRest = lineRest.trim();

			if (lineRest.toUpperCase().startsWith("REM DFLBL ")) {
				String lblExpr = lineRest.substring("REM DFLBL ".length()).trim();
				int lnNum = Integer.parseInt(lineNum);
				defMap.put(lblExpr, lnNum);
				defMapInv.put(lnNum, lblExpr);
				// System.out.println("found LBL '" + lblExpr + "' @" + lineNum);
			}
		}

		// short recap
		// for (Map.Entry<String, Integer> entry : defMap.entrySet()) {
		// System.out.println("(ii) I found '" + entry.getKey() + "' @" +
		// entry.getValue());
		// }
	}

	/** 100 GOSUB 10 --> 100 GOSUB $DRAW_RECT */
	public static String encode(String source) throws Exception {
		String result = "";

		// lecture des definitions de LABEL
		Map<String, Integer> defMap = new HashMap<String, Integer>();
		Map<Integer, String> defMapInv = new HashMap<Integer, String>();

		readAllLabelDefs(source, defMap, defMapInv);

		BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(source.getBytes())));
		String line;

		// ============================================================

		// lecture des appels de line
		in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(source.getBytes())));

		// "## ..."
		boolean inSystemRemmark = false;
		boolean hasReqPreprocessor = false;

		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}

			if (line.startsWith("##")) {
				if (line.toUpperCase().startsWith("## USE:PREPROCESSOR")) {
					hasReqPreprocessor = true;
				}

				result += line + "\n";
				inSystemRemmark = true;
				continue;
			} else if (inSystemRemmark && !hasReqPreprocessor) {
				result += "## " + "\n";
				result += "## USE:PREPROCESSOR" + "\n";
				result += "## " + "\n";
				inSystemRemmark = false;
				// continue;
			}
			if (!line.startsWith("##")) {
				inSystemRemmark = false;
			}

			inSystemRemmark = false;

			line = line.replace('\t', ' ');

			String lineNum = line.split(" ")[0];

			// TODO manage multi spaces
			String lineRest = line.substring((lineNum + " ").length()).trim();


			// TODO : manage multiple GOTO / GOSUB on same line
			String tk = null;
			String callExpr = null;
			String replacementExpr = null;

			int pos = lineRest.indexOf("GOTO ");
			if (pos != -1) {
				tk = "GOTO ";
			} else {
				pos = lineRest.indexOf("GOSUB ");
			}
			if (pos != -1) {
				if (tk == null) {
					tk = "GOSUB ";
				}
				callExpr = lineRest.substring(pos);
				if (callExpr.indexOf(":", 0) > -1) {
					callExpr = callExpr.substring(0, pos);
				}

				int lineDestNum = Integer.parseInt(callExpr.split(" ")[1]);

				if (defMapInv.get(lineDestNum) == null) {
					// just call a line w/o any label...
					// System.out.println("(!!) destination not found : " + lineDestNum);
					replacementExpr = tk + "" + lineDestNum;
				} else {
					replacementExpr = tk + "$" + defMapInv.get(lineDestNum);
				}

				// System.out.println(lineNum + " // " + lineRest + (callExpr == null ?
				// "" : " // => " + callExpr + " -> " + replacementExpr));
			}

			if (replacementExpr != null) {
				line = line.replaceAll(Pattern.quote(callExpr), Matcher.quoteReplacement(replacementExpr));
			}

			result += line + "\n";
		}

		return result;
	}

	/** 100 GOSUB $DRAW_RECT --> 100 GOSUB 10 */
	public static String decode(String source) throws Exception {

		// lecture des definitions de LABEL
		Map<String, Integer> defMap = new HashMap<String, Integer>();
		Map<Integer, String> defMapInv = new HashMap<Integer, String>();
		readAllLabelDefs(source, defMap, defMapInv);

		BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(source.getBytes())));
		String line;
		String result = "";

		// ============================================================

		// lecture des appels de Label
		in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(source.getBytes())));

		// "## ..."
		boolean inSystemRemmark = false;
		boolean hasReqPreprocessor = false;

		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}

			if (line.startsWith("##")) {
				if (line.toUpperCase().startsWith("## USE:PREPROCESSOR")) {
					hasReqPreprocessor = true;
				}

				result += line + "\n";
				inSystemRemmark = true;
				continue;
			} else if (inSystemRemmark && !hasReqPreprocessor) {
				result += "## " + "\n";
				result += "## USE:PREPROCESSOR" + "\n";
				result += "## " + "\n";
				inSystemRemmark = false;
				// continue;
			}

			if (!line.startsWith("##")) {
				inSystemRemmark = false;
			}

			line = line.replace('\t', ' ');

			String lineNum = line.split(" ")[0];

			// TODO manage multi spaces
			String lineRest = line.substring((lineNum + " ").length());

			System.out.println("[REST] ("+ lineRest +")");
			if (lineRest.startsWith("REM") || lineRest.startsWith("'")) {
				result += line+'\n';
				continue;
			}


			// TODO : manage multiple GOTO / GOSUB on same line
			String tk = null;
			String callExpr = null;
			String replacementExpr = null;

			int pos = lineRest.indexOf("GOTO ");
			if (pos != -1) {
				tk = "GOTO ";
			} else {
				pos = lineRest.indexOf("GOSUB ");
			}
			if (pos != -1) {
				if (tk == null) {
					tk = "GOSUB ";
				}
				callExpr = lineRest.substring(pos);
				if (callExpr.indexOf(":", 0) > -1) {
					callExpr = callExpr.substring(0, pos);
				}

				// can be a regular lineNumber too !!
				String lineDestLabel = null;

				if ( callExpr.length() == 0 ) {
					continue;
				}

				try { lineDestLabel = callExpr.split(" ")[1]; }
				catch (Exception ex) {
					System.out.println("(EE) @ "+line);
					System.out.println("(EE) @ "+lineRest);
					System.out.println("(EE) @ "+callExpr);
					return null;
				}

				// .substring(1) to remove '$'
				if (defMap.get(lineDestLabel.substring(1)) == null) {
					// just call a line w/o any label...
					// System.out.println("(!!) destination not found : " + lineDestNum);
					replacementExpr = tk + "" + lineDestLabel;
				} else {
					replacementExpr = tk + "" + defMap.get(lineDestLabel.substring(1));
				}

				// System.out.println(lineNum + " // " + lineRest + (callExpr == null ?
				// "" : " // => " + callExpr + " -> " + replacementExpr));
			}

			if (replacementExpr != null) {
				line = line.replaceAll(Pattern.quote(callExpr), Matcher.quoteReplacement(replacementExpr));
			}

			result += line + "\n";
		}

		return result;
	}

}
