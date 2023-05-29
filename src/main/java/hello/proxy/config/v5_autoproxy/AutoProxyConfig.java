package hello.proxy.config.v5_autoproxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 자동 프록시 생성기 - AutoProxyCreator
 * - 앞서 이야기한 스프링 부트 자동 설정으로 AnnotationAwareAspectAutoProxyCreator 라는 빈 후처리기가 스프링 빈에 자동으로 등록됨
 * - 이 빈 후처리기는 스프링 빈으로 등록된 Advisor 들을 자동으로 찾아서 프록시가 필요한 곳에 자동으로 프록시를 적용함
 * - (참고) AnnotationAwareAspectAutoProxyCreator 은 @AspectJ 와 관련된 AOP 기능도 자동으로 찾아서 처리함
 *         Advisor 는 물론이고, @Aspect 도 자동으로 인식해서 프록시를 만들고 AOP 를 적용함. 자세한 내용은 뒤에서 설명
 *
 * 자동 프록시 생성기의 작동 과정
 * 1. 생성 : 스프링이 스프링 빈 대상이 되는 객체를 생성함 (@Bean, 컴포넌트 스캔 모두 포함)
 * 2. 전달 : 생성된 객체를 빈 저장소에 등록하기 직전에 빈 후처리기에 전달함
 * 3. 모든 Advisor 빈 조회 : 자동 프록시 생성기 - 빈 후처리기기는 스프링 컨테이너에서 모든 Advisor 를 조회함
 * 4. 프록시 적용 대상 체크 : 앞서 조회한 Advisor 에 포함되어 있는 포인트컷을 사용해서 해당 객체가 프록시를 적용할 대상인지 판단함.
 *                         이때 객체의 클래스 정보는 물론이고, 해당 객체의 모든 메서드를 포인트컷에 하나하나 모두 매칭해봄. 그래서 조건이 하나라도 만족하면 프록시 적용 대상이 됨
 * 5. 프록시 생성 : 프록시 적용 대상이면 프록시를 생성하고 반환해서 프록시를 스프링 빈으로 등록함. 만약 프록시 적용 대상이 아니면 원본 객체를 반환해서 원본 객체를 스프링 빈으로 등록함
 * 6. 빈 등록 : 반환된 객체는 스프링 빈으로 등록됨
 *
 * (주의) 프록시를 만드는 단계에서 사용되는 포인트컷과 실제 실행단계에서 사용되는 포인트컷을 구분해서 이해할 필요가 있음
 * 1. 프록시 적용 여부 판단 - 생성 단계
 * - 자동 프록시 생성기는 포인트컷을 사용해서 해당 빈이 프록시를 생성할 필요가 있는지 없는지 체크함
 * - 클래스 + 메서드 조건을 모두 비교함. 이때 모든 메서드를 체크하는데, 포인트컷 조건에 하나하나 매칭해봄. 조건에 맞는 것이 하나라도 있으면 프록시를 생성함
 *   (ex) orderControllerV1 은 request(), noLog() 가 있는데, 여기서 request() 가 조건에 만족하므로 프록시를 생성함
 *
 * 2. 어드바이스 적용 여부 판단 - 사용 단계
 * - 프록시가 호출되었을 때 부가 기능인 어드바이스를 적용할지 말지 포인트컷을 보고 판단함
 * - 앞서 예시로 말한 orderControllerV1 은 이미 프록시가 걸려있는데, request() 는 현재 포인트컷 조건에 만족하므로 프록시는 어드바이스를 먼저 호출하고, target 을 호출함.
 *   반면 noLog() 는 현재 포인트컷 조건에 만족하지 않으므로 어드바이스를 호출하지 않고 바로 target 을 호출함
 *
 * (+주의)하나의 프록시, 여러 Advisor 적용
 * - 만약 어떤 스프링 빈이 advisor1, advisor2 가 제공하는 포인트컷의 조건을 모두 만족하면 프록시 자동 생성기는 포록시를 몇개 생성할까?
 *   => 하나의 프록시를 생성한다. 그 이유는 프록시 팩토리가 생성하는 프로시는 내부에 여러 advisor 들을 포함할 수 있기 때문
 * 프록시 자동 생성기 상황별 정리
 * 1. advisor1 의 포인트컷 만 만족 ->프록시 1개 생성, 프록시에 advisor1 만 포함
 * 2. advisor1, advisor2 의 포인트컷을 모두 만족 -> 프록시 1개 생성, 프록시에 advisor1, advisor2 모두 포함
 * 3. advisor1, advisor2 의 포인트컷을 모두 만족하지 않음 -> 프록시가 생성되지 않음
 */
@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AutoProxyConfig {

    /**
     * 어드바이저만 등록하면 자동 프록시 생성기가 처리해줌
     */
    //@Bean
    public Advisor advisor1(LogTrace logTrace) {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");

        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    /**
     * 위 어드바이저에서(advisor1) 문제 발생
     * - 애플리케이션 서버를 실행해보면, 스프링이 초기화 되면서 기대하지 않은 아래와 같은 로그들이 올라옴.
     *   그 이유는 지금 사용하는 포인트컷이 단순히 메서드 이름에 "request*", "order*", "save*" 만 포함되어 있으면 매칭 된다고 판단하기 때문.
     *   결국 스프링이 내부에서 사용하는 빈에도 메서드 이름에 request 라는 단어가 포함되면 프록시가 만들어지고, 어드바이스도 적용되게 됨
     * (문제 예시)
     *   EnableWebMvcConfiguration.requestMappingHandlerAdapter()
     *   EnableWebMvcConfiguration.requestMappingHandlerAdapter() time=286ms
     *   EnableWebMvcConfiguration.requestMappingHandlerMapping()
     *   EnableWebMvcConfiguration.requestMappingHandlerMapping() time=30ms
     *
     * => 결론적으로 패키지에 메서드 이름까지 함께 지정할 수 있는 정밀한 포인트컷이 필요함
     */
    //@Bean
    public Advisor advisor2(LogTrace logTrace){

        /**
         * AspectJ 포인트컷 표현식을 사용
         * - '*' : 모든 반환 타입
         * - 'hello.proxy.app..' :해당 패키지와 그 하위 패키지
         * - '*(..)' : '*' 은 모든 메서드 이름, '(..)' 은 파라미터는 상관 없다는 의미
         * 이후 강의에서 상세히 설명
         */
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* hello.proxy.app..*(..))");

        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    /**
     * 위 어드바이저에서(advisor2) 문제 발생
     * http://localhost:8080/v3/no-log 요청시 로그가 발생하면 안되지만 로그가 출력됨.
     * 이는 advisor2 는 단순히 package 를 기준으로 포인트컷 매칭을 했기 때문임.
     * => && 연산을 사용해 두 표현식을 조합하는 것이 가능. 나중에 자세히 설명
     */
    @Bean
    public Advisor advisor3(LogTrace logTrace){

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))");

        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}