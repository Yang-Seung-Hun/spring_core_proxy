package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import hello.proxy.proxyfactory.ProxyFactoryTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

/**
 * 포인트컷, 어드바이스, 어드바이저
 * 1.포인트컷 (Pointcut)
 * - 어디에 부가 기능을 적용할지, 어디에 부가 기능을 적용하지 않을지 판단하는 필터링 로직
 * - 주로 클래스와 메서드 이름으로 필터링 함
 * - 이름 그대로 어떤 포인트(Point)에 기능을 적용할지 하지 않을지 잘라서(cut) 구분하는 것
 *
 * 2.어드바이스 (Advice)
 * - 이전에 본 것 처럼 프록시가 호출하는 부가 로직
 * - 단순하게 프록시 로직이라 생각하면 됨
 *
 * 3.어드바이저 (Advisor)
 * - 단순하게 하나의 포인트컷과 하나의 어드바이스를 가지고 있는 것
 * - 쉽게 말해 포인트컷1 + 어드바이스1
 *
 * 역활과 책임
 * - 위 와 같이 구분한 것은 역활을 책임을 명확하기 분리하기 위함
 * - 포인트컷은 대상 여부를 확인하는 필터 역할만 담당
 * - 어드바이스는 깔깜하게 부가 기능 로직을 담당
 */
@Slf4j
public class AdvisorTest {

    /**
     * - DefaultPointcutAdvisor : Advisor 인터페이스와 가장 일반적인 구현체. 생성자를 통해 하나의 포인트컷과 하나의 어드바이스를 넣어주면 됨
     * - Pointcut.TRUE : 항상 true 를 반환하는 포인트컷
     * - 이전에는 proxyFactory.addAdvice(new TimeAdvice()) 처럼 어드바이저가 아닌 어드바이스를 바로 적용함.
     *   이는 단순히 편의 메서드이고 결과적으로는 해당 메서드 내부에서 지금 코드와 똑같은 DefaultPointcutAdvisor(Pointcut.True, new TimeAdvice()) 어드바이저가 생성됨
     */
    @Test
    void advisorTest1(){
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    /**
     * 직접 만든 포인트컷으로 save() 호출시에만 어드바이스 적용되도록 함
     */
    @Test
    @DisplayName("직접 만든 포인트컷")
    void advisorTest2(){
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    /**
     * 직접 구현한 포인트컷
     * Pointcut 인터페이스를 구현함
     * 현재 메서드 기준으로 로직을 적용하면 됨. 클래스 필터는 항상 true 를 반환하도록 했고, 메서드 비교 기능인 MyMethodMatcher 를 사용함
     */
    static class MyPointcut implements Pointcut{
        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }
        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }

    /**
     * 직접 구현한 MethodMatcher
     * MethodMatcher 를 구현함
     */
    static class MyMethodMatcher implements MethodMatcher{

        private String matchName = "save";

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            boolean result = method.getName().equals(matchName);
            log.info("포인트컷 호출 method={} targetClass={}", method.getName(), targetClass);
            log.info("포인트컷 결과 result={}", result);
            return result;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }

    /**
     * 스프링은 무수히 많은 포인트컷을 제공함. 그 중 대표적인 몇가지는 다음과 같음
     * 1. NameMatchMethodPointcut : 메서드 이름을 기반으로 매칭. 내부에서는 PatternMatchUtils 를 사용
     * 2. JdkRegexMethodPointcut : JDK 정규 표현식을 기반으로 포인트컷 매칭
     * 3. AnnotationMatchingPointcut : 애노테이션으로 매칭
     * 4. AspectJExpressionPointcut : aspectJ 표현식으로 매칭
     *
     * 가장 중요한 것은 aspectJ 표현식임
     * - 사실 나머지 포인트컷들은 중요하지 않음
     * - 실무에서는 사용하기도 편리하고 기능도 가장 많은 aspectJ 표현식을 기바으로 사용하는 AspectJExpressionPointcut 을 사용함
     */
    @Test
    @DisplayName("스프링이 제공하는 포인트컷")
    void advisorTest3(){
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("save");

        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }
}
