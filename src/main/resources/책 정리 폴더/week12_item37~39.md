## 아이템 37. ordinal 인덱싱 대신 EnumMap을 사용하라

- ordinal로 인덱스를 얻는 경우가 있다.

```java
public class Plant {
    enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL}

    final String name;
    final LifeCycle lifeCycle;

    Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}

```

- 이 정원에 심은 식물들을 배열 하나로 관리하고 생애주기 별로 묶어본다

```java
Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
for (int i =0; i < plantsByLifeCycle.length; i++) {
        plantsByLifeCycle[i] = new HashSet<>();
}

for (Plant p : list) {
    plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
}

for (int i = 0; i < plantsByLifeCycle.length; i++) {
    System.out.println(Plant.LifeCycle.values()[i]);
    System.out.println(plantsByLifeCycle[i]);
}
```

- 잘 동작은 하지만 정확한 정숫값을 사용해 작성자가 직접 검증을 해야한다
- 정수는 열거타입과는 다르게 타입이 안전하지 않다
- 이런 부분을 해결하기 위해 EnumMap을 제공한다

```java
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle =
        new EnumMap<>(Plant.LifeCycle.class);
for (Plant.LifeCycle lc : Plant.LifeCycle.values()) {
    plantsByLifeCycle.put(lc, new HashSet<>());
}

for (Plant p : list) {
    plantsByLifeCycle.get(p.lifeCycle).add(p);
}
System.out.println(plantsByLifeCycle);
```

- 짧고 안전하다
- 맵의 키인 열거타입도 출력용 문자를 제공해 따로 레이블을 달 일도 없다
- 추가로 배열 인덱스 계산하는 과정에서 오류가 날 가능성이 사라진다
- 스트림을 쓰면 좀 더 깔끔해진다

```java
System.out.println(Arrays.stream(garden)
        .collect(groupingBy(p -> p.lifeCycle)));
```

- 이 코드는 EnumMap이 아닌 고유 맵 구현체를 사용해서 성능이 떨어진다
- groupingBy 메소드에서 mapFactory 매개변수에 원하는 맵 구현체를 명시해 주면 된다

```java
System.out.println(Arrays.stream(garden)
        .collect(groupingBy(p -> p.lifeCycle,
        () -> new EnumMap<>(LifeCycle.class), toSet())));
```

- 이런식으로 ordinal을 쓰면 인덱스 오류나 npe등이 발생할 수 있어서 EnumMap을 쓰는게 더 안전하고 소스 유지보수에도 더 좋다.

## 아이템 38. 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

- 타입 안전 열거 패턴은 확장할 수 있다.
- 대부분의 상황에서 열거타입을 확장하는건 좋지 않지만, 연산 코드같은 경우는 쓸만하다

```java
public interface Operation {
    double apply(double x, double y);
}

public enum BasicOperation implements Operation {
    PLUS("+") {
        public double apply (double x, double y) {return x + y; }
    }
    MINUS ...
}
```

- 열거형 타입인 BasicOperation은 확장이 안되지만 인터페이스인 Operation은 확장이 가능하다
- 이렇게 하면 Operation을 구현한 또 다른 열거 타입을 정의해 기본 타입인 BasicOperation을 대체할 수 있다.

```java
public enum ExtendedOperation implements Operation {
    EXP("^") {
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    }
    ...
}
```

- 이런식으로 Operation 인터페이스로 선언된 부분에선 ExtendedOperation으로 대체가 가능하다.
- 아래처럼 215페이지에서 짰던 테스트를 ExtendedOperation의 모든 원소를 테스트하도록 수정이 가능하다

```java
public static void main(String[] args) {
    double x = Double.parseDouble(args[0]);
    double y = Double.parseDouble(args[1]);
    test(ExtendedOperation.class, x, y);
}

private static <T extends Enum<T> & Operation> void test(Class<T> onEnumType, double x, double y) {
    for (Operation op : onEnumType.getEnumConstatns()) {
        ...
    }
}
```

- 특정 Operation의 클래스 타입을 받아 처리해 준다
- 이런식으로 확장 가능한 열거 타입을 사용하게 되면 유용한 점이 있지만, 서로 열거 타입끼리 구현을 상속할 수 없는 문제가 있다
- 이럴 경우엔 인터페이스에 디폴트 메소드를 사용하면 좋음

## 아이템 39. 명명 패턴보단 애너테이션을 사용하라

- JUnit 3같은 경우는 메소드 이름을 무조건 test로 시작해야 했음
- 이런건 오타가 나면 작동을 안하는 단점이 있고, 올바른 프로그램 요소에서만 사용되리란 보증이 없다
- 예를 들어 클래스 명을 TestSafetyMechanisms 같이 지어서 던져도 테스트가 제대로 돌지 않는다
- 또다른 문제로는 프로그램 요소를 매개변수로 던질 수가 없다(?)
- 이런 문제들을 해결하기 위해서 적용한게 어노테이션임

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 테스트 메소드임을 선언하는 애노테이션
 * 매개변수 없는 정적 메소드 전용
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TEST {}
```

- 이 Test 어노테이션은 @Retention과 @Target 어노테이션을 포함한다
- 간단하게 런타임에도 유지되어야 하고 메소드에서만 사용되어야 한다고 알려주는 어노테이션
- 이런식으로 제약을 줘서 사용이 가능하다
- 만약 Test 어노테이션에 오타가 있거나 잘못 쓰면 컴파일 타임에 에러가 나서 런타임 오류가 발생할 일이 없어진다

```java
public class Sample {
    @Test
    public static void m1 {...}
    
    @Test
    public static void m2 {...}

    @Test
    public void m5 {...}
    
    public void m6 {...}
}
```

- 이런식으로 사용할 경우 m1, m2는 잘 동작하고 m5는 정적메소드가 아니라 컴파일 오류, m6는 테스트 어노테이션이 없어 테스트 도구가 무시하게 된다
- 이런식으로 테스트 어노테이션은 클래스의 의미에 직접적으로 영햐을 주지는 않고, 추가적인 정보를 제공하고 특별한 처리가 가능하게 해준다
- 아래처럼 어노테이션이 있을 경우에 특별한 처리등을 할 수 있게 해주기도 한다

```java
import java.lang.reflect.InvocationTargetException;

public class RunTest {
    ...

    Class<?> testClass = Class.forName(args[0]);
    for(
    Method m :testClass.getDeclaredMethods())

    {
        if (m.isAnnotationPresent(Test.class)) {
            tests++;
            ...기타 로직;

            try {
                ...
            } catch (InvocationTargetException e) {
                throw...
            }
        }
    }
}
```

- 문제가 있을 시 예외를 던지게 해주는 등 @Test 어노테이션이 달린 메소드들만 따로 처리가 가능하게 해준다
- 좀더 구체적으로 특정 예외를 던져야만 성공하는 테스트를 지원하도록 해보면

```java
/**
 * 명시한 예외를 던져야 성공하는 테스트 메소드용 어노테이션
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```

- 실제로 사용해보면

```java
public class Sample2 {
    @ExceptionTest(ArithmeticException.class)
    public static void m1() {
        int i = 0;
        i = i / i;
    }
}
```

- 이런식으로 사용이 가능하다
- 배열로도 받는게 가능하다

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable>[] value();
}
```

- 이런식으로 사용하면 매우 유연해지는데 중괄호로 감싸고 쉼표로 구분해 주면 된다

```java
public class Sample2 {
    @ExceptionTest({ArithmeticException.class, NullPointerException.class})
    public static void m1() {
        Integer a = null;
        int b = a.parseInt();
    }
}
```

- 여러 개의 값을 받도록 하는 어노테이션도 있다
- @Repeatable

```java
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
    Class<? extends Throwable>[] value();
}

// 컨테이너 어노테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
    ExceptionTest[] value();
}
```

- 이제 반복적으로 어노테이션 사용이 가능해진다

```java
public class Sample2 {
    @ExceptionTest(ArithmeticException.class)
    @ExceptionTest(NullPointerException.class)
    public static void m1() {
        int i = 0;
        i = i / i;
    }
}
```