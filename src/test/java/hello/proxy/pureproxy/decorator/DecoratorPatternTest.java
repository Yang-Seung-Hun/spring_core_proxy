package hello.proxy.pureproxy.decorator;

import hello.proxy.pureproxy.decorator.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 프록시 vs 데코레이터 패턴
 * 의도(intent)로 판단
 * - 사실 프록시 패턴과 데코레이터 패턴은 그 모양이 거의 같고, 상황에 따라 정말 똑같을 때도 있음
 * - 디자인 패턴에서 중요한 것은 해당 패턴의 겉모양이 아니라 패턴을 만든 의도가 더 중요함
 * - 프록시 패턴의 의도 : 다른 객체에 대한 접근 제어를 하기 위해 대리자 제공
 * - 데코레이터 패턴의 의도 : 객체에 추가 책임을 동적으로 추가하고, 기능 확장을 위한 유연한 대안 제공
 */

@Slf4j
public class DecoratorPatternTest {

    @Test
    void noDecorator(){
        Component realComponent = new RealComponent();
        DecoratorPatternClient client = new DecoratorPatternClient(realComponent);
        client.execute();
    }

    /**
     * Message Decorator 는 결과값을 꾸며줌
     * data -> **** data ****
     */
    @Test
    void decorator1(){
        Component realComponent = new RealComponent();
        Component messageDecorator = new MessageDecorator(realComponent);
        DecoratorPatternClient client = new DecoratorPatternClient(messageDecorator);
        client.execute();
    }

    /**
     * TimeDecorator 는 시간을 측정해줌
     *
     * 데코레이터 패턴은 여러 프록시를 체인으로 묶을 수 있음
     * client -> timeDecorator -> messageDecorator -> realComponent 의 객체 의존관계를 설정하고 실행함
     */
    @Test
    void decorator2(){
        Component realComponent = new RealComponent();
        Component messageDecorator = new MessageDecorator(realComponent);
        TimeDecorator timeDecorator = new TimeDecorator(messageDecorator);
        DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
        client.execute();
    }
}
