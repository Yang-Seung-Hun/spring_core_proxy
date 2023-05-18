package hello.proxy.cglib.code;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * MethodInterceptor 를 구현해서 CGLIB 프록시의 실행 로직을 정의함
 * JDK 동적 프록시 설명 예제와 거의 같은 코드
 * proxy.invoke(target, args) :  실제 대상을 동적으로 호출함 (method 를 사용해도 되지만, CGLIB 는 성능상 MethodProxy proxy 를 사용하는 것을 권장함
 */
@Slf4j
public class TimeMethodInterceptor implements MethodInterceptor {

    private final Object target;

    public TimeMethodInterceptor(Object target) {
        this.target = target;
    }


    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = methodProxy.invoke(target, args);

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}", resultTime);
        return result;
    }
}
