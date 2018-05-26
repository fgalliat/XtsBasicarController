package sys;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

public abstract class coupleStream {

 protected system sysParent = null;

 protected Object       attached = null;
 protected InputStream  data_in  = null;
 protected OutputStream data_out = null;

 protected BufferedReader data_buff_in  = null;
 protected PrintStream    data_buff_out = null;

 public coupleStream() {
 }


 public InputStream  getInputStream()  { return(data_in);  }
 public OutputStream getOutputStream() { return(data_out); }

 public void   setAttached(Object attached) { this.attached = attached; }
 public Object getAttached()                { return(attached); }

 public void   setSysParent(system sys) { this.sysParent = sys; }
 public system getSysParent()           { return(sysParent); }

//-----------[ I/O open-close java ]----------------------

 public void setInputStream(InputStream in)    {
  try {
   closeIN();
   this.data_in  = in;
   if (data_in != null) this.data_buff_in = new BufferedReader( new InputStreamReader(data_in));
  }
  catch(Exception e) { error(e); }
 }

 public void setOutputStream(OutputStream out) {
  try {
   closeOUT();
   this.data_out = out;
   if (data_out != null) this.data_buff_out = new PrintStream( data_out );
  }
  catch(Exception e) { error(e); }
 }

 public void closeIN() {
  try {
   if (data_buff_in != null) data_buff_in.close();
   if (data_in != null)      data_buff_in.close();
  }
  catch(Exception e) { error(e); }
 }

 public void closeOUT() {
  try {
   if (data_buff_out != null) data_buff_out.close();
   if (data_out != null)      data_buff_out.close();
  }
  catch(Exception e) { error(e); }
 }

 public void close() {
  closeIN();
  closeOUT();
 }

//-----------[ I/O open-close ecma ]----------------------

 public abstract void initIN();
 public abstract void initOUT();



//-----------[ I/O use ecma ]-----------------------------

 public void writeByte(int bts) {
  try { data_out.write(bts); data_out.flush(); }
  catch(Exception e ) { error(e); };
 }

 public void print(String msg) {
  try { data_buff_out.print(msg); data_out.flush(); }
  catch(Exception e ) { error(e); };
 }

 public int getByte() {
  try { return(data_in.read() ); }
  catch(Exception e ) { error(e); };
 return(-1);
 }

 public String getLine() {
  try { return(data_buff_in.readLine() ); }
  catch(Exception e ) { error(e); };
 return(null);
 }

//-----------[ Internal routines ]------------------------

 protected void error(Exception e) {
  System.err.println(e.getMessage() );
 }

}

