package com.xtase.websandbox.www;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

import sys.system;

public class JavascriptParser {


 protected Context    cx         = null;
 protected Scriptable scope      = null;
 protected boolean    quitting   = false;
 protected String     scriptPath = null;

 protected system systemX = null;

 public JavascriptParser(String scriptPath) throws Exception {
  this.scriptPath = scriptPath;
  cx = Context.enter();
  scope = cx.initStandardObjects( new ImporterTopLevel() );
  systemX = new system(cx, scope);
  systemX.setDebug(false);
  scope.put("systemX", scope, systemX);
 }

 public void addReference(String ref, Object dest) {
  scope.put(ref, scope, dest);
 }

 public String evaluate(String str) throws Exception {
  return( systemX.evaluteExpression(str) );
 }

 public void launch() throws Exception {
  systemX.boot(scriptPath);
  Context.exit();
 }

 public void dispose() {
  try { Context.exit(); } catch(Exception ex) {}
 }

}

