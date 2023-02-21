# item 51. 메서드 시그니처를 신중히 설계하라 
  - 메서드 시그니처? 메서드 명과 매개변수 
1. 메서드 이름을 신중히 짓자 
    - 같은 패키지의 이름과 일관되게, 개발자들이 많이 쓰는, 길지 않은 
2. 편의 메서드를 너무 많이 만들지 말자. 
    - 확신이 서지 않는다면 만들지 말자 
    - 편의 클래스? 편의를 위한 클래스 ex) Collections.min, max
3. 매개변수 목록을 짧게 유지하자
    - 4개 이하 good. 적을수록 좋음 
    - 같은 타입의 매개변수가 여러개 연달아 나오는 경우 해롭다. 실수로 순서 바꿔도 그대로 컴파일되고 의도와 다르넥 동작.
    - 매개변수 목록 줄여주는 기술
      1. 여러 메서드로 쪼갠다. 
        - 메소드가 많아질 수 있지만, 직교성을 높여 오히려 메서드 수가 줄어들 수 있음 
        - 직교성이 높다 : 공통점이 없는 기능들이 잘 분리되어 있다. 기능을 원자적으로 쪼개 제공한다. 기능적으로 쪼개다보면 중복이 줄고 결합성이 낮아진다. 
        - microsevice는 직교성이 높고 monolithic는 직교성이 낮다. 대체적으로 
      2. 매개변수 여러개를 묶어주는 도우미 클래스를 만들기 
        - 도우미 클래스는 정적 멤버 클래스로 둔다. 
      3. 앞의 두 방법의 혼합. 빌더패턴을 메서드 호출에 응용. 
        - 매개변수를 하나로 추상화한 객체를 정의하여 사용. 클라이언트는 필요한 매개변수를 다 설정한 다음, 설정한 매개변수의 유효성을 검사하여 객체를 넘김
4. 매개변수의 타입으로는 클래스보다는 인터페이스가 더 낫다. 
    - 매개변수로 적합한 인터페이스가 있다면 클래수로 구현하지 말고 직접 인터페이스 사용. 
    - ex) 매개변수로 hashMap넘길일 없음. Map으로 사용.
5. boolean보다는 원소 2개짜리 열거 타입이 낫다. 
    - 메서드 이름상 boolean을 받아야 의미가 명확할 때 제외 
    - 진짜 의미상 true, false가 아니라 다른 의미가 있다면 열거타입으로 만들어서 쓰는게 좋음.
    - 나중에 원소를 추가하기도 좋다. 


# item 52. 다중정의는 신중히 사용하라.
1. 다중정의(overloading)된 메서드들 중 어떤 메서드를 호출할지가 컴파일 타임에 정해짐
    - 재정의한 메서드는 동적으로 선택되고, 다중 정의한 메서드는 정적으로 선택됨.
    - 다중정의한 메서드 사이에서는 객체의 런타임 타입은 전혀 중요치 않고, 선택은 오직 매개변수의 컴파일 타임 타입에 의해 이뤄진다.
    - 다중정의가 혼동을 일으키는 상황을 피해야한다. 
    - 안전하고 보수적으로 가려면 매개변수 수가 같은 다중정의는 만들지 말자, 
    - 다중정의하는 대신 메서드 이름을 다르게 지어주는 길도 있다.
      - ObejctOutputStream 
      - <img width="532" alt="image" src="https://user-images.githubusercontent.com/52403454/219852490-02f166e5-a96c-41fd-a97e-d95aa23b6c3a.png">
2. 생성자는 이름을 다르게 지을 수없으니 두번째 생성자부터는 무조건 다중정의가 된다. 
    - 정적팩토리 메소드 활용 
    - 매개변수 중 하나 이상이 근본적으로 다르면 헷갈일 일이 없다.
    - <img width="776" alt="image" src="https://user-images.githubusercontent.com/52403454/219852973-21a7105c-e46a-4265-a367-7f6d4bbaea95.png">
  ~~~ java
public class SetList {
    public static void main(String[] args) {
        Set<Integer> set = new TreeSet<>();
        List<Integer> list = new ArrayList<>();

        for (int i = -3; i < 3; i++) {
            set.add(i);
            list.add(i);
        }
        for (int i = 0; i < 3; i++) {
            set.remove(i);
            list.remove(i);
        }
        System.out.println(set + " " + list);
    }
}
~~~
- list의 Remove는 Index기반. Set의 remove는 원소 기반
    - 제네릭과 오토박싱이 자바4에 등장하면서 Object와 int가 근복적으로 다르지 않게 되어서 문제가 생기기 시작.

~~~ java
//1번. Thread의 생성자 호출
new Thread(System.out::println).start();

//2번. ExecutorService의 submit 메서드 호출
ExecutorService es = Executors.newCachedThreadPool();
es.submit(System.out::println);
~~~

- 2번 실패한다. submit 다중정의 메서드 중에서는 Callable<T>를 받는 메서드가 있다.
  println이 void를 반환하니 callable과 헷갈일이 없어 보이지만, 다중정의 해소는 이렇게 동작하지 않는다,...?
  println이 다중정의 없이 단 하나만 존재했다면 제대로 컴파일 되었을 것.
  <br>
  submit도 다중정의고 println도 다중정의라는 말...?
- 메서드를 다중정의할 때 서로 다른 함수형 인터페이스라도 같은 위치의 인수로 받아서는 안된다. 
- String, StringBuffer, StringBuidler, CharBuffer등의 비슷한 부류의 타입에는 다중정의가 있으나(CharSequence, contentEquals), 완전히 같은 작업을 수행한다.
  

# item 53. 가변인수는 신중히 사용하라
  
1. 가변인수 메서드는 명시한 타입의 인수를 0개 이상 받을 수 있다.

~~~ java
static int sum(int... args) {
    int sum = 0;
    for (int arg : args) 
        sum += arg;
    return sum;
}

sum(1, 2, 3)
~~~ 
  
~~~ java
static int min(int... args) {
    if (args.length == 0)
        throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
    int min = args[0];
    for (int i = 1; i < args.length; i++)
        if (args[i] < min)
            min = args[i];
    return min;
}
~~~
  
- 인수를 0개만 넣으면 컴파일타임이 아닌 런타임에 오류가 난다. 
- 코드도 더럽다. args유효성 검사도 명시적으로 해야하고, min의 초기값을 Integer.MAX_VALUE로 설정하지 않고는 for-each도 사용하기 어려움
  
~~~ java 
static int min(int firstArgs, int... remainingArgs) {
    int min = firstArg;
    for (int arg : remainingArgs)
        if (arg < min)
            min = arg;
    return min;
}
~~~
                      
- 대신 요렇게 쓰자. 
- 가변인수는 인수 개수가 정해지지 않을 때 유용하지만, 성능에 민감한 상황이라면 걸림돌이 될 수 있다.
  - 가변인수는 호출될 때 마다 배열을 새로 하나 할당하고 초기화한다. 
- 대신 많이 쓰는 인수 개수들은 다중정의해서 쓰고, 많이 쓰지 않는 가변개수의 개수를 갖는 것들은 가변인수 메서드로 대체하면 된다. 

                      
# item 54. null이 아닌 빈 컬렉션이나 배열을 반환하라.
1. 컬렉션이나 배열같은 컨테이너가비었을 때 null을 반환하는 메서드를 사용할 때면 항시 와와 같은 방어코드를 넣어줘야 한다. 수년뒤에 오류가 발생할 수도...
  - 빈 컨테이너를 할당하는 데도 비용이 드니 null을 반환하는 것이 낫다는 주장도 있으나, 틀린 주장
      1. 할당이 성능차이라고 신경쓸 정도 수준이 못된다.
      2. 빈 컬렉션과 배열은 굳이 할당하지 않고 반환할 수 있다. 
                      
~~~ java
 public List<Cheese> getCheeses() {
    return new ArrayList<>(cheesesInStock);
}
~~~
 - 매번 빈 '불변'컬렉션을 반환하면 된다. 불변객체는 자유롭게 공유해도 안전하다. 
  - Collections.emptySet, Collections.emptyMap
2. 배열을 사용할 때도 절대 null을 반환하지 말고 길이가 0인 배열을 반환하라. 
  - 성능이 걱정된다면 길이 0짜리 배열을 미리 선언해두고 그 배열을 반환해라. 길이 0인 배열은 모두 불변이기 때문이다. 
~~~ java
  private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses() {
    return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
  ~~~
  - 단순 성능개선이 목적이라면 오히려 성능이 떨어진다는 연구 결과도 있다. 
