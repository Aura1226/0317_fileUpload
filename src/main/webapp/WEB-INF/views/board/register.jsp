<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<button id="saveBtn">Register</button>

<script>

document.querySelector("#saveBtn").addEventListener("click", function(e){
	
	const arr = [
		{fileName:"aaa.jpg", uuid:"11111", uploadPath:"2021/03/18"},
		{fileName:"bbb.jpg", uuid:"22222", uploadPath:"2021/03/18"}
		
	]
	
	const obj = {title:"Title", content:"Content", writer:"user00", fileList: arr}
	
	fetch("/board/register", 
			{
		      method: 'post',
		      headers: {'Content-type' : 'application/json; charset=UTF-8'},
		      body: JSON.stringify(obj)
			})
	
	
}, false)

</script>

</body>
</html>