package hello.proxy.pureproxy.concreteproxy;

import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy;
import org.junit.jupiter.api.Test;

public class ConcreteProxyTest {

    @Test
    void noProxy(){
        ConcreteLogic concreteLogic = new ConcreteLogic();
        ConcreteClient client = new ConcreteClient(concreteLogic);
        client.execute();
    }

    /**
     * 핵심은 ConcreteClient 생성자에 ConcreteLogic 이 아니라 TimeProxy 를 주입하는 부분임
     * ConcreteClient 는 ConcreteLogic 을 의존하는데, 다형성에 의해 ConcreteLogic 에 ConcreteLogic 도 들어갈 수 있고, TimeProxy 도 들어갈 수 있음
     *
     * 인터페이스가 없어도 클래스 기반의 프록시가 잘 적용된 것을 확인 할 수 있음
     */
    @Test
    void addProxy(){
        ConcreteLogic concreteLogic = new ConcreteLogic();
        TimeProxy timeProxy = new TimeProxy(concreteLogic);
        ConcreteClient client = new ConcreteClient(timeProxy);
        client.execute();
    }
}
