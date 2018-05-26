package com.xtase.websandbox.www.pack.ext_struct.table;
public abstract class Tab {

 public abstract int size();

 public String toString() { return( join() ); }

 public String join() { return(join(',') ); }
 public String join(char with) { return(join(with+" ", true) ); }
 public String join(String with) { return(join(with, true) ); }
 public String join(char with, boolean accollade) { return(join(with+" ", true) ); }
 public abstract String join(String with, boolean accollade);


 // public *** getLast();
 //public void add(***)		// pbm cf types simples interface peut util !!! fais chier !


 public abstract void remove(int index);
 public void removeLast()  { remove( size() - 1 ); }
 public void removeFirst() { remove( 0 ); }

 //----------------- SORT -------------------------------
 public void sort() {
  sort(0, size()-1 ); // !! -1 tres important !!
 }

 /* Quick Sort implementation */
 protected void sort(int left, int right) {
  int leftIndex = left;
  int rightIndex = right;
  int partionElement;

  if ( right > left) {
   partionElement = ( left + right ) / 2;
   while( leftIndex <= rightIndex ) {
    while( ( leftIndex < right ) && ( compareChild(leftIndex, partionElement) < 0 ) ) ++leftIndex;
    while( ( rightIndex > left ) && ( compareChild(rightIndex, partionElement) > 0 ) ) --rightIndex;
    if( leftIndex <= rightIndex ) {  swapChild( leftIndex, rightIndex);
                                     ++leftIndex;
                                     --rightIndex; }
   }
   if( left < rightIndex ) sort( left, rightIndex );
   if( leftIndex < right ) sort( leftIndex, right );
  }
 }

 public abstract void swapChild(int faceA, int faceB);
 public abstract int  compareChild(int faceA, int faceB);
 //----------------- /SORT -------------------------------

 protected static String[] cutArray(String[] array, int toLength) {
  if ( toLength >= array.length ) return(array);
  String[] arrayDest = new String[ toLength ];
  System.arraycopy(array, 0, arrayDest, 0, toLength);
 return(arrayDest);
 }

 protected static int[] cutArray(int[] array, int toLength) {
  if ( toLength >= array.length ) return(array);
  int[] arrayDest = new int[ toLength ];
  System.arraycopy(array, 0, arrayDest, 0, toLength);
 return(arrayDest);
 }


}
