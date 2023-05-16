package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

/**
 * 실행 순서 정리
 * 1. 클라이언트는 JDK 프록시의 call()을 실행
 * 2. JDK 동적 프록시는 InvocationHandler.invoke()를 호출한다. TimeInvocationHandler 가 구현체로 있으므로 TimeInvocationHandler.invoke()가 호출 됨
 * 3. TimeInvocationHandler 가 내부 로직을 수행하고, method.invoke(target, args)를 호출해서 target 인 실제 객체(AImpl)를 호출함
 * 4. AImpl 인스턴스의 call()이 실행됨
 * 5. AImpl 인스턴스의 call()의 실행이 끝나면 TimeInvocationHandler 로 응답이 돌아옴. 시간 로그를 출력하고 결과를 반환함.
 *
 * 정리
 * 예제를 보면 AImpl, BImpl 각각 프록시를 만들지 않았음
 * 프록시는 JDK 동적 프록시를 사용해서 동적으로 만들고 TimeInvocationHandler 는 공통으로 사용함
 * JDK 동적 프록시 기술 덕분에 대상 만큼 프록시 객체를 만들지 않아도 되고, 같은 부가 기능 로직을 한번만 개발해서 공통으로 적용할 수 있음
 * 만약 적용 대상이 100개여도 동적 프록시를 통해서 생성하고, 각각 필요한 InvocationHandler 만 만들어서 넣어주면 됨
 * 결과적으로 프록시 클래스를 수 없이 만들어야하는 문제도 해결하고, 부가 기능 로직도 하나의 클래스에 모아서 단일 책임 원칙(SRP)도 지킬 수 있음
 */
@Slf4j
public class JDKDynamicProxyTest {

    @Test
    void dynamicA(){
        AInterface target = new AImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        // 생성 된 프록시 클래스의 메서드 호출 시 handler 의 로직을 실행함
        AInterface proxy = (AInterface)Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);

        // call()을 생성 했으므로 handler 의 invoke() 메서드의 두번째 인자(Method)로 call()이 들어감 -> target 의 call 이 호출 됨
        proxy.call();

        log.info("targetClass={}", target.getClass()); // targetClass=class hello.proxy.jdkdynamic.code.AImpl
        log.info("proxyClass={}", proxy.getClass()); // proxyClass=class com.sun.proxy.$Proxy12
    }

    @Test
    void dynamicB(){
        BInterface target = new BImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        // 생성 된 프록시 클래스의 메서드 호출 시 handler 의 로직을 실행함
        BInterface proxy = (BInterface)Proxy.newProxyInstance(BInterface.class.getClassLoader(), new Class[]{BInterface.class}, handler);

        // call()을 생성 했으므로 handler 의 invoke() 메서드의 두번째 인자(Method)로 call()이 들어감 -> target 의 call 이 호출 됨
        proxy.call();

        log.info("targetClass={}", target.getClass()); // targetClass=class hello.proxy.jdkdynamic.code.BImpl
        log.info("proxyClass={}", proxy.getClass()); // proxyClass=class com.sun.proxy.$Proxy12
    }
}
