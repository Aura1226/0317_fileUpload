package org.donghyun.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.donghyun.dto.AttachFileDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;

@RestController
@Log4j
public class UploadController {//파일업로드 경로 설정
	
	@GetMapping("/view")
	public ResponseEntity<byte[]> viewFile(String file){
		
		byte[] result = null;
		
		ResponseEntity<byte[]> res = null;
		
		try {
			String deStr = URLDecoder.decode(file, "UTF-8");
			
			String originStr = deStr.replace("#",".");
			
			log.info(originStr);
			
			File targetFile = new File("C:\\upload\\"+ originStr);
			
			//MIME 
			String mimeType = Files.probeContentType(targetFile.toPath());
			
			HttpHeaders header = new HttpHeaders();
			header.add("Content-Type",mimeType);
			
			res = new ResponseEntity<>(
					FileCopyUtils.copyToByteArray(targetFile),
					header,
					HttpStatus.OK
					);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return res;
	}
	//REST Api에서 가장 많이 보는 에러느 415에러다...전달되는 데이터 포맷이 안맞을 때 일어난다. => 처음부터 적용하지말고...
	@DeleteMapping("/removeFile")
	public ResponseEntity<String> removeFile(@RequestBody AttachFileDTO dto){
		
		log.info(dto);
		
		log.info("remove...........");
		
		String filePath = "C:\\upload\\" + dto.getUploadPath();
		String fileName = dto.getUuid()+"_"+dto.getFileName();
		
		if(dto.isImage()) {
			
			File thumb = new File(filePath+File.separator+"s_"+fileName);
			thumb.delete();
			
		}
		
		File target = new File(filePath+File.separator+fileName);
		target.delete();//then같은것도 적용해야겠지? 해봐...
		
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}
	
	//파일 업로드는 GET방식 불가
	@PostMapping("/upload")//1단계 경로 지정(업로드 위치, URL 경로)
	public ResponseEntity<List<AttachFileDTO>> upload(MultipartFile[] files){ //이름을 맞춰줘야 하는데(files로 ) 그게 싫고 새로운 이름을 주고 싶다면
		                                                       //MultipartFile[]앞에 @RequestParam("새 이름")해주면 된다. 
		String uploadFolder = "C:\\upload";//업로드할 폴더 경로
		
		log.info("---------------------");

		String folderPath = getFolder();//지정한 업로드 경로에 uuid와 mkdirs()로 체크한 폴더(yy-MM-dd)를 실행해서 folderPath에 담아준다(upload폴더가 없다면 그것까지 생성해서)
		
		File uploadPath = new File(uploadFolder, getFolder()); //업로드할 폴더 경로와 중복이름 방지와 정리를 위해 설정한 폴더를 File에 적용해서uploadPath에 담는다
		
		
		if (uploadPath.exists() == false) {//만약, uploadPath에 설정한 폴더가 없다면 설정한대로 폴더들을 자동으로 만들어준다.
			uploadPath.mkdirs();
		}//mkdirs(); 얘가 uuid 설정해준대로(new SimpleDateFormat("yyyy-MM-dd");) 상위폴더까지 한번에 생성해준다. upload폴더가 없다면 upload까지 만들어줌
		
		//null이나 length 체크
		
		List<AttachFileDTO> list = new ArrayList<>();//AttachFileDTO을 가져와 리스트 타입에 배열로 담는다
		
		for (int i = 0; i < files.length; i++) {//업로드할 파일의 개수 만큼 for루프를 돌려 mfile에 배열로 담는다
			MultipartFile mfile = files[i];
			
			//log.info(mfile.getOriginalFilename()); //getContentType해주면 마임타입을 알 수 있다.
			//log.info(mfile.getSize());
			String fileName = mfile.getOriginalFilename();//mfile에 담긴 파일의 원본명을 fileName에 String타입으로 담아준다.
			
			UUID uuid = UUID.randomUUID();//임의의 UUID값을 생성
			
			//log.info(mfile.getContentType()); //마임타입(MIME)이 나온다는게 굉장히 중요한 의미를 가지기 때문에 설명을 위해 확인
			boolean isImage = mfile.getContentType().startsWith("image");//마임타입을 확인한것을 이용해서 mfile이 image인것을 
			                                                      //isImage에 담는다 이것을 썸네일에 이용할 것
			
			//1> File saveFile = new File(uploadFolder, fileName); //새 File을 uploadFolder, fileName을 적용해 만들어 saveFile에 넣어줌
			
			//설정한 경로에 폴더가 없을시 아래서 new SimpleDateFormat("yyyy-MM-dd"); 해준 형식대로 상위폴더부터 자동으로 생성해주기 위해
			//2> File saveFile = new File(uploadPath, fileName); //임의의 UUID값을 줘서 중복 파일명을 방지하기 위해 아래걸로
			File saveFile = new File(uploadPath, uuid.toString()+ "_" + fileName);//임의의 값으로 정해준 uuid 값과 원본 파일명사이에 _로 구분해준다.
			
			try {
				mfile.transferTo(saveFile);//mfile에 담긴 파일들의 이름을 saveFile의 형식에 맞춰준다.
				
				if (isImage) {//여기가 썸네일을 만들어주는 if조건문
					
					FileOutputStream fos = new FileOutputStream(
							new File(uploadPath, "s_" + uuid.toString()+ "_" + fileName));
					
					Thumbnailator.createThumbnail(mfile.getInputStream(), fos, 100, 100);//Thumbnailator를 이용해서 mfile
					fos.close();
				}
				
				AttachFileDTO attachFileDTO = 
						new AttachFileDTO(fileName, folderPath, uuid.toString(), isImage);
				
				list.add(attachFileDTO);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String str =  sdf.format(new Date());
		
		return str.replace("-", File.separator);
	}
	
}
