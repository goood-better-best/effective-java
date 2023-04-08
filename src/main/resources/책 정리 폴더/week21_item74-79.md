# 아이템 74. 메서드가 던지는 모든 예외를 문서화하라

- 메서드가 던지는 예외는 그 메서드를 올바로 사용하는 데 아주 중요한 정보이므로 각 메서드가 던지는 예외 하나하나를 문서화하는 것이 중요함.
- **검사 예외는 항상 따로따로 선언하고, 각 예외가 발생하는 상황을 자바독의 @throws 태그를 사용하여 정확히 문서화하자.**
    - 공통 상위 클래스 하나로 뭉뚱그려 선언하는 일은 삼가자.
        - 메서드 사용자에게 각 예외에 대처할 수 있는 힌트를 주지 못할뿐더러, 같은 맥락에서 발생할 여지가 있는 다른 예외들까지 삼켜버릴 수 있어 API 사용성을 크게 떨어뜨림.
    - 이 규칙에 유일한 예외가 있다면 바로 main 메서드임.
        - main은 오직 JVM만이 호출하므로 Exception을 던지도록 선언해도 괜찮음.
- 비검사 예외는 일반적으로 프로그래밍 오류를 뜻하는데, 자신이 일으킬 수 있는 오류들이 무엇인지 알려주면 프로그래머는 자연스럽게 해당 오류가 나지 않도록 코딩하게 됨.
- 발생 가능한 비검사 예외를 문서로 남기는 일은 인터페이스 메서드에서 특히 중요함.
    - 이 조건이 인터페이스의 일반 규약에 속하게 되어 그 인터페이스를 구현한 모든 구현체가 일관되게 동작하도록 해주기 때문.
- **메서드가 던질 수 있는 예외를 각각 @throws 태그로 문서화하되, 비검사 예외는 메서드 선언의throws 목록에 넣지말자.**
- 검사냐 비검사냐에 따라 API 사용자가 해야할 일이 달라지므로 이 둘을 확실히 구분해주는 게 좋음.
    - 검사 예외만 메서드 선언의 throws 문에 일일이 선언하고, 비검사 예외는 메서드 선언에는 기입하지 말자.

<br>

# 아이템 75. 예외의 상세 메시지에 실패 관련 정보를 담으라

- 예외를 잡지 못해 프로그램이 실패하면 자바 시스템은 그 예외의 스택 추적(stack trace) 정보를 자동으로 출력함.
    - 스택 추적은 예외 객체의 toString 메서드를 호출해 얻는 문자열로, 보통은 예외의 클래스 이름 뒤에 상세 메시지가 붙는 형태임.
- **실패 순간을 포착하려면 발생한 예외에 관여된 모든 매개변수와 필드의 값을 실패 메시지에 담아야 함.**
- IndexOutOfBoundsException의 상세 메시지는 범위의 최솟값과 최댓값, 그리고 그 범위를 벗어났다는 인덱스의 값을 담아야 함.
    - 셋 중 한두 개 혹은 셋 모두가 잘못됐을 수 있음.
        - 인덱스가 최솟값보다 1만큼 작거나 최댓값과 같을 경우 (인덱스는 0부터 시작하므로 최댓값과 같으면 안 됨)
        - 범위를 아주 크게 벗어난 경우
        - 최솟값이 최댓값보다 클 경우 (내부의 불변식이 심각히 깨진 경우)
    - 이상의 현상들은 모두 원인이 다르므로, 현상을 보면 무엇을 고쳐야 할지를 분석하는 데 큰 도움이 됨.
- 실패를 적절히 포착하려면 필요한 정보를 예외 생성자에서 모두 받아서 상세 메시지까지 미리 섕성해놓는 방법도 괜찮음.
- 관련 데이터를 모두 담아야 하지만 장황할 필요는 없음.
- 최종 사용자에게는 친절한 안내 메시지를 보여줘야 하는 반면, 예외 메시지는 가독성보다는 담긴 내용이 훨씬 중요함.

<br>

# 아이템 76. 가능한 한 실패 원자적으로 만들라

- **호출된 메서드가 실패하더라도 해당 객체는 메서드 호출 전 상태를 유지해야 함.**
    - 이러한 특성을 **실패 원자적(failure-atomic)**이라고 함.

### **메서드를 실패 원자적으로 만드는 방법**

1. 가장 간단한 방법은 **불변 객체로 설계하는 것**. 
    - 불변 객체의 상태는 생성 시점에 고정되어 절대 변하지 않기 때문에 불변 객체는 태생적으로 실패 원자적임.
2. 가변 객체의 메서드를 실패 원자적으로 만드는 방법은 **작업 수행에 앞서 매개변수의 유효성을 검사하는 것임.** 
    - 객체의 내부 상태를 변경하기 전에 잠재적 예외의 가능성 대부분을 걸러낼 수 있는 방법임.
3.  **실패할 가능성이 있는 모든 코드를, 객체의 상태를 바꾸는 코드보다 앞에 배치**하는 방법. 
    - 계산을 수행해보기 전에는 인수의 유효성을 검사해볼 수 없을 때, 앞서의 방식에 덧붙여 쓸 수 있는 기법임.
4. **객체의 임시 복사본에서 작업을 수행한 다음, 작업이 성공적으로 완료되면 원래 객체와 교체**하는 것임. 
    - 데이터를 임시 자료구조에 저장해 작업하는 게 더 빠를 때 적용하기 좋은 방식임.
    - 예를 들어 어떤 정렬 메서드에서는 정렬을 수행하기 전에 입력 리스트의 원소들을 배열로 옮겨 담음.
        - 배열을 사용하면 정렬 알고리즘의 반복문에서 원소들에 훨씬 빠르게 접근할 수 있기 때문.
        - 물론 이는 성능을 높이고자 취한 결정이지만, 혹시나 정렬에 실패하더라도 입력 리스트는 변하지 않는 효과를 덤으로 얻게 됨.

5. **작업 도중 발생하는 실패를 가로채는 복구 코드를 작성하여 작업 전 상태로 되돌리는 방법**. 

- 주로 (디스크 기반의) 내구성(durability)을 보장해야 하는 자료구조에 쓰이는데, 자주 쓰이는 방법은 아님.

### **메서드를 실패 원자적으로 만들 수 없는** 예외 사항

- 실패 원자성은 일반적으로 권장되는 덕목이지만 항상 달성할 수 있는 것은 아님.
- Error는 복구할 수 없으므로 AssertionError에 대해서는 실패 원자적으로 만들려는 시도조차 할 필요가 없음.

<br>

# 아이템 77. 예외를 무시하지 말라

- 예외 무시
    - 해당 메서드 호출을 try 문으로 감싼 후 catch 블록에서 아무 일도 하지 않으면 끝임.
        
        ```java
        // catch 블록을 비워두면 예외가 무시됨.
        try {
        	...
        } catch (SomeException e) { }
        ```
        
    - 예외는 문제 상황에 잘 대처하기 위해 존재하는데 **catch 블록을 비워두면 예외가 존재할 이유가 없어짐.**
    - 비유하자면 화재경보를 무시하는 수준을 넘어 아예 꺼버려, 다른 누구도 화재가 발생했음을 알지 못하게 하는 것과 같음.
- 물론 예외를 무시해야 할 때도 있음.
    - 예를 들어 FilelnputStream을 닫을 때가 그럼.
    - 입력 전용 스트림이므로 파일의 상태를 변경하지 않았으니 복구 할 것이 없으며, 스트림을 닫는다는건 필요한 정보는 이미 다 읽었다는 뜻이니 남은 작업을 중단할 이유도 없음.
- 만약 **예외를 무시하기로 했다면 catch 블록 안에 그렇게 결정한 이유를 주석으로 남기고 예외 변수의 이름도 ignored로 바꿔놓도록 하자.**
    
    ```java
    Future<Integer> f = exec.submit(planarMap::chromaticNumber);
    int numColors=4;// 기본값
    try{
    	numColors=f.get(1L, TimeUnit.SECONDS);
    } catch (TimeoutException | ExecutionException **ignored**) {
    	// 기본값 사용
    }
    ```
    
- 예외를 적절히 처리하면 오류를 완전히 피할 수도 있음.
- 예외를 무시하지 않고 바깥으로 전파되게만 놔둬도 최소한 디버깅 정보를 남긴 채 프로그램이 신속히 중단되게는 할 수 있음.

<br>

# 아이템 78. 공유 중인 가변 데이터는 동기화해 사용하라

- synchronized 키워드는 해당 메서드나 블록을 한번에 한 스레드씩 수행하도록 보장함.
    - 한 스레드가 변경하는 중이라서 상태가 일관되지 않은 순간의 객체를 다른 스레드가 보지 못하게 막는 용도.
- 한 객체가 일관된 상태를 가지고 생성되고, 이 객체에 접근하는 메서드는 객체에 락(lock)을 걸고, 락을 건 메서드는 객체의 상태를 확인하고 필요하면 수정함.
    - 객체를 하나의 일관된 상태에서 다른 일관된 상태로 변화시킴.
- 동기화의 중요한 기능
    - 동기화 없이는 한 스레드가 만든 변화를 다른 스레드에서 확인하지 못할 수 있음.
    - 동기화는 일관성이 깨진 상태를 볼 수 없게 하는 것은 물론, 동기화된 메서드나 블록에 들어간 스레드가 같은 락의 보호하에 수행된 모든 이전 수정의 최종 결과를 보게 해줌.

- 다른 스레드를 멈추는 방법
    - 스레드는 자신의 boolean 필드를 폴링하면서 그 값이 true가 되면 멈춤.
    - 이 필드를 false로 초기화해놓고, 다른 스레드에서 이 스레드를 멈추고자 할 때 true로 변경하는 식.

- 잘못된 코드 - 이 프로그램은 얼마나 오래 실행될까?
    
    ```java
    public clas StopThread {
        private static boolean stopRequested;
        
        public static void main(String[] args) throws InterrupedException {
            Thread backgroundThread = new Thread(() -> {
                int i = 0;
                while (!stopRequested)
                    i++;
            });
            backgroundThread.start();
            
            TimeUnit.SECONDS.sleep(1);
            stopRequested = true;
        }
    }
    ```
    
    - 메인 스레드가 1초 후 stopRequested를 true로 설정하면 backgroundThread는 반복문을 빠져나올 것처럼 보이지만, 영원히 수행됨.
    - 원인은 동기화 때문으로, 동기화하지 않으면 메인 스레드가 수정한 값을 백그라운드 스레드가 언제쯤에나 보게 될지 보증할 수 없음.

- 동기화가 빠지면 가상머신이 다음과 같은 최적화를 수행할 수도 있음.
    
    ```java
    // 원래 코드 
    while(!stopRequested)
        i++;
    
    // 최적화한 코드 
    if (!stopRequested)
        while(true)
            i++;
    ```
    
    - OpenJDK 서버 VM이 실제로 적용하는 끌어올리기(hoisting)라는 최적화 기법임.
    - 이 결과 프로그램은 응답 불가(liveness failure) 상태가 되어 더 이상 진전이 없음.
- stopRequested 필드를 동기화해 접근하면 이 문제를 해결할 수 있음.
    - 이렇게 변경하면 1초 후에 종료됨.
    
    ```java
    public clas StopThread {
        private static boolean stopRequested;
        
        private static synchronized void requestStop() {
            stopRequested = true;
        }
        
        private static synchronized boolean stopRequested() {
            return stopRequested;
        }
        
        public static void main(String[] args) throws InterrupedException {
            Thread backgroundThread = new Thread(() -> {
                int i = 0;
                while (!stopRequested())
                    i++;
            });
            backgroundThread.start();
            
            TimeUnit.SECONDS.sleep(1);
            requestStop();
        }
    }
    ```
    
    - 쓰기 메서드(requestStop)와 읽기 메서드(stopRequested) 모두를 동기화함.
    - 쓰기와 읽기 모두가 동기화되지 않으면 동작을 보장하지 않음.
        - 쓰기 메서드만 동기화해서는 충분하지 않음.

- volatile 한정자는 배타적 수행과는 상관없지만 항상 가장 최근에 기록된 값을 읽게 됨을 보장함.
- volatile 필드를 사용해 스레드가 정상 종료함.
    
    ```java
    public clas StopThread {
        private static volatile boolean stopRequested;
        
        public static void main(String[] args) throws InterrupedException {
            Thread backgroundThread = new Thread(() -> {
                int i = 0;
                while (!stopRequested)
                    i++;
            });
            backgroundThread.start();
            
            TimeUnit.SECONDS.sleep(1);
            stopRequested = true;
        }
    }
    ```
    

- 잘못된 코드 - 동기화가 필요함.
    
    ```java
    private static volatile int nextSerialNumber = 0;
    
    public static int generateSerialNumber() {
        return nextSerialNumber++;
    }
    ```
    
    - 이 메서드는 매번 고유한 값을 반환할 의도로 만들어짐.
    - 이 메서드의 상태는 nextSerialNumber라는 단 하나의 필드로 결정되는데, 원자적으로 접근할 수 있고 어떤 값이든 허용함.
    - 문제는 증가 연산자(++)
        - 이 연산자는 코드상으로는 하나지만 실제로는 nextSerialNumber 필드에 두 번 접근함.
        - 만약 두 번째 스레드가 이 두 접근 사이를 비집고 들어와 값을 읽어가면 첫 번째 스레드와 똑같은 값을 돌려받게 됨.
    - generateSerialNumber 메서드에 synchronized 한정자를 붙이면 이 문제가 해결됨.
        - 동시에 호출해도 서로 간섭하지 않으며 이전 호출이 변경한 값을 읽게 된다는 뜻임.

- java.util.concurrent.atomic 패키지의 AtomicLong을 사용
    - 이 패키지에는 락 없이도(lock-free)스레드 안전한 프로그래밍을 지원하는 클래드들이 담겨 있음.
    - volatile은 동기화의 두 효과 중 통신 쪽만 지원하지만 이 패키지는 원자성(배타적 실행)까지 지원함.
    - java.util.concurrent.atomic을 이용한 락프리 동기화
        
        ```java
        private static final AtomicLong nextSerialNum = new AtomicLong();
        
        public static long generateSerialNumber() {
            return nextSerialNum.getAndIncrement();
        }
        ```
        
        - 가변 데이터를 공유하지 않고, 불변 데이터만 공유하거나 아무것도 공유하지 않는 것이 좋음.
        - 가변 데이터는 단일 스레드에서만 쓰도록 해야함.
        - 한 스레드가 데이터를 다 수정한 후 다른 스레드에 공유할 때는 해당 객체에서 공유하는 부분만 동기화해도 됨.

<br>

# 아이템 79. 과도한 동기화는 피하라

- 과도한 동기화는 성능을 떨어뜨리고, 교착 상태에 빠뜨리고, 심지어 예측할 수 없는 동작을 낳기도 함.
- **응답 불가와 안전 실패를 피하려면 동기화 메서드나 동기화 블록 안에서는 제어를 절대로 클라이언트에 양도하면 안됨.**
    - 예를 들어 동기화된 영역 안에서는 재정의할 수 있는 메서드는 호출하면 안 되며, 클라이언트가 넘겨준 함수 객쳬를 호출해서도 안됨.
- 동기화된 영역을 포함한 클래스 관점에서는 이런 메서드는 모두 바깥 세상에서 온 외계인임.
    - 그 몌서드가 무슨 일을 할지 알지 못하며 통제할 수도 없다는 의미.
- 외계인 메서드(alien method)가 하는 일에 따라 동기화된 영역은 예외를 일으키거나, 교착상태에 빠지거나, 데이터를 훼손할 수도 있음.

- 다음은 어떤 집합(Set)을 감싼 래퍼 클래스이고, 이 클래스의 클라이언트는 집합에 원소가 추가되면 알림을 받을 수 있다. → 관찰자 패턴
    
    ```java
    public class ObservableSet<E> extends ForwardingSet<E> {
        public ObservableSet(Set<E> set) { super(set); }
        
        private final List<SetObserver<E>> observers
                = new ArrayList<>();
    
        public void addObserver(SetObserver<E> observer) {
            synchronized(observers) {
                observers.add(observer);
            }
        }
    
        public boolean removeObserver(SetObserver<E> observer) {
            synchronized(observers) {
                return observers.remove(observer);
            }
        }
    
        private void notifyElementAdded(E element) {
            synchronized(observers) {
                for (SetObserver<E> observer : observers)
                    observer.added(this, element);
            }
        }
        
        @Override public boolean add(E element) {
            boolean added = super.add(element);
            if (added)
                notifyElementAdded(element);
            return added;
        }
    
        @Override public boolean addAll(Collection<? extends E> c) {
            boolean result = false;
            for (E element : c)
                result |= add(element);  // notifyElementAdded를 호출한다.
            return result;
        }
    }
    ```
    
    - 관찰자들은 addObserver와 removeObserver 메서드를 호출해 구독을 신청하거나 해지함.
        - 두 경우 모두 다음 콜백 인터페이스의 인스턴스를 메서드에 건넴.
            
            ```java
            public interface SetObserver<E> {
                // ObservableSet에 원소가 더해지면 호출된다.
                void added(ObservableSet<E> set, E element);
            }
            ```
            
    - 예컨대 다음 프로그램은 0부터 99까지를 출력함.
        
        ```java
        public static void main(String[] args) {
        	ObservableSet<Integer> set = new ObservableSet<>(New HashSet<>());
        	
        	set.addObserver((s, e) -> System.out.println(e)); // add 된 원소를 출력
        
        	for (int i = 0; i < 100; i++) 
        		set.add(i);
        }
        ```
        
    - 평상시에는 위와 같이 집합에 추가된 정수값을 출력하다가, 그 값이 23이면 자기 자신을 제거하는 관찰차 추가
        
        ```java
        public static void main(String[] args) {
        	ObservableSet<Integer> set = new ObservableSet<>(New HashSet<>());
        	
        	set.addObserver(new SetObserver<Integer>() {
        		public void added(ObservableSet<Integer> s, Integer e) {
        			System.out.println(e);
        			if (e == 23) 
        				s.removeObserver(this);
        		}
        	});
        
        	for (int i = 0; i < 100; i++) 
        		set.add(i);
        }
        ```
        
        - 이 프로그램은 0부터 23까지 출력한 후 관찰자 자신을 구독 해지한 다음 조용히 종료할 것임.
            - 그런데 실제로 실행해 보면 그렇게 진행되지 않음.
            - 이 프로그램은 23까지 출력한 다음 ConcurrentModificationException을 던짐.
            - 관찰자의 added 메서드 호출이 일어난 시점이 notifyElementAdded가 관찰차들의 리스트를 순회하는 도중이기 때문.
            - added 메서드는 ObservableSet의 remove Observer 메서드를 호출하고, 이 메서드는다시 observers,remove메서드를 호출하는데, 여기저 문제가 발생함.
            - 리스트에서 원소를 제거하려 하는데, 마침 지금은 이 리스트를 순회하는 도중 즉, 허용되지 않은 동작임.
            - notify ElementAdded 메서드에서 수행하는 순회는 동기화 블록 안에 있으므로 동시 수정이 일어나지 않도록 보장하지만, 정작 자신이 콜백을 거쳐 되돌아와 수정 하는 것까지 막지는 못함.
- 구독해지를 하는 관찰자를 작성하는데, removeObserver를 직접 호출하지 않고 실행자 서비스를 사용해 다른 스레드한테 부탁하는 예제
    
    ```java
    set.addObserver(new SetObserver<Integer>() {
        public void added(ObservableSet<Integer> s, Integer e) {
            System.out.println(e);
            if (e == 23) {
                ExecutorService exec = Executors.newSingleThreadExecutor();
                try {
                    exec.submit(() -> s.removeObserver(this)).get();
                } catch (ExecutionException | InterruptedException ex) {
                    throw new AssertionError(ex);
                } finally {
                    exec.shutdown();
                }
            }
        }
    });
    ```
    
    - 이 프로그램을 실행하면 예외는 나지 않지만 교착상태에 빠짐.
    - 백그라운드 스레드가 s.removeObserver를 호출하면 관찰자를 잠그려 시도하지만 락을 얻을 수 없음.
        - 메인 스레드가 이미 락을 쥐고 있기 때문.
        - 그와 동시에 메인 스레드는 백그라운드 스레드가 관찰자를 제거하기만을 기다리는 중. 바로 교착상태임.
- 위와 똑같은 상황이지만 불변식이 임시로 깨진 경우
    - 자바 언어의 락은 재진입(reentrant)을 허용하므로 교착상태에 빠지지는 않음.
    - 예외를 발생시킨 첫 번째 예에서라면 외계인 메서드를 호출하는 스레드는 이미 락을 쥐고 있으므로 다음번 락 획득도 성공함.
    - 그 락이 보호하는 데이터에 대해 개념적으로 관련이 없는 다른 작업이 진행 중인데도 그럼.
    - 재진입 가능락은 객체 지향 멀티 스레드 프로그램을 쉽게 구현할 수 있도록 해주지만, 응답 불가(교착상태)가 될 상황을 안전 실패(데이터 훼손)로 변모시킬 수도 있음.
- 외계인 메서드 호출을 동기화 블록 바깥으로 옮기면 됨.
    - notifyElenientAdded메서드에서 라면 관찰자 리스트를 복사해 쓰면 락 없이도 안전하게 순회할 수 있음.
        - 이 방식을 적용하면 앞서의 두 예제에서 예외 발생과 교착상태 증상이 사라짐.
            
            ```java
            private void notifyElementAdded(E element) {
                List<SetObserver<E>> snapshot = null;
                synchronized(observers) {
                    snapshot = new ArrayList<>(observers);
                }
                for (SetObserver<E> observer : snapshot)
                    observer.added(this, element);
            }
            ```
            
- 사실 외계인 메서드 호출을 동기화 블록 바깥으로 옮기는 더 나은 방법이 있음.
    - 자바의 동시성 컬렉션 라이브러리의 CopyOnWriteArrayList가 정확히 이 목적으로 특별히 설계된 것.
    - 이름이 말해주듯 ArrayList를 구현한 클래스로, 내부를 변경하는 작업은 항상 깨끗한 복사본을 만들어 수행하도록 구현함.
    - 내부의 배열은 절대 수정되지 않으니 순회할 때 락이 필요 없어 매우 빠름.
    - 명시적으로 동기화한 곳이 사라짐.
        
        ```java
        private final List<SetObserver<E>> observers =
                new CopyOnWriteArrayList<>();
        
        public void addObserver(SetObserver<E> observer) {
            observers.add(observer);
        }
        
        public boolean removeObserver(SetObserver<E> observer) {
            return observers.remove(observer);
        }
        
        private void notifyElementAdded(E element) {
            for (SetObserver<E> observer : observers)
                observer.added(this, element);
        }
        ```
        
- 동기화 영역 바깥에서 호출되는 외계인 메서드를 열린 호출(open call）이라 함.
    - 외계인 메서드는 얼마나 오래 실행될지 알 수 없는데 , 동기화 영역 안에서 호출된다면 그동안 다른 스레드는 보호된 자원을 사용하지 못하고 대기해야만 함.
    - 따라서 열린 호출은 실패 방지 효과 외에도 동시성 효율을 크게 개선해줌.

- 가변 클래스를 작성한다면 두 선택지 중 하나를 따르자.
    1. 동기화를 전혀 하지 말고, 그 클래스를 동시에 사용해야 하는 클래스가 외부에서 알아서 동기화하게 하자.
    2. 동기화를 내부에서 수행해 스레드 안전한 클래스로 만들자. 단, 클라이언트가 외부에서 객체 전체에 락을 거는 것보다 동시성을 월등히 개선할 수 있을 때만 선택해야 함.
    - 선택하기 어렵다면 동기화하지 말고, 대신 문서에 "스레드 안전하지 않다"고 명시하기
