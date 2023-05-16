package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class ReflectionTest {

    /**
     * 공통 로직 1과 2는 호출하는 메서드만 다르고 전체 코드 흐름이 완전히 같음
     * 여기서 공통 로직1과 공통 로직2를 하나의 메서드로 뽑아서 합칠 수 있을까?
     *      - 쉬어보이지만 중간에 호출하는 메서드가 다르기 때문에 메서드로 뽑아서 공통화하는 것은 매우 어려움
     *      - 호출하는 메서드인 target.callA(), target.callB() 이부분마 동적으로 처리할 수 있다면 문제를 해결 할 수 있을
     *      -> 이럴때 사용하는 기술이 바로 리플렉션임
     *      -> 리플렉션은 클래스나 메서드의 메타정보를 사용해서 동적으로 호출하는 메서드를 변경할 수 있음
     *      (참고) 람다를 활용해도 해결 가능함
     */
    @Test
    void reflection0(){
        Hello target = new Hello();

        //공통 로직1 시작
        log.info("start");
        String result1 = target.callA(); // 호출 하는 메서드만 다름
        log.info("result={}", result1);
        //공통 로직1 종교

        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB(); // 호출 하는 메서드만 다름
        log.info("result={}", result2);
        //공통 로직2 종교
    }

    /**
     * Class.forName("...") : 클래스 메타정보를 획득함. 내부 클래스는 구분을 위해 $를 사용
     * classHello.getMethod("...") : 해당 클래스의 "..." 메서드 메타정보를 획득
     * methodCallA.invoke(target 인스턴스) : 획득한 메서드 메타정보로 실제 인스턴스의 메서드를 호출
     *
     * 메서드 정보를 획득해 메서드를 호출하면 어떤 효과가 있는가?
     *      ->클래스나 메서드 정보를 동적으로 변경 할 수 있게 됨
     */
    @Test
    void reflectionV1() throws Exception {

        //클래스 정보 획득
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        //callA 메서드 정보 획득
        Method methodCallA = classHello.getMethod("callA");
        Object result1 = methodCallA.invoke(target);
        log.info("result1={}", result1);

        //callB 메서드 정보 획득
        Method methodCallB = classHello.getMethod("callB");
        Object result2 = methodCallB.invoke(target);
        log.info("result2={}", result2);

    }

    /**
     * 정적인 target.callA(), target.callB() 코드를 리플렉션을 사용해서 Method라는 메타정보로 추상화함
     * 그 덕분에 공통 로직을 만들 수 있게됨
     *
     * 리플렉션 주의점
     * 리플렉션은 런타임에 동작하기 때문에 컴파일 시점에서 오류를 잡을 수 없음
     * -> 프레임워크 개발이나 또는 매우 일반적인 공통 처리가 필요할 때 분적으로 주의하며 사용해야함
     */
    @Test
    void reflectionV2() throws Exception {

        //클래스 정보 획득
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);

        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);

    }

    /**
     * 공통 로직1,2를 한번에 처리할 수 있는 통합된 공통 처리 로직
     * 첫번째 파라미터 : 호출할 메서드 정보
     *      - 기존에는 메서드 이름을 직접 호출했지만, 이제는 Method라는 메타정보를 통해 호출할 메서드 정보가 동적으로 제공됨
     * 두번째 파라미터 : Object 이기 때문에 어떠한 인스턴스도 받을 수 있음
     */
    private void dynamicCall(Method method, Object target) throws Exception {
        log.info("start");
        Object result = method.invoke(target);
        log.info("result={}", result);
    }


    @Slf4j
    static class Hello{
        public String callA(){
            log.info("callA");
            return "A";
        }

        public String callB(){
            log.info("callB");
            return "B";
        }
    }
}
