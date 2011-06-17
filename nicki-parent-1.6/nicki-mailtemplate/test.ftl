	
Test aus eDir
<html>
<head>
  <title>Welcome!</title>
</head>
<body>
  <h1>Welcome ${user.toUpperCase().length()}!</h1>
  <p>Our latest product:
  <a href="${latestProduct.url}">${latestProduct.name}</a>!
</body>
</html>  
<#if user.length()==8>
Hallo
</#if>

