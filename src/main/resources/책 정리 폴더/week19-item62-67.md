# 아이템 62. 다른 타입이 적절하다면 문자열 사용을 피하라

- 문자열은 다른 값 타입을 대신하기에 적합하지 않다.
- 예를들어 수치형이면 int, float등을 쓰고 예/아니오면 boolean 등을 써야 한다
- 마찬가지로 열거형 타입을 대신하기에는 적합하지 않다.
- 혼합타입도 올바르지 않음

```java
String compoundKey = className + "#" + i.next()
```

- 혼종 그자체다
- 각 타입의 기능을 쓸 수 없고 단순 String의 기능만 사용이 가능하다

<hr />

- 권한 표현에도 좋지 않다

```java
public class ThreadLocal {
    private ThreadLocal() { }
    
    public static void set(String key, Object value);
    
    public static Object get(String key);
}
```

- 이렇게 쓰면 키를 같은걸 쓰면 같은 변수를 공유하는 문제가 발생한다
- 추가로 보안도 취약하다
- 아래처럼 문자열 대신 위조할 수 없는 키를 사용하면 된다.

```java
public class ThreadLocal {
    private ThreadLocal() { }
    
    public static class Key {
        key() {}
    }
    
    public static Key getKey() {
        return new Key();
    }
    
    public static void set(Key key, Object value);
    public static Object get(Key key);
}
```

- 좀 더 개선해보면
- set get이 굳이 스태틱일 필요가 없으니 Key 클래스의 인스턴스 메소드로 바꾼다
- 이러면 Key는 더 이상 스레드 지역변수를 구분하기 위한 키가 아니라 그 자체가 스레드 지역변수가 되버림
- 외부 클래스인 ThreadLocal을 날리고 중첩 클래스인 Key의 이름을 ThreadLocal로 바꾼다. 
- 쓰기 좋게 매개변수화 타입 T로 선언해서 써주자
- 
```java
public final class ThreadLocal<T> {
    public ThreadLocal();
    public void set(T value);
    public T get();
}
```

- 이런식으로 쓰자

# 아이템 63. 문자열 연결은 느리니 주의하자

- 문자열 연산자 + 는 편리하지만 성능이 저하된다

```java
public String statement() {
    String result = "";
    for (int i = 0; i < numItems(); i++) {
        result += lineForItem(i);
    }
    return result;
}
```

- 아주 느리니까 StringBuilder를 쓰자

```java
public String statement() {
    StringBuilder b = new StringBuilder(numItems() * LINE_WIDTH);
    for (int i = 0; i < numItems(); i++) {
        b.append(lineForItem(i));
    }
    return result;
}
```

- String + 를 컴파일 시에 StringBuilder로 동작하도록 개선했다고 들었는데 정확한건 좀 더 찾아봐야 될듯

# 아이템 64. 객체는 인터페이스를 사용해 참조하라

- 적합한 인터페이스만 있다면 매개변수 뿐 아니라 반환값, 변수, 필드를 전부 인터페이스 타입으로 선언해라
- 객체의 실제 클래스를 사용해야 할 상황은 오직 생성자로 생성할 때뿐임

```java
Set<Son> sonSet = new LinkedHashSet<>();
```

- 이렇게 할 경우에 좀 더 유연해진다 (다형성)

```java
sonSet = new HashSet<>();
```

- 당연하지만 인터페이스에 새로운 기능을 명시하면 구현 클래스들도 새로운 기능을 작성해야 한다.

<hr />

- 클래스를 직접 참조해야 할 때도 있다.
  - 적합한 인터페이스가 없는 String 같은 값 클래스는 당연히 클래스를 참조해야 한다.
  - 또 다른 케이스로는 인터페이스에서 제공하지 않는 기능을 클래스가 갖고 있는 경우에도 클래스를 참조해야 한다.
    - 예를들어 PriorityQueue는 Queue 인터페이스에는 없는 comparator 메소드를 제공한다. 이런경우 Queue로 선언하면 안됨

# 아이템 65. 리플렉션보다는 인터페이스를 사용하라

- 리플렉션을 쓰면 임의의 클래스에 접근할 수 있다.
- 생성자, 메소드, 필드 인스턴스등 다양하게 접근이 가능한데, 단점도 물론 있음
  - 컴파일타임 타입 검사가 주는 이점을 하나도 못누림 
    - -> 리플렉션 자체가 런타임에 동작하는 거기 때문에 런타임 오류가 날 가능성이 있다
  - 코드가 지저분해짐
  - 성능도 떨어짐
- 실제로 리플렉션을 쓰는 프레임워크나 도구가 있긴하지만 점차 줄이는 추세 (ex. 스프링)
- 리플렉션은 제한된 형태로만 써야 효과가 극대화 된다
- 리플렉션은 인스턴스 생성에만 쓰고 이렇게 만든 인스턴스는 인터페이스나 상위 클래스로 참조해 사용하자

```java
public static void main(String[]args){
    // 클래스 이름을 Class 객체로 변환
    Class<? extends Set<String>> cl = null;
    try {
        cl = (Class<? extends Set<String>>)
                Class.forName(arg[0]);
    } catch (ClassNotFoundException) {
        // error
    }
    
    Constructor<? extends Set<String>> cons = null;
    try {
        cons = cl.getDeclaredConstructor();    
    } catch(NoSuchMethodConstructor e) {
        // error    
    }
    
    // 집합의 인스턴스를 만든다
    Set<String> s = null;
    
    try {
        s = cons.newInstance();    
    } catch(IlleagalAccessException e) {
        ... 
    } catch {
        ...
    } catch {
        ... 에러 6개   
    }
    
    s.addAll(Arrays.asList(args).subList(1, arg.length));
}
```

- 위에 코드는 간단해 보이지만 쉽게 제네릭 집합 테스터로 변신할 수 있다.
- 말그대로 클래스 명으로 그냥 객체를 만드는 상황임
- 보통 프레임워크에서 많이 쓰고, 딱 이정도만 썼을때 아주 효율적이다
- 반면에 단점도 있는데
  - 만약에 리플렉션 없이 생성했으면 굳이 처리하지 않아 도 될 필요없는 예외를 catch를 통해 하나하나 다 잡아줘야 한다
  - 즉 파라미터로 잘못 들어오면 런타임중에 6가지 종류의 에러를 볼 수가 있다
  - 추가로 코드도 길어져서 보기 싫음
- 위의 코드를 컴파일해보면 비검사 형변환 경고가 뜨지만, 컴파일 자체는 통과하게 된다
- 런타임 에러가 발생할 여지가 아주 농후함
- 여튼 잘써야 한다.

# 아이템 66. 네이티브 메소드는 신중히 사용하라

- 자바 네이티브 인터페이스는 자바 프로그램이 네이티브 메소드를 호출하는 기술
- 여기서 네이트브 메소드는 C나 C++ 같은 네이티브 프로그래밍 언어로 작성한 메소드들
- 보통 세 가지 이유로 쓰이는데
  - 레지스트리 같은 플랫폼 특화 기능 사용
  - 네이티브 코드로 작성된 기존 라이브러리를 사용
  - 성능 개선을 목적으로 성능에 결정적인 영향을 주는 영역만 따로 네이티브 언어로 작성
- 하지만 사실상 쓸일이 이제 거의 없다
- 성능 개선을 위해서도 굳이 쓰는걸 권장하지 않음
- C같은거로 만들어진 BigInteger같은 것도 버전이 올라가면서 자바로 다시 만들었음

# 아이템 67. 최적화는 신중히 하라

- 최적화 격언 세 개가 있다

```zsh
1. (맹목적인 어리석음을 포함해) 그 어떤 핑계보다 효율성이라는 이름 아래 행해진
컴퓨터 죄악이 더 많다. (심지어 효율을 높이지도 못함)
- 윌리엄 울프

2. (전체의 97% 정도인) 자그마한 효율성은 모두 잊자.
섣부른 최적화가 만악의 근원
- 도널드 크누스

3. 최적화를 할 때는 두 규칙을 따르라
첫 번째, 하지 마라.
두 번째, (전문가 한정) 아직 하지 마라. 다시 말해,
완전히 명백하고 최적화되지 않은 해법을 찾을 때까지 하지마라
- M.A 잭슨
```

- 이 격언들은 자바가 나오기 20년전에 나온 말들
- 보통 최적화는 안좋은 방향으로 간다
- 성능 때문에 견고한 구조를 희생하지 말자
- 빠른 프로그램보다는 좋은 프로그램을 작성해라
- 중간에 변경은 항상 어렵기 때문에 설계 단계에서 부터 성능을 염두해서 짜야되느데
  - 성능을 제한하는 설계는 피해라
  - API를 설계할 때 성능에 주는 영향을 고려해라
- 만약 최적화를 해야되면
  - 프로파일링 도구를 활용하면 최적화 노력을 어디에 집중해야 하는지 알기 쉽다.
  - 특히 시스템 규모가 클 때
- 자바 같은 경우는 비교적 고 추상화된 언어라서 프로그래머가 작성하는 코드와 CPU에서 수행하는 명령 사이의 `추상화 격차`가 크다
- 즉 내 딴에는 성능 최적화라고 짰는데 CPU단에선 예측못한 성능 저하가 발생할 수 있음
- 심지어 지금은 이 책의 초판이 나올때보다 20년이 지나서 프로그램도 복잡해지고 어려우니까 최적화는 신중히 하도록 하자