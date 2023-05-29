package hello.proxy.config.v6_aop.aspect;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


/**
 * @Aspect 프록시 - 설명
 * - 앞서 자동 프록시 생성기를 학습할 때, 자동 프록시 생성기(AnnotationAwareAspectAutoProxyCreator)는 Advisor 를 자동으로 찾아와서 필요한 곳에 프록시를 생성하고 적용해준다 했는데,
 *   여기에 추가로 @Aspect 를 찾아서 이것을 Advisor 로 만들어주는 역할도 추가로 함. 그래서 이름 앞에 AnnotationAware 이 붙어 있는 것임
 * - @Aspect 를 어드바이저로 변환해서 저장하는 과정
 *   1. 실행 : 스프링 애플리케이션 로딩 시점에 자동 프록시 생성기를 호출
 *   2. 모든 @Aspect 빈 조회 : 자동 프록시 생성기는 스프링 컨테이너에서 @Aspect 애노테이션이 붙은 스프링 빈을 모두 조회함
 *   3. 어드바이저 생성 : @Aspect 어드바이저 빌더를 통해 @Aspect 애노테이션 정보를 기반으로 어드바이저를 생성
 *   4. @Aspect 기반 어드바이저 저장 : 생성한 어드바이저를 @Aspect 어드바이저 빌더 내부에 저장
 *   (참고) @Aspect 어드바이저 빌더
 *         BeanFactoryAspectJAdvisorBuilder 클래스. @Aspect 의 정보를 기반으로 포인터컷, 어드바이스, 어드바이저를 생성하고 보과하는 것을 담당함
 *         @Aspect 정보를 기반으로 어드바이저를 만들고, @Aspect 어드바이저 빌더 내부 저장소에 캐싱하고, 캐시에 어드바이저가 이미 만들어져 있는 경우 캐시에 저장된 어드바이저를 반환함
 * - 어드바이저를 기반으로 프록시 생성
 *   1. 생성 : 스프링이 스프링 빈 대상이 되는 객체를 생성함 (@Bean, 컴포넌트 스캔 모두 포함)
 *   2. 전달 : 생성된 객체를 빈 저장소에 등록하기 직전에 빈 후처리기에 전달함
 *   3-1. Advisor 빈 조회 : 스프링 컨테이너에서 Advisor 빈을 모두 조회함
 *   3-2. @Aspect Advisor 조회 : @Aspect 어드바이저 빌더 내부에 저장된 Advisor 를 모두 조회함
 *   4. 프록시 적용 대상 체크 : 앞서 3-1, 3-2 에서 조회한 Advisor 에 포함되어 있는 포인트컷을 사용해서 해당 객체가 프록시를 적용할 대상인지 판단함.
 *                           이때 객체의 클래스 정보는 물론이고, 해당 객체의 모든 메서드를 포인트컷에 하나하나 모두 매칭해봄. 그래서 조건이 하나라도 만족하면 프록시 적용 대상이 됨
 *   5. 프록시 생성 : 프록시 적용 대상이면 프록시를 생성하고 반환해서 프록시를 스프링 빈으로 등록함. 만약 프록시 적용 대상이 아니면 원본 객체를 반환해서 원본 객체를 스프링 빈으로 등록함
 *   6. 빈 등록 : 반환된 객체는 스프링 빈으로 등록됨
 */
@Slf4j
@Aspect
public class LogTraceAspect {

    private final LogTrace logTrace;

    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    /**
     * @Aspect : 어노테이션 기반 프록시를 적용할 때 필요
     * @Around("execution(* hello.proxy.app..*(..))")
     *      - @Around 의 값에 포인트컷 표현식을 넣음. 표현식은 AspectJ 표현식을 사용
     *      - @Around 의 메서드는 어드바이스가 됨
     * ProceedingJoinPoint : 어드바이스에서 살펴본 MethodInvocation 과 유사한 기능. 내부에 실제 호출 대상, 전달 인자, 그리고 어떤 객체와 어떤 메서드가 호출되었는지 정보가 포함되어 있음
     * ProceedingJointPoint.proceed() : 실제 호출 대상(target) 호출
     */
    @Around("execution(* hello.proxy.app..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws  Throwable{
        TraceStatus status = null;
        try{
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            Object result = joinPoint.proceed();

            logTrace.end(status);
            return result;
        } catch (Exception e){
            logTrace.exception(status, e);
            throw e;
        }
    }
}
