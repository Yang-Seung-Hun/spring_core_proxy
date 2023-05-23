package hello.proxy.config.v4_postprocessor.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 원본 객체를 프록시 객체로 변환하는 역할을 함. 이때 프록시 팩토리를 사용하는데, 프록시 팩토리는 advisor 가 필요하기 때문에 이부분은 외부에서 주입 받게 함
 * 모든 스프링 빈들에 프록시를 적용할 필요는 없음 -> 특정 패키지와 그 하위에 위치한 스프링 빈들만 프록시를 적용함 (조건문 부분)
 *
 * 프록시 적용 대상 체크
 * - 우리가 직접 등록한 스프링 빈들 뿐만 아니라 스프링 부트가 기본으로 등록하는 수많은 빈들이 빈 후처리기에 넘어옴. 그래서 어떤 빈을 프록시로 만들 것인지 기준이 필요함
 * - 스프링 부트가 기본으로 제공하는 빈 중에는 프록시 객체를 만들 수 없는 빈들도 있음. 따라서 모든 객체를 프록시로 만들 경우 오류가 발생함
 */
@Slf4j
public class PackageLogTracePostProcessor implements BeanPostProcessor {

    private final String basePackage;
    private final Advisor advisor;

    public PackageLogTracePostProcessor(String basePackage, Advisor advisor) {
        this.basePackage = basePackage;
        this.advisor = advisor;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.info("param beanName={} bean={}", beanName, bean.getClass());

        //프록시 적용 대상 여부 체크
        //프록시 적용 대상이 아니면 원본을 그대로 진행
        String packageName = bean.getClass().getPackageName();
        if(!packageName.startsWith(basePackage)){
            return bean;
        }

        //프록시 대상이면 프록시를 만들어서 반환
        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.addAdvisor(advisor);

        Object proxy = proxyFactory.getProxy();
        log.info("create proxy: target={} proxy={}", bean.getClass(), proxy.getClass());
        return proxy;
    }
}
