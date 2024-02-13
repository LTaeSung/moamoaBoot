package util.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

public class FileController {

	@Value("${realPath.registed_img_path}")
	private String registed_img_path;
	
	public FileNameVO upload(MultipartFile file, String registed_img_path) {
		if (file.isEmpty())
			return null;

		// 업로드시의 본래 파일 명
		FileNameVO vo = new FileNameVO();
		vo.setSaved_filename(file.getOriginalFilename());
		vo.setRegisted_img_path(registed_img_path);

		try {
			// 서버에 파일 저장
			File target_file = new File(vo.getRegisted_img_path() + "/" + vo.getSaved_filename());
			file.transferTo(target_file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return vo;
	}
	
	public void remove(String imgUrl) {
		Path filePath = Paths.get(registed_img_path + "/" + imgUrl);
		try {
			Files.deleteIfExists(filePath);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}