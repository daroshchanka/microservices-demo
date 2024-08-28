package dmax.demo.documents.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Value("${dmax.openapi.dev-url}")
  private String devUrl;

  @Bean
  public OpenAPI myOpenAPI() {
    Server devServer = new Server();
    devServer.setUrl(devUrl);
    devServer.setDescription("Server URL in Development environment");

    Contact contact = new Contact();
    contact.setEmail("daroshachanka@gmail.com");
    contact.setName("dmax");

    License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

    Info info = new Info()
        .title("Image Generation Manager API")
        //.version("1.0")
        .contact(contact)
        .description("This API exposes endpoints to manage image generation.")
        .license(mitLicense);

    return new OpenAPI().info(info).servers(List.of(devServer));
  }
}
