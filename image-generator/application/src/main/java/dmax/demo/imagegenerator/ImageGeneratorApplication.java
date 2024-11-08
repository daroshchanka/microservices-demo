package dmax.demo.imagegenerator;

import dmax.demo.generatedfilesstorage.feign.GeneratedFilesStorageClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients(clients = GeneratedFilesStorageClient.class)
@SpringBootApplication
public class ImageGeneratorApplication {

  public static void main(String[] args) {
    SpringApplication.run(ImageGeneratorApplication.class, args);
  }

}
