package hello.proxy.pureproxy.proxy;

import hello.proxy.pureproxy.proxy.code.CacheProxy;
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

    /**
     * realSubject 와 cacheProxy 를 생성하고 둘을 연결한다. 결과적으로 cacheProxy 가 realSubject 를 참조하는 런타임 객체 의존관계가 완성됨
     * client 에는 realSubject 가 아닌 cacheProxy 를 주입함
     * 결과적으로 client -> cacheProxy -> realSubject 런타임 객체 의존관계가 완성됨
     *
     * 처음에만 realSubject 를 호출하고 나머지는 cacheProxy 를 호출함 -> 속도 향상
     *
     * 프록시 패턴의 핵심은 RealSubject 코드와 클라이언트 코드를 전혀 변경하지 않고, 프록시를 도입해서 접근 제어를 했다는 것임
     * 실제 클라이언트에서는 프록시 객체가 주입되었는지, 실제 객체가 주입되었는지 알지 못함 (인터페이스를 의존하니까)
     */
    @Test
    void cacheProxyTest(){
        RealSubject realSubject = new RealSubject();
        CacheProxy cacheProxy = new CacheProxy(realSubject);
        ProxyPatternClient client = new ProxyPatternClient(cacheProxy);
        client.execute();
        client.execute();
        client.execute();
    }
}
