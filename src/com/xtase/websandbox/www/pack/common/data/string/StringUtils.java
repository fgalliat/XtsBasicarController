package com.xtase.websandbox.www.pack.common.data.string;

import com.xtase.websandbox.www.pack.ext_struct.table.stringTab;

public class StringUtils {

 public static final String HEX_SYMBOLS = "0123456789ABCDEF";


 // ---------------------------

 public static String formatDecimal(double d, int decim) {
  String seq = ""+d;
  int pos = seq.indexOf( "." );
  if ( pos == -1 ) return( seq+"."+repeat('0', decim) );

  try {
   String left  = seq.substring( 0, pos );
   String right = seq.substring( pos+1 );
   if (right.length() < decim) { right += repeat('0', (decim - right.length() ) ); }
   else { right = right.substring(0, decim); }
   seq = left +"."+ right;
   //System.err.println("(!!) pack.common.data.string.StringUtils.formatDecimal("+d+","+decim+") => "+seq); 
  } catch(Exception ex) { 
     System.err.println("(EE) pack.common.data.string.StringUtils.formatDecimal("+d+","+decim+") !!ERROR!! "); 
    }

 return( seq );
 }

 // ---------------------------


 // ==================== ==================== ==================== ==================== ==================== ====================
 public static String ucFirst(String str) { return( (""+str.charAt(0)).toUpperCase() + str.substring(1).toLowerCase() ); }

 // ==================== ==================== ==================== ==================== ==================== ====================

 public static String quote(String pieceOfcode) { return( quoteString(pieceOfcode) ); }

 public static String quoteString(String pieceOfcode) {
  String ret = StringUtils.replaceBy( StringUtils.replaceBy( pieceOfcode , "\\", "\\\\") , "\"", "\\\"");
         ret = StringUtils.replaceBy(ret, "\r", "");
         ret = StringUtils.replaceBy(ret, "\n", "\"+\n\""); // ici
         if ( !ret.endsWith("\n") ) { ret += "\\n\"+\n"; }
         ret = "\""+ret;
  ret = ret.substring(0, ret.length() - 2);	// enleve le '+\n"
 return(ret);
 }

 public static String toStringCode(String pieceOfcode) {
  String ret = replaceBy( replaceBy( pieceOfcode , "\\", "\\\\") , "\"", "\\\"");
         ret = replaceBy(ret, "\r", "");
         ret = replaceBy(ret, "\n", "\"+\n\"");
         if ( !ret.endsWith("\n") ) { ret += "\\n\"+\n"; }
         ret = "\""+ret;
 return(ret);
 }


 // ==================== ==================== ==================== ==================== ==================== ====================

 // a securiser (autre implem)
 public static int hexToInt(String digit) {
  int value = (HEX_SYMBOLS.indexOf( digit.charAt(0) ) * 16) + HEX_SYMBOLS.indexOf( digit.charAt(1) );
 return( value );
 }

 /**
  Extrait une represantaion numerique d'une chaine
  ex: toto51541a1 => 51541
 */
 public static String extractNumRepres(String name) {
  boolean num    = false;
  String  tmpRes = "";
  for (int i=0; i < name.length(); i++) {
   if (name.charAt(i) >= '0' && name.charAt(i) <= '9') {
    if (!num) { num = true; }
    tmpRes += ""+name.charAt(i);
   }
   else if (num) break;
  }
 return(tmpRes);
 }

 // ==================== ==================== ==================== ==================== ==================== ====================

 public static String repeat(char ch, int times) { return( paddStringRight("", times, ch) ); }

 // ---------------------------

 public static String paddStringRight(String str, int length)                { return( paddStringRight(str, length, ' ') ); }
 public static String paddStringRight(String str, int length, char fillWith) {
  String representation = ""+str;			// cf padding de nul => 'null   '
  String str2           = ""+representation;
  if (representation.length() > length) { str2 = representation.substring(0, length); }
  else                                  { for(int i=representation.length(); i < length; i++) { str2 += fillWith; } }
 return(str2);
 }

 public static String paddStringLeft(String str, int length)                { return( paddStringLeft(str, length, ' ') ); }
 public static String paddStringLeft(String str, int length, char fillWith) {
  String representation = ""+str;			// cf padding de nul => 'null   '
  String str2           = ""+representation;
  if (representation.length() > length) { str2 = representation.substring(0, length); }
  else                                  { for(int i=representation.length(); i < length; i++) { str2 = fillWith+str2; } }
 return(str2);
 }

 // ---------------------------

 public static String formatString(String str, int len) { return( formatString(str, len, true) ); }
 public static String formatString(String str, int len, boolean stringMode) { return( formatString(str, len, stringMode, ' ') ); }
 public static String formatString(String str, int len, boolean stringMode, char fillWith) {
  String tmp = ""+str;
  if (str.length() > len) tmp = str.substring(0,len);
  else {
   if (stringMode) for(int i=tmp.length(); i < len; i++) tmp += fillWith;
   else            for(int i=tmp.length(); i < len; i++) tmp = fillWith+tmp;
  }
 return(tmp);
 }


 /** as String.trim() remove all char <= 20h at left of string */
 public static String ltrim(String str) {
  int i = 0;
  for(;i < str.length(); i++) { if ( (int)str.charAt(i) > 32 ) { break; } }
 return( (i == str.length() - 1) ? "" : str.substring(i) );
 }

 // ---------------------------

 public static String addIndents(String origin, int howManySpaces) {
  String[] lines = split(origin, "\n");
  String ret = "";
  for(int l=0; l < lines.length; l++) {
   ret += repeat( ' ', howManySpaces) + lines[l] + "\n";
  }
 return(ret);
 }

 // ==================== ==================== ==================== ==================== ==================== ====================

 public static String join(int[] tab) {
  String join = "";
  for(int i=0; i < tab.length; i++) { join += tab[i] + ( (i < tab.length - 1) ? ", " : "" ); }
 return(join);
 }

 public static String join(boolean[] tab) {
  String join = "";
  for(int i=0; i < tab.length; i++) { join += tab[i] + ( (i < tab.length - 1) ? ", " : "" ); }
 return(join);
 }

 public static String join(char[] tab) {
  String join = "";
  for(int i=0; i < tab.length; i++) { join += tab[i] + ( (i < tab.length - 1) ? ", " : "" ); }
 return(join);
 }

 public static String join(Object[] tab) {
  String join = "";
  for(int i=0; i < tab.length; i++) { join += tab[i] + ( (i < tab.length - 1) ? ", " : "" ); }
 return(join);
 }

 public static String[] split(String str, String delim) { return( split(str, delim, false) ); }

 public static String[] split(String str, String delim, boolean keepDelimAtEndOfTok) {
  if (str.indexOf(delim) == -1) { String[] ret2 = {str}; return(ret2); }

  stringTab ret = new stringTab();
  int nextTokenIdx = 0;
  int lastTokenIdx = 0;
  try {
   while( nextTokenIdx < str.length() ) {
    nextTokenIdx = str.indexOf(delim, lastTokenIdx);
    if (nextTokenIdx == -1) { ret.add( str.substring(lastTokenIdx) ); break; }
 
    if ( keepDelimAtEndOfTok) nextTokenIdx += delim.length();
    ret.add( str.substring(lastTokenIdx, nextTokenIdx) );
    lastTokenIdx = nextTokenIdx + ( (keepDelimAtEndOfTok) ? 0 : delim.length() );
   }
  } catch(Exception ex) { ex.printStackTrace(); }

 return(ret.toStringArray() );
 }

 public static String[] cutFirst(String str, String cutter) {
  String[] tmp  = new String[2];
  String str1   = new String(str);
  int    index  = -1;
  if( (index = str1.indexOf(cutter) ) > -1 ) {
   tmp[0] = str1.substring( 0, index );
   tmp[1] = str1.substring( index + cutter.length() );
  }
 return(tmp);
 }

 // ==================== ==================== ==================== ==================== ==================== ====================


 public static String replaceBy(String str, String[] oldE, String[] newE) {
  String tmp = new String(str);
  for( int i = 0; i < oldE.length; i++ ) {
   if (oldE[i] != null) tmp = replaceBy(tmp, oldE[i], newE[i]);
  }
 return(tmp);
 }

 /** <FONT style='color: #FF0000'> corriged le 27/10/04 cf bug si derniers chars de str = premiers chars de oldE !a verif quand - voir -faire mieux ! </FONT> */
 public static String replaceBy(String str, String oldE, String newE) {
  if ( str.indexOf(oldE) == -1) return(str);	// 1ere mesure a prendre pour ne pas parcourir tt le tableau inutilement

  // indexOf etant implemented a peut pres pareil, il ne sert a rien de l'utiliser pour booster le code !
  String retBuffer = "";
  char[] toReplace = oldE.toCharArray();
  char[] byReplace = newE.toCharArray();


  // new !!!!! trash - cf bug dur dernier char s'il correspond au premier char de la recherche
  char[] support   = (str+ ( toReplace[0] == '.' ? "*" : "." ) ).toCharArray();


  int found = 0;
  int i=0;
  for(; i < support.length; i++) {
   if( support[i] == toReplace[found] ) {
    found++;
    if (found == toReplace.length ) { retBuffer += newE; found = 0; }
   }
   else { i -= found; found = 0; retBuffer += support[i]; }
  }

  // new !!!!! trash
  retBuffer = retBuffer.substring( 0, retBuffer.length() -1  );

 return(retBuffer);
 }



}

