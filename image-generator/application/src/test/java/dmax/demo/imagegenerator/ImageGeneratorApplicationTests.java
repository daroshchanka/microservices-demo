package dmax.demo.imagegenerator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"})
@SpringBootTest
class ImageGeneratorApplicationTests {

	@Test
	void contextLoads() {
	}

}
