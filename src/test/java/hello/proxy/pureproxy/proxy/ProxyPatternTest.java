package hello.proxy.pureproxy.proxy;

import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import org.junit.jupiter.api.Test;

public class ProxyPatternTest {

    /**
     * client.execute()를 3번 호출하면 데이터를 조회하는데 1초씩 총 3초 걸림
     * 그런데 이 데이터가 한번 조회하면 변하지 않는 데이터라고 하면 어딘가에 보관해두고 이미 조회한 데이터를 사용하면 성능에 이점을 얻을 수 있음
     * 이런 것을 캐시라고 함
     * 프록시 패턴의 주요 기능은 접근 제어인데 캐시도 접근 자체를 제어하는 기능 중 하나임
     */
    @Test
    void noProxyTest(){
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);
        client.execute();
        client.execute();
        client.execute();
    }
}
