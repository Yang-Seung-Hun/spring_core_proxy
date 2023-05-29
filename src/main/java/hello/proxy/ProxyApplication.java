package hello.proxy;
import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v1_proxy.ConcreteProxyConfig;
import hello.proxy.config.v1_proxy.InterfaceProxyConfig;
import hello.proxy.config.v2_dynamicproxy.DynamicProxyBasicConfig;
import hello.proxy.config.v2_dynamicproxy.DynamicProxyFilterConfig;
import hello.proxy.config.v3_proxyfactory.ProxyFactoryConfigV1;
import hello.proxy.config.v3_proxyfactory.ProxyFactoryConfigV2;
import hello.proxy.config.v4_postprocessor.BeanPostProcessorConfig;
import hello.proxy.config.v5_autoproxy.AutoProxyConfig;
import hello.proxy.config.v6_aop.AopConfig;
import hello.proxy.trace.logtrace.LogTrace;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @Component 스캔 시작 패키지 대상을 hello.proxy.app 으로 함 -> config 패키지에 있는 @Configuration 은 컴포턴스 스캔 되지 않음
 * @Configuration 이 자동 등록 되지 안으니 이렇게 @Import 를 사용해 수동으로 등록
 */
@SpringBootApplication(scanBasePackages = "hello.proxy.app")
//@Import({AppV1Config.class, AppV2Config.class})
//@Import(InterfaceProxyConfig.class) // v1에 프록시 적용
//@Import(ConcreteProxyConfig.class) // v2에 프록시 적용
//@Import(DynamicProxyBasicConfig.class) // JDK 동적 프록시 적용
//@Import(DynamicProxyFilterConfig.class) // JDK 동적 프록시 적용 (no-log 해결)
//@Import(ProxyFactoryConfigV1.class) // ProxyFactory 적용
//@Import(ProxyFactoryConfigV2.class) // ProxyFactory 적용
//@Import(BeanPostProcessorConfig.class)// 빈후처리기 v3 적용
//@Import(AutoProxyConfig.class) // 스프링에서 제공하는 빈 후처리기 사용
@Import(AopConfig.class) // @Aspect 사용
public class ProxyApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

	/**
	 * v1,2 프록시 적용 시 사용할 로그 추적기 스프링에 등록
	 */
	@Bean
	public LogTrace logTrace(){
		return new ThreadLocalLogTrace();
	}
}
