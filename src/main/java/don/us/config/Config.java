package don.us.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import util.file.FileController;

@Configuration
public class Config {
	@Bean
	public FileController fileController() {
		return new FileController();
	}
}
