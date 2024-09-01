package dmax.demo.generatedfilesstorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GeneratedFilesStorageApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeneratedFilesStorageApplication.class, args);
	}

}
