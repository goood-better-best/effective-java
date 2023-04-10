# item 80 스레드보다는 실행자, 태스크, 스트림을 애용하라
~~~ java 
ExecutorService exec = Executors.newSingLeThreadExecutor(); //큐 생성
exec.execute( runnable) ; //태스트 실행
exec.shutdown(); //태스크 종료
~~~ 
- **실행자 프레임 워크** ( Executor Framework )
  - 실행자에 실행할 태스크를 넘기는 방법 
    - 추상적인 인터페이스 
    - 작업 등록과 실행을 분리하는 표준적인 방법
  - java.util.concurrent 패키지 등장
  - 서비스 기능 외에도 여러기능이 존재
- 실행자 서비스를 사용하기에 까다로운 애플리케이션도 있다
  - 작고 가벼운 서버라면 Executors.newCachedThreadPooL이 좋은 선택 ( 특별한 설정이 필요없다 )
  - 무거운 서버라면 Executors.newFixedThreadPool을 선택하거나 , ThreadPoolExceutor를 직접사용하는게 낫다.
- 작업단위를 나타내는 추상 개념이 **태스크**다
  - 작업단위는 아래와 같이 나눌수 있다.
    - Runnable
    - Callable ( 값을 반환하고, 임의의 예외를 던진다.)
    - 태스크 수행은 실행자 서비스가 한다.
- 자바7부터는 포크조인 태스크(ForkJoinTask) 를 지원한다.
  - 일이 먼저 끝낸 쓰레드는 다른 스레드의 남은 태스크를 가져와 대신 처리할 수 있다.
  - 모든 스레드를 바삐 움직여 높은 처리량과 낮은 지연시간을 달성한다.
<br><br><br><br>

# item 81 wait와 Notify보다는 동시성 유틸리티를 애용하라
- wait, notify -> Lock을 소유한 Thread가 일시정지 또는 꺠우는 역할을 수행시 사용
- wait와 notify는 올바르게 사용하기가 아주 까다로우니 고수준 동시성 유틸리티를 사용하자.
  -> 일일히 Thread 컨트롤 하지 말것
- 고수준의 유틸리티?
  - 실행자 프레임워크
  - 동시성 컬렉션 (concurrent collection)
    - List, Queue, Map과 같은 표준 컬렉션에 동시성을 가미
    - 락을 무력화하면 속도가 느려진다. ( 이를 대안으로 상태 의존적 수정 메서드가 자바 8에 default method가 추가)
    - ConcurrentHashMap
      - 다른 컬렉션과 달리 Sycronized를 사용하지 x
      - Compare and Swap (lock x)
  - 동기화 장치 (synchronizer)
    - 동기화 장치는 다른 스레드를 기다릴수 있게하여 , 서로 작업을 조율할 수 있게 해준다.
    - CountDownLatch, Semaphore, CyclicBarrier, Exchanger, Phaser
    - 예시는 책 참고 (p443), wait, notify 사용하는 것보다 더 직관적
    - 스레드 기아 교착상태(thread starvation deadlock)
    - 시간 간격을 잴 때는 항상 System.currentTimeMillis가 아닌 System.nanoTime 을 사용하자.
      (실 시간 시계의 시간 보정에 영향받지 않는다, 그것도 힘들다면 jmh 같은 프레임워크를 사용하자)
  
~~~ java 

synchronized (obj) {
while (< 조건이충족되지않았다>)
    obj.wait(); (락을놓고 깨어나면다시잡는다.)
    ... // 조건이충족됐을때의동작을수행한다. }
}
~~~ 
- notify 대신 notifyAll을 사용, 모든 쓰레드를 깨어나 정확성 충족

# item 82 스레드 안전성 수준을 문서화하라
- 메서드 선언에 Synchronized 한정자를 선언할지는 구현 이슈일 뿐 API에 속하지 않는다.
- 멀티스레드 환경에서 API를 안전하게 사용하게 하려면 클래스가 지원하는 스레드 안정성 수준을 정확히 명시해야한다.
- 불변 (String, Long, BigInteger) -> 무조건적 스레드 안전 (AtomicLong, ConccurrentHashMap) ->  
-> 조건부 스레드 안전(Collections.synchronized) -> 스레드 안전하지 않음 (ArrayList, HashMap)
-> 스레드 적대적 ( 일반적 deprecated API )
- 스레드 안전성 어노테이션 (@Immutable, @ThreadSafe, @NotThreadSafe가 그 예시이다.)
- 락 필드는 항상 Final로 선언하라.

# item 83 지연 초기화는 신중히 사용하라
- 필드의 초기화 시점을 그 값이 처음 필요할 떄까지 늦추는 기법
- 접근하는 비용은 늘어난다.
- 성능 때문에 정적 필드를 지연 초기화해야 한다면 지연 초기화 홀더 클래스 (lazy initialization holder class) 관용구를 사용하자.
  - 디자인 패턴   
  - 클래스는 클래스가 처음 쓰일때 비로소 초기화된다는 특성을 이용한 관용구
~~~ java 
public class LazyInitializationHolder {
    private static class ObjectHolder {
        static final MyObject instance = new MyObject();
    }

    public static MyObject getInstance() {
        return ObjectHolder.instance;
    }
}
~~~
- final로 선언된 instance는 멀티스레딩에 유리하게 사용 가능 ( feat. GPT )
- 성능 때문에 인스턴스 필드를 지연 초기화해야 한다면 이중검사(double- check)관용구 (volatile) 를 사용하라
  - > 이중 검사 : 필드를 두번 검사하는 방식 , 한번은 동기화 없이 한번은 동기화하여
~~~ java 
private volatile Fieldiype field;

private FieldType getField() {
    FieldType result = field;
    if (result != null) { // 첫번째검사(락사용안함》
        return result;
    
    synchronized(this) {
        if (field = null.) // 두번째검사(락사용)
        field = coniputeFieldValueO; return field;
    }
}
~~~ 

~~~ java
//지연초기화란? chatGPT
public class MyClass {
    private MyOtherClass myOtherObject;

    public MyOtherClass getMyOtherObject() {
        if (myOtherObject == null) {
            myOtherObject = new MyOtherClass();
        }
        return myOtherObject;
    }
}
~~~
# item 84 프로그램의 동작을 스레드 스케줄러에 기대지 말라
- 스레드 스케줄러는 멀티스레드 프로그램에서 CPU 자원을 스케줄링하는 시스템 컴포넌트다
- 정확성이나 성능이 스레드 스케줄러에 따라 달라지는 프로그램이라면 다른 플랫폼에 이식하기 어렵다.
  - > 멀티스레딩 방식에 어떤 스레드가 먼저 수행될지 스레드 스케줄러가 결정
- 효율적인 방법은 다음 일거리가 생길 때까지 대기하도록 하는 것 이다. 스레드는 당장 처리해야 할 작업이 없다면 실행돼서는 안 된다.
- Thread.yield를 써서 문제를 고쳐보려는 유혹을 떨쳐내자. ( 테스트할 방법이 없다 )
- 적절치 않은 알고리즘이라면 CPU 자원 낭비 , 스레드간 경쟁 상황으로 인한 성능저하가 온다.

> 진짜 원인을 찾아서 문제를 해결하라.