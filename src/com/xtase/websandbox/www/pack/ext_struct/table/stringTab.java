package com.xtase.websandbox.www.pack.ext_struct.table;

import java.util.Enumeration;

import com.xtase.websandbox.www.pack.common.data.string.StringUtils;

public class stringTab extends Tab {

 protected static final int DEFAULT_PADD = 8;

 protected volatile int realLength = 0;
 protected volatile String[] buffer = new String[0];

 protected java.util.Random rnd = new java.util.Random( System.currentTimeMillis() );
 protected int rnd(int max) { return(  rnd.nextInt(max) ); }

 public stringTab() {
  this(0);
 }

 public stringTab(int initialCapacity) {
  this(new String[initialCapacity]);
  realLength = 0;	// forced
 }

 public stringTab(String[] initial) {
  realLength = initial.length;
  buffer     = new String[realLength];
  System.arraycopy(initial, 0, buffer, 0, initial.length-0);  
 }

 public stringTab(Enumeration enume) {
  this(0);
  while( enume.hasMoreElements() ) { add( enume.nextElement().toString() ); }
 }


 // --------------- same :( ---------------------------------
 public int size() { return( realLength ); }
 public boolean empty() { return( realLength == 0 ); }
 public String join(String with, boolean accollade) {
  String ret = "";
  if (accollade) ret +="{";
  String tmp;
  for(int i=0; i < realLength; i++) { tmp = ""+buffer[i]; ret += ( (!tmp.equals(with)) ? tmp : "\\"+tmp) + with; }
  if (ret.length() > 1) ret = ret.substring(0, ret.length() - with.length()  );
  if (accollade) ret +="}";
 return( ret ); 
 }
 // ---------------------------------------------------------

 public stringTab revert() {
  stringTab ret = new stringTab( realLength );
  for(int i=0; i < realLength; i++) { ret.add( ""+ this.get( (realLength-1) - i ) ); }
 return(ret);
 }

 public stringTab cloneIt() {
  stringTab ret = new stringTab( size() );
  System.arraycopy(buffer, 0, ret.buffer, 0, size());  
  ret.realLength = realLength;
 return(ret);
 }

 public void set(int index, String s) {
  if (index > realLength) { ensureCapacity(index - realLength);
                            realLength = index+1; }		// a verif
  buffer[index] = s;
 }


 public void add(String v) {
  ensureCapacity(1);
  buffer[realLength] = v;
  realLength++;
 }

 public void add(int index, String v) {		// insert
  ensureCapacity(1);
  //System.out.println(". "+index+" "+v+" "+realLength+" "+join() );
  System.arraycopy(buffer, index, buffer, index+1, realLength-index);
  buffer[index] = v;
  realLength++;
 }

 public synchronized void remove(int index) {
  if (index > realLength) return;
  String[] newTab = new String[buffer.length];
  if (index > 0) System.arraycopy(buffer, 0, newTab, 0, index);
  realLength--;
  if (index < realLength) System.arraycopy(buffer, index+1, newTab, index, (realLength - index) );
  buffer = newTab;
 }

 public void removeAll(String value) { this.buffer = substract( scan( value ) ).toStringArray(); }

 public void purge() {
  buffer = cutArray(buffer, ( realLength + DEFAULT_PADD ) );
 }

 protected void ensureCapacity(int howMany) {
  if ( ( realLength + howMany ) < buffer.length ) return;
  int newSize = ( realLength + howMany + DEFAULT_PADD );
  String[] newTab = new String[newSize];
  System.arraycopy(buffer, 0, newTab, 0, buffer.length);
  buffer = newTab;
 }

 //----------------- SORT -------------------------------
 public void swapChild(int faceA, int faceB) {
  String tmp_t = get(faceA);
  set(faceA, get(faceB) );
  set(faceB, tmp_t);
 }
 //----------------- /SORT -------------------------------

 public String[] toStringArray() {
  String[] ret = new String[size()];
  System.arraycopy(buffer, 0, ret, 0, realLength);
 return(ret);
 }

 // ---------------------------------------------------------

 // ----------------------------------------------------
 public static stringTab makeFromSplit(String delim, String toCut) {
 return( new stringTab( StringUtils.split(toCut, delim) ) );
 }


 // ----------------------------------------------------

 public stringTab extract(int srcOff, int dstOff) { 
  stringTab ret = new stringTab( (dstOff-srcOff) + 1);
  for(int i=srcOff; i <= dstOff; i++) {
   ret.add( get(i) );
  }
 return(ret); 
 }

 public stringTab extract(intTab positions) { 
 return( extract( positions.toIntArray() ) ); 
 }

 /** keep in a copy */
 public stringTab extract(int[] positions) { 
  stringTab ret = new stringTab(positions.length);
  for(int i=0; i < positions.length; i++) {
   int recToExtract = positions[i];
   ret.add( get( recToExtract ) );
  }
 return(ret); 
 }

 public stringTab substract(intTab positions) { 
  intTab tmp = positions.cloneIt();
  tmp.sort();
 return( substract( tmp.toIntArray() ) ); 
 }

 /** remove in a copy, positions must be sorted */
 public stringTab substract(int[] positions) { 
  stringTab ret = new stringTab(size() - positions.length);
  int index = 0;
  int max = positions.length-1;
  for(int i=0; i < size(); i++) {
   if (i == positions[index]) { if(index < max) index++; }
   else ret.add( get( i ) );
  }
 return(ret); 
 }

 /** remove in a copy, positions must be sorted */
 public stringTab substract(int srcOff, int dstOff) { 
  stringTab ret = cloneIt();
  for(int i=srcOff; i <= dstOff; i++) {
   ret.remove( srcOff );
  }
 return(ret); 
 }

 // ---------------------------------------------------------

// -------------------------------------------------------

 public void trimAllChildren() { 
  for(int i=0; i < realLength; i++) { buffer[i] = buffer[i].trim(); }	// direct 4 perf. issues
 }


 public void paragraphChildren(int width, boolean cesure) {
  int i = 0;
  boolean toto = true;
  while(toto) { 
   if( buffer[i] != null && buffer[i].length() >= width) {
    if (cesure) {}
    else        { String tmp = buffer[i].substring(width);
                  buffer[i]  = buffer[i].substring(0, width);
                  add(i+1, tmp);
                }
   }
   i++; if (i >= realLength) break;
  }
 }


 // ------------- don't directly override buffer.values ---------------------------------------------

 public void set(int index, charTab c) {
  set(index, c.toString() );
 }

 //----------------- SORT ------------------------------- // !! PSION !!
 public int  compareChild(int faceA, int faceB) { return( get(faceA).compareTo( get(faceB) ) ); }



 public void insert(int index, String value) { add(index, value); }

 public String get(int i)      { return( buffer[i] ); }
 public charTab getAsRec(int i) { return( new charTab( get(i) ) ); }

 /** return the reference, not a clone */
 public String getLast() { return( buffer[realLength-1] ); }

 /* lifo comportement */
 public void push(String v) { add(v); }
 public String pop()        { String ret = getLast(); removeLast(); return(ret); }
 public String peek()       { return( getLast() ); }


 public boolean contains(String key) { return(indexOfStringInTab(key) > -1); }
 public String getOneOf() { return( get( rnd( size() ) ) ); }

 public int indexOf(String key)                      { return(indexOfStringInTab(key, 0) ); }
 public int indexOfStringInTab(String key)           { return(indexOfStringInTab(key, 0) ); }
 public int indexOfStringInTab(String key, int from) {
  for (int i=from; i < realLength; i++) { if ( buffer[i].equals(key) ) { return(i); } }
 return(-1);
 }

 public int count(String key) { return( scan(key).size() ); }
 
 /** cf scan not */
 public stringTab exclude(stringTab tab) {
  stringTab ret = cloneIt();
  for (int toEvit=0; toEvit < tab.size(); toEvit++) { 
   intTab linesToKeep = ret.scanNot( tab.get(toEvit) );
   ret = ret.extract( linesToKeep ); 
  }
  ret.purge();
 return(ret);
 }

 /** don't modify tab content */
 public intTab scanEmpty() { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) { if ( get(i).trim().length() == 0) { ret.add(i); } }
 return(ret); 
 }

 /** don't modify tab content */
 public intTab scanNotEmpty() { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) { if ( get(i).trim().length() > 0) { ret.add(i); } }
 return(ret); 
 }

 /** 
  return indexes
 */
 public intTab scan(String toFind) { return(scan(toFind, false)); }
 public intTab scan(String toFind, boolean ignoreCase) { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) {
   if ( ((ignoreCase) ? get(i).toLowerCase() : get(i) ).equals( ((ignoreCase) ? toFind.toLowerCase() : toFind ) ) ) { ret.add(i); }
  }
 return(ret); 
 }

 public intTab scanNot(String toFind) { return(scanNot(toFind, false)); }
 public intTab scanNot(String toFind, boolean ignoreCase) { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) {
   boolean retained = ( ! ((ignoreCase) ? get(i).toLowerCase() : get(i) ).equals( ((ignoreCase) ? toFind.toLowerCase() : toFind ) ) );
   //System.out.println(retained);
   if (retained) { ret.add(i); }
  }
 return(ret); 
 }

 public intTab scanIn(String toFind) { return(scanIn(toFind, false)); }
 public intTab scanIn(String toFind, boolean ignoreCase) { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) {
   if ( ((ignoreCase) ? get(i).toLowerCase() : get(i) ).indexOf( ((ignoreCase) ? toFind.toLowerCase() : toFind ) ) > -1 ) { ret.add(i); }
  }
 return(ret); 
 }

 public intTab scanNotIn(String toFind) { return(scanNotIn(toFind, false)); }
 public intTab scanNotIn(String toFind, boolean ignoreCase) { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) {
   if ( ((ignoreCase) ? get(i).toLowerCase() : get(i) ).indexOf( ((ignoreCase) ? toFind.toLowerCase() : toFind ) ) == -1 ) { ret.add(i); }
  }
 return(ret); 
 }


 public intTab scanStartsWith(String toFind) { return(scanStartsWith(toFind, false)); }
 public intTab scanStartsWith(String toFind, boolean ignoreCase) { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) {
   if ( ((ignoreCase) ? get(i).toLowerCase() : get(i) ).startsWith( ((ignoreCase) ? toFind.toLowerCase() : toFind ) ) ) { ret.add(i); }
  }
 return(ret); 
 }

 public intTab scanEndsWith(String toFind) { return(scanEndsWith(toFind, false)); }
 public intTab scanEndsWith(String toFind, boolean ignoreCase) { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) {
   if ( ((ignoreCase) ? get(i).toLowerCase() : get(i) ).endsWith( ((ignoreCase) ? toFind.toLowerCase() : toFind ) ) ) { ret.add(i); }
  }
 return(ret); 
 }

 public stringTab cutBefore(String toFind) { return( cutBefore(toFind, false) ); }
 public stringTab cutBefore(String toFind, boolean ignoreCase) { 
  stringTab ret = new stringTab( size() );
  for(int i=0; i < realLength; i++) {
   int index = ((ignoreCase) ? get(i).toLowerCase() : get(i) ).indexOf( ((ignoreCase) ? toFind.toLowerCase() : toFind ) );
   if ( index == -1 ) { ret.add( get(i) ); }
   else               { ret.add( get(i).substring(0, index) ); }
  }
 return(ret); 
 }

 // a tester
 public stringTab cutAfter(String toFind) { return( cutAfter(toFind, false) ); }
 public stringTab cutAfter(String toFind, boolean ignoreCase) { 
  stringTab ret = new stringTab( size() );
  for(int i=0; i < realLength; i++) {
   int index = ((ignoreCase) ? get(i).toLowerCase() : get(i) ).indexOf( ((ignoreCase) ? toFind.toLowerCase() : toFind ) );
   if ( index == -1 ) { ret.add( get(i) ); }
   else               { ret.add( get(i).substring(index+toFind.length() + 1) ); }
  }
 return(ret); 
 }

 // tested en '0' insert IN a child
 /* returns a new stringTab wich contains the same values but with a string 'toFill' inserted in all children  */
 public stringTab insertAt(int index, String toFill) { 
  stringTab ret = new stringTab( size() );
  for(int i=0; i < realLength; i++) {
   String str = get(i);
   if ( index > str.length() ) { ret.add( get(i)+""+toFill ); }
   else                        { ret.add( get(i).substring(0, index) + toFill + get(i).substring(index) ); }
  }
 return(ret); 
 }

 public void importArray(String[] recs) {
  if (recs == null) return;
  for(int i=0; i < recs.length; i++) { add(recs[i]); }
 }





}
