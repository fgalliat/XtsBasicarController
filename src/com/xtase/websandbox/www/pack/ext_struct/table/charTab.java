package com.xtase.websandbox.www.pack.ext_struct.table;
public class charTab extends Tab {

 protected static final int DEFAULT_PADD = 8;

 protected volatile int realLength = 0;
 protected volatile char[] buffer = new char[0];

 public charTab(String value) { new charTab(value.length() ); add(value.toCharArray() ); }

 public charTab() {
  this(0);
 }

 public charTab(int initialCapacity) {
  buffer = new char[initialCapacity];
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

 public void add(char v) {
  ensureCapacity(1);
  buffer[realLength] = v;
  realLength++;
 }

 public void add(char[] v) {
  ensureCapacity(v.length);
  for(int i=0; i < v.length; i++) { buffer[realLength + i] = v[i]; }
  realLength+=v.length;
 }

 // -------------------------------------------------------
 public boolean equals(Object o) { return( equals( (charTab)o ) ); }
 public boolean equals(String o) { return( equals( new charTab(o) ) ); }
 public boolean equals(charTab o) {
  if (o.realLength != realLength) return(false);
  for(int i=0; i < realLength; i++) { if (buffer[i] != o.buffer[i]) { return(false); } }
 return(true);
 }

 public boolean startsWith(String str) { return( startsWith( new charTab(str) ) ); }

 public boolean startsWith(charTab tab) {
  if (tab.realLength > this.realLength) { return(false); }
 return( extract(0, tab.realLength - 1).equals( tab ) );
 }

 public boolean endsWith(String str) { return( startsWith( new charTab(str) ) ); }

 public boolean endsWith(charTab tab) {
  if (tab.realLength > this.realLength) { return(false); }
 return( extract( (realLength - tab.realLength - 1) , realLength - 1).equals( tab ) );
 }

 public int indexOf(String expr) { return(indexOf( new charTab(expr) ) ); }

 public int indexOf(String expr, int from) { return(indexOf( new charTab(expr) , from)); }

 public int indexOf(charTab expr) { return(indexOf(expr, 0)); }

 public int indexOf(charTab expr, int from) { 
  if (from + expr.realLength > this.realLength) { return(-1); }

  int count = 0;
  for(int i=from; i < (this.realLength-expr.realLength); i++) { 
   if (buffer[i] != expr.buffer[count]) { count = -1; }
   count++;
   if (count == expr.realLength) { return(i); }
  }

 return(-1);
 }

 // -------------------------------------------------------


 public void set(int index, char v) {
  if (index > realLength) { ensureCapacity(index - realLength);
                            realLength = index+1; }		// a verif
  buffer[index] = v;
 }

 public void set(int index, char[] v) {
  if (index+v.length-1 > realLength) { ensureCapacity(index+v.length-1 - realLength);
                                       realLength = index+v.length+1; }		// a verif
  for( int i=0; i < v.length; i++) { buffer[index] = v[i]; }
 }

 protected char[] toCharArray() {
  char[] ret = new char[size()];
  System.arraycopy(buffer, 0, ret, 0, realLength);
 return(ret);
 }

 // ---------------------------------------------------------

 public void substract(int srcOff, int dstOff) { 
  for(int i=srcOff; i <= dstOff; i++) {
   remove( srcOff );
  }
  //System.out.println(getClass().getName()+".substract(int srcOff, int dstOff) TO BE VERIFIED");
 }

 public void substract(intTab positions) { 
  substract( positions.toIntArray() ); 
 }

 public void substract(int[] positions) { 		// a tester
 /*
  for(int i=0; i < positions.length; i++) {
   ret.remove( get(positions[i]) );
  }
*/ System.out.println(getClass().getName()+".substract(int[] positions) NOT YET IMPLEMENTED");
 }
 // -----------------------------------------

 public charTab extract(int srcOff, int dstOff) { 
  charTab ret = new charTab( (dstOff-srcOff) + 1);
  for(int i=srcOff; i <= dstOff; i++) {
   ret.add( get(i) );
  }
 return(ret); 
 }

 public charTab extract(intTab positions) { 
 return( extract( positions.toIntArray() ) ); 
 }

 public charTab extract(int[] positions) { 
  charTab ret = new charTab(positions.length);
  for(int i=0; i < positions.length; i++) {
   ret.add( get(positions[i]) );
  }
 return(ret); 
 }
 // ---------------------------------------------------------


 public void add(int index, char v) {		// insert
  ensureCapacity(1);
  System.arraycopy(buffer, index, buffer, index+1, realLength-index);
  buffer[index] = v;
  realLength++;
 }

 public char get(int i) { return( buffer[i] ); }

 public synchronized void remove(int index) {
  if (index > realLength) return;
  char[] newTab = new char[buffer.length];
  if (index > 0) System.arraycopy(buffer, 0, newTab, 0, index);
  realLength--;
  if (index < realLength) System.arraycopy(buffer, index+1, newTab, index, (realLength - index) );
  buffer = newTab;
 }

 public void insert(int index, char value) { add(index, value); }

 /** NOT YET IMPLEMENTED !! */
 public void removeAll(char value) {}

 protected void ensureCapacity(int howMany) {
  if ( ( realLength + howMany ) < buffer.length ) return;
  int newSize = ( realLength + howMany + DEFAULT_PADD );
  char[] newTab = new char[newSize];
  System.arraycopy(buffer, 0, newTab, 0, buffer.length);
  buffer = newTab;
 }


 public char getLast() { return( buffer[realLength-1] ); }
 // ---------------------------------------------------------


 /* lifo comportement */
 public void push(char v) { add(v); }
 public char pop()        { char ret = getLast(); removeLast(); return(ret); }
 public char peek()       { return( getLast() ); }


 public int indexOfCharInTab(char key)           { return(indexOfCharInTab(key, 0) ); }
 public int indexOfCharInTab(char key, int from) {
  for (int i=from; i < realLength; i++) { if ( buffer[i] == key ) { return(i); } }
 return(-1);
 }

 public int count(char key) { return( scan(key).size() ); }
 
 /** 
  return indexes
 */
 public intTab scan(char toFind) { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) {
   if ( get(i) == toFind ) { ret.add(i); }
  }
 return(ret); 
 }


 //----------------- SORT -------------------------------
 public int  compareChild(int faceA, int faceB) {
  if (faceA > faceB) return(1);
  if (faceA < faceB) return(-1);
  return(0);
 }

 public void swapChild(int faceA, int faceB) {
  char tmp_t = get(faceA);
  set(faceA, get(faceB) );
  set(faceB, tmp_t);
 }
 //----------------- /SORT -------------------------------

 public String toString() { return( new String(buffer, 0, realLength) ); }

}
