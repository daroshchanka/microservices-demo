package dmax.demo.documents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class DocumentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentsApplication.class, args);
	}

}
