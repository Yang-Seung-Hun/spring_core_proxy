package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class LogTraceBasicHandler implements InvocationHandler {

    private final Object target;
    private final LogTrace logTrace;

    public LogTraceBasicHandler(Object target, LogTrace logTrace) {
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        TraceStatus status = null;
        try{
            String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()"; // 로그 추적기에 사용할 메시지
            status = logTrace.begin(message);

            Object result = method.invoke(target, args); // 비즈니스 로직 실행

            logTrace.end(status);
            return result;
        }catch (Exception e){
            logTrace.exception(status, e);
            throw e;
        }
    }
}
