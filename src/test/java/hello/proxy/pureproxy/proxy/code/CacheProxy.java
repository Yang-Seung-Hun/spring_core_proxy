package hello.proxy.pureproxy.proxy.code;

import lombok.extern.slf4j.Slf4j;

/**
 * 프록시 객체
 * 실제 객체(RealSubject)와 모양이 같아야하기 때문에 Subject 인터페이스를 구현함
 * 클라이언트가 프록시를 호출하면 실제 객체가 호출 되어야 하기 때문에 내부에 실제 객체의 참조를 가지고 있음(target)
 */
@Slf4j
public class CacheProxy implements Subject{

    private Subject target; // 실제 객체 참조
    private String cacheValue;

    public CacheProxy(Subject target) {
        this.target = target;
    }

    /**
     * cacheValue 에 값이 없으면 실제 객체(target)을 호출해서 값을 구하고, cacheValue 에 저장하고 반환
     * cacheValue에 값이 있으면 실제 객체를 전혀 호출하지 않고, 캐시 값을 그대로 반환함
     * 처음 조회 이후에는 캐시에서 매우 빨게 데이터 조회가 가능해짐
     */
    @Override
    public String operation() {
        log.info("프록시 호출");
        if(cacheValue == null){
            cacheValue = target.operation();
        }
        return cacheValue;
    }
}
