package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.*;
import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderControllerConcreteProxy;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderRepositoryConcreteProxy;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderServiceConcreteProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 인터페이스 기반 프록시 VS 클래스 기반 프록시
 * - 클래스 기반 프록시는 해당 클래스에만 적용할 수 있는 반면 인터페이스 기반 프록시는 인터페이스만 같으면 모든 곳에 적용할 수 있음
 * - 클래스 기반 프록시는 상속을 사용하기 때문에 몇가지 제약이 있음
 *      - 부모 클래스의 생성자 호출해야 한다 (super() 부분)
 *      - 클래스에 final 키워드가 붙으면 상속이 불가능 (자바 기본 문법)
 *      - 메서드에 final 키워드가 붙으면 해당 메서드를 오버라이딩 할 수 없음 (자바 기본 문법)
 * => 인터페이스 기반 프록시는 상속이라는 제약에서 자유롭고, 프로그래밍 관점에서도 역활과 구현을 명확하게 나누기 때문에 더 좋음
 *
 * BUT
 * - 이론적으로 모든 객체에 인터페이스를 도입해서 역활과 구현을 나누어 구현체를 매우 편리하게 변경할 수 있으면 좋음
 * - 하지만 실제로는 구현을 거의 변경할 일이 없는 클래스도 많기 때문에 구현을 변경할 가능성이 거의 없는 코드에 무작정 인터페이스를 사용하느 것은 번거롭고 실용적이지 못함
 *
 * 결론
 * - 실무에서는 프록시를 적용할 때 V1처럼 인터페이스가 있는 경우도 있고, V2처럼 구체 클래가 있는 경우도 있음
 * - 2가지 상황을 모두 대응할 수 있어야 함
 *
 *  * 개선해야 할 점
 *  * 1. 로그 추적기를 도입할 클래스가 너무 많으면 그에 해당하는 많은 프록시 클래스를 만들어야하는 단점이 존재
 */
@Configuration
public class ConcreteProxyConfig {

    @Bean
    public OrderControllerV2 orderController(LogTrace logTrace){

        OrderControllerV2 orderController = new OrderControllerV2(orderService(logTrace));

        return new OrderControllerConcreteProxy(orderController, logTrace);
    }

    @Bean
    public OrderServiceV2 orderService(LogTrace logTrace){

        OrderServiceV2 serviceImpl = new OrderServiceV2(orderRepository(logTrace));

        return new OrderServiceConcreteProxy(serviceImpl, logTrace);
    }

    @Bean
    public OrderRepositoryV2 orderRepository(LogTrace logTrace){

        OrderRepositoryV2 repositoryImpl = new OrderRepositoryV2();

        return new OrderRepositoryConcreteProxy(repositoryImpl, logTrace);
    }
}
