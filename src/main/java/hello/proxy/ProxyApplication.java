package hello.proxy;
import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @Component 스캔 시작 패키지 대상을 hello.proxy.app 으로 함 -> config 패키지에 있는 @Configuration 은 컴포턴스 스캔 되지 않음
 * @Configuration 이 자동 등록 되지 안으니 이렇게 @Import 를 사용해 수동으로 등록
 */
@SpringBootApplication(scanBasePackages = "hello.proxy.app")
@Import({AppV1Config.class, AppV2Config.class})
public class ProxyApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}


}
