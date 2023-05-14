package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * v1 프록시 런타임 객체 의존 관계 설정
 * - 기존에는(AppV1Config)에는 스프링 빈이 OrderServiceV1Impl, OrderControllerImpl 같은 실제 객체를 반환했음
 * - 기존과 달리 이제는 프록시 객체를 구현 한 후 프록시 객체를 스프링 빈으로 대신 등록함
 * - 실제 객체는 스프링 빈으로 등록하지 않음 (프록시 객체가 실제 객체의 참조를 가지고 있음)
 * - 프록시 객체는 스프링 컨네이너가 관리하고 자바 힙 메모리에 올라가는 반면에, 실제 객체는 자바 힙 메모리에는 올라가지만 스프링 컨테이너가 관리하지는 않음
 *
 * 런타임 객체 의존 관계
 * client -> orderControllerProxy(Proxy) -> orderControllerV1Impl -> orderServiceProxy(Proxy) -> orderServiceV1Impl -> orderRepositoryProxy(Proxy) -> orderRepositoryV1Impl
 *
 * 개선한 점
 * 1. 프록시 덕분에 원본 코드를 전혀 수정하지 않고 로그 추적기를 도입할 수 있게 됨
 *
 * 개선해야 할 점
 * 1. 로그 추적기를 도입할 클래스가 너무 많으면 그에 해당하는 많은 프록시 클래스를 만들어야하는 단점이 존재
 */
@Configuration
public class InterfaceProxyConfig {

    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace){

        OrderControllerV1Impl controllerImpl = new OrderControllerV1Impl(orderService(logTrace));

        return new OrderControllerInterfaceProxy(controllerImpl, logTrace);
    }

    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace){

        OrderServiceV1Impl serviceImpl = new OrderServiceV1Impl(orderRepository(logTrace));

        return new OrderServiceInterfaceProxy(serviceImpl, logTrace);
    }

    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace){

        OrderRepositoryV1Impl repositoryImpl = new OrderRepositoryV1Impl();

        return new OrderRepositoryInterfaceProxy(repositoryImpl, logTrace);
    }

}
