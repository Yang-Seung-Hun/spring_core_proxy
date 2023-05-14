package hello.proxy.config.v1_proxy.concrete_proxy;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

/**
 * 클래스 기반 프록시의 단점
 * - 자바 기본 문법에 의해 자식 클래스를 생성할 때는 항상 super()로 부모 클래스의 생성자를 호출해야 함
 * - 부모 클래스 생성자를 호출하는 부분이 없으면 기본 생성자가 호출됨
 * - 현재 부모 클래스인  OrderControllerV2는 기본생성자가 없고, 생성자에서 파라미터 1개를 필수로 받기 때문에 super(..)를 호출해야 함 (OrderService 도 마찬가지)
 * - 프록시는 부모 객체의 기능을 사용하지 않기 때문에 super(null)로 입력해야 함
 * - 인터페이스 기반 프록시 경우 이와 같은 고민을 하지 않아도 됨
 */
public class OrderControllerConcreteProxy extends OrderControllerV2 {

    private final OrderControllerV2 target;
    private final LogTrace logTrace;

    public OrderControllerConcreteProxy(OrderControllerV2 target, LogTrace logTrace) {
        super(null);
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public String request(String itemId) {
        TraceStatus status = null;
        try{
            status = logTrace.begin("OrderController.request()");

            String result = target.request(itemId);

            logTrace.end(status);
            return result;
        }catch(Exception e){
            logTrace.exception(status, e);
            throw e;
        }
    }

    @Override
    public String noLog() {
        return target.noLog();
    }
}
