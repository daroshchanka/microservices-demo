package dmax.demo.imagegenerator;

import dmax.demo.documents.feign.DocumentsClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(clients = DocumentsClient.class)
@SpringBootApplication
public class ImageGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageGeneratorApplication.class, args);
	}

}
