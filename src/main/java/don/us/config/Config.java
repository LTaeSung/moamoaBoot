package don.us.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import util.file.FileController;
import util.file.HandleDays;

@Configuration
public class Config {
	@Bean
	public FileController fileController() {
		return new FileController();
	}
	
	@Bean
	public HandleDays days() {
		return new HandleDays();
	}
}
