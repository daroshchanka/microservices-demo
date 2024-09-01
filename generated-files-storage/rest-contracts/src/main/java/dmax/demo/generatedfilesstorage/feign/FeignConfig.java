package dmax.demo.generatedfilesstorage.feign;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FeignConfig {

  @Bean
  public Encoder multipartFormEncoder() {
    return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
      @Override
      public HttpMessageConverters getObject() throws BeansException {
        return new HttpMessageConverters(new RestTemplate().getMessageConverters());
      }
    }));
  }

  @Bean
  public Decoder jsonDecoder() {
    return new JacksonDecoder();
  }
}
