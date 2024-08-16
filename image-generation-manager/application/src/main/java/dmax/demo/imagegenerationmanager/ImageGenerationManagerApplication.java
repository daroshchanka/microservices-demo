package dmax.demo.imagegenerationmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ImageGenerationManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageGenerationManagerApplication.class, args);
	}

}
