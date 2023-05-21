package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * invocation.proceed() 를 호출하면 target 클래스를 호출하고 그 결과를 받음
 * target 클래스의 정보가 보이지 않는데, 그 이유는 MethodInvocation 내부에 모두 포함되어 있기 때문 (프록시 팩토리로 프록시 생성 할때 target 정보를 파라미터로 전달 받음)
 */
@Slf4j
public class TimeAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = invocation.proceed();

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}", resultTime);
        return result;
    }
}
