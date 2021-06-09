<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>


	<hr />
	<button id="saveBtn">Register</button>
	<hr />
	<hr />


	<input type='file' name='uploadFile' multiple="multiple">
	<button id="uploadBtn">Upload</button>

	<ul class="uploadResult">
	</ul>


	<script>
	
	   const obj  = {
		  		  title:"Title", 
		  		  content:"Content",
		  		  writer:"user00",
		  		  fileList: arr}	
		  	
		    fetch("/board/register",
		  		  {
		  	  		method: 'post',
		  	  		headers: {'Content-type': 'application/json; charset=UTF-8'},
		  	  		body: JSON.stringify(obj)
		  		  })

		  }, false);

	
	const uploadUL = document.querySelector(".uploadResult");
	
	
		document.querySelector("#uploadBtn").addEventListener("click",
				function(e) {

					const formData = new FormData();

					const input = document.querySelector("input[name='uploadFile']");
					
					const files = input.files; //이부분 책과 다르다 책은 jQuery사용 우린 바닐라 js?
					
					console.dir(input);
					
					for(let i = 0; i < files.length; i++){
						formData.append("files", files[i]); //formData에 파일만 넣을 수 있는건 아니다. 태그 정보도 넣을 수 있다??
					}
					
					fetch("/upload", {method:"post", body:formData}) //fetch했으면 반드시 then
					.then(res => res.json())
					.then(jsonObj => {
						
						console.log(jsonObj);
						
						let htmlCode = "";
						for (let i = 0; i < jsonObj.length; i++) {
							let fileObj = jsonObj[i];  //배열 안에 json문자열이 들어 있다..?
							                                  //썸네일 이미지를 원본 이미지로 출력하고 싶으면 .thumbLink를 .thumbLink.link로
							htmlCode += "<li id='li_"+fileObj.uuid+"'><img src='/view?file=" + fileObj.thumbLink +"'> " + 
									"<button onclick='removeFile("+ JSON.stringify(fileObj) +")'>DEL</button></li>";
							        //JSON.stringify(fileObj) JSON데이터를 문자열해주면 객체하나하나에 ""이 붙어서 객체처럼 됨(객체리터럴?)
							        //stringify를 안해주면 오브젝트...
									//JSON데이터를 쓸 떈 쿼테이션 ("" / '')을 조심해야한다.
						}
						uploadUL.innerHTML += htmlCode;
						
						
					})
					
				}, false);
		
		function removeFile(param) {
			console.log(param) //param? JSON 문자열로 파라미터를 던져서? 음...? 
			
			alert("REMOVE FILE")
			
			//fetch날릴 땐 당연히 URL부터
			fetch("/removeFile", 
					{
				      method: 'delete',
				      headers: {'Content-type' : 'application/json; charset=UTF-8'},
				      body: JSON.stringify(param)
					})
			
			document.querySelector("#li_" + param.uuid).remove()  //toDO의 중요성? #?
					//"#"만 해주면 id가 숫자로 시작하는 경우가 생기기때문에 li id에 "li_"같은 문자열을 넣어줘서 해결
		}
		
	</script>


</body>
</html>