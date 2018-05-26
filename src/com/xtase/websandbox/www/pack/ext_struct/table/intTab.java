package com.xtase.websandbox.www.pack.ext_struct.table;
public class intTab extends Tab {

 protected static final int DEFAULT_PADD = 8;

 protected volatile int realLength = 0;
 protected volatile int[] buffer = new int[0];

 public intTab() {
  this(0);
 }

 public intTab(int initialCapacity) {
  buffer = new int[initialCapacity];
 }

 public intTab cloneIt() {
  intTab ret = new intTab( size() );
  System.arraycopy(buffer, 0, ret.buffer, 0, size());  
  ret.realLength = realLength;
 return(ret);
 }

 /* lifo comportement */
 public void push(int v) { add(v); }
 public int pop()        { int ret = getLast(); removeLast(); return(ret); }
 public int peek()       { return( getLast() ); }



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

 public void add(int index, int v) {		// insert
  ensureCapacity(1);
  System.arraycopy(buffer, index, buffer, index+1, realLength-index);
  buffer[index] = v;
  realLength++;
 }



 public int get(int i) { return( buffer[i] ); }

 public void add(int v) {
  ensureCapacity(1);
  buffer[realLength] = v;
  realLength++;
 }

 public synchronized void remove(int index) {
  if (index > realLength) return;
  int[] newTab = new int[buffer.length];
  if (index > 0) System.arraycopy(buffer, 0, newTab, 0, index);
  realLength--;
  if (index < realLength) System.arraycopy(buffer, index+1, newTab, index, (realLength - index) );
  buffer = newTab;
 }

 public void insert(int index, int value) { add(index, value); }

 /** NOT YET IMPLEMENTED !! */
 public void removeAll(int value) {}

 protected void ensureCapacity(int howMany) {
  if ( ( realLength + howMany ) < buffer.length ) return;
  int newSize = ( realLength + howMany + DEFAULT_PADD );
  int[] newTab = new int[newSize];
  System.arraycopy(buffer, 0, newTab, 0, buffer.length);
  buffer = newTab;
 }

 protected int[] toIntArray() {
  int[] ret = new int[size()];
  System.arraycopy(buffer, 0, ret, 0, realLength);
 return(ret);
 }

 public int getLast() { return( buffer[realLength-1] ); }
 // ---------------------------------------------------------

 public intTab extract(int srcOff, int dstOff) { 
  intTab ret = new intTab( (dstOff-srcOff) + 1);
  for(int i=srcOff; i <= dstOff; i++) {
   ret.add( get(i) );
  }
 return(ret); 
 }

 public intTab extract(intTab positions) { 
 return( extract( positions.toIntArray() ) ); 
 }

 public intTab extract(int[] positions) { 
  intTab ret = new intTab(positions.length);
  for(int i=0; i < positions.length; i++) {
   ret.add( get(positions[i]) );
  }
 return(ret); 
 }
 // ---------------------------------------------------------

 public int count(int key) { return( scan(key).size() ); }

 /** 
  return indexes
 */
 public intTab scan(int toFind) { 
  intTab ret = new intTab();
  for(int i=0; i < realLength; i++) {
   if ( get(i) == toFind ) { ret.add(i); }
  }
 return(ret); 
 }

 public void purge() {
  if ( realLength <= buffer.length ) return;
  int newSize = ( realLength + DEFAULT_PADD );
  int[] newTab = new int[newSize];
  System.arraycopy(buffer, 0, newTab, 0, buffer.length);
  buffer = newTab;
  // to realLength
 }




 public void set(int index, int v) {
  if (index > realLength) { ensureCapacity(index - realLength);
                            realLength = index+1; }		// a verif
  buffer[index] = v;
 }

 public void set(int index, int[] v) {
  if (index+v.length-1 > realLength) { ensureCapacity(index+v.length-1 - realLength);
                                       realLength = index+v.length+1; }		// a verif
  for( int i=0; i < v.length; i++) { buffer[index] = v[i]; }
 }

 //----------------- SORT -------------------------------
 public int  compareChild(int faceA, int faceB) {
  if (faceA > faceB) return(1);
  if (faceA < faceB) return(-1);
  return(0);
 }

 public void swapChild(int faceA, int faceB) {
  int tmp_t = get(faceA);
  set(faceA, get(faceB) );
  set(faceB, tmp_t);
 }
 //----------------- /SORT -------------------------------

}
