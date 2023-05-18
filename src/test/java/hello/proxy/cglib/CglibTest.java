package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

@Slf4j
public class CglibTest {

    /**
     * Enhancer : CGLIB 은 Enhancer 클래스를 사용해서 프록시를 생성함
     * enhancer.setSuperclass(ConcreteService.class) : CGLIB 는 구체 클래스를 상속 받아서 프록시를 생성. 어떤 구체 클래스를 상속 받을지 지정함
     * enhancer.setCallback() : 프록시에 적용할 실행 로직을 할당
     * enhancer.create() : 프록시를 생성한다. 앞서 설정한 setSuperclass() 에서 지정한 클래스를 상속 받아서 프록시가 만들어짐
     *
     * CGLIB 가 생성한 프록시 클래스 이름
     * ConcreteService$$EnhancerByCGLIB$$25d6b0e3 이런 규칙으로 만들어짐
     *
     * CGLIB 제약
     * 클래스 기반 프록시는 상속을 사용함
     * - 부모 클래스의 생성자를 체크해야 함 -> CGLIB 은 자식 클래스를 동적으로 생성하기 때문에 기본 생성자가 필요함
     * - 클래스에 final 키워드가 붙으면 상속이 불가능함 -> CGLIB 에서는 예외가 발생함
     * - 메서드에 final 키워드가 붙으면 해당 메서드를 오버라이딩 할 수 없음 -> CGLIB 에서는 프록시 로직이 동작하지 않는다
     */
    @Test
    void cglib(){
        ConcreteService target = new ConcreteService();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ConcreteService.class);
        enhancer.setCallback(new TimeMethodInterceptor(target));
        ConcreteService proxy = (ConcreteService) enhancer.create();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();
    }
}
