package dmax.demo.imagegenerator;

import dmax.demo.documents.feign.DocumentsClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients(clients = DocumentsClient.class)
@SpringBootApplication
public class ImageGeneratorApplication {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(ImageGeneratorApplication.class);
    application.setWebApplicationType(WebApplicationType.NONE);
    application.run(args);
  }

}
