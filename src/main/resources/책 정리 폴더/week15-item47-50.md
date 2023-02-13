# item47. 반환 타입으로는 스트림보다 컬렉션이 낫다.

- 스트림은 반복을 지원하지 않는다.
- 만약 스트림을 반환하면 for-each로 반복하길 원하는 사용자는 불편해진다
- 스트림은 Iterable의 모든 추상 메소드를 포함하고 있지만, 확장을 하지 않아서 쓸 수가 없다
- 우회하는 방법으로 iterator 메소드에 메소드 참조를 걸면 되는데 당연히 좋지 않은 방법

```java
for (ProcessHandle ph : (Iterable<ProcessHandle>) ProcessHandle.allProcesses()::iterator) {}
```

- 너무 난잡하고 보기 어려움
- 어댑터를 사용할 수 도 있긴 하다

```java
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
    return stream::iterator;
}
```

- 반대로 stream to iterable도 가능한데, 자바가 기본으로 제공하는 기능이 아니라 불편하다
- 즉 사람들마다 stream 기능을 쓰고 싶은 사람도 있고, iterable기능을 사용하고 싶어하는 사람이 있다
- Collection 을 사용하면 괜찮아지는데, Iterable의 하위타입이면서 stream 메소드도 제공해서 반복과 스트림 둘 다 지원한다.
- 따라서 원소 시퀀스를 반환하는 공개 API의 반환 타입은 Collection이나 그 하위 타입을 쓰는게 일반적이다.
- 사이즈를 위해 AbstractCollection 같은 클래스를 사용해 커스텀 컬렉션을 생성해서 리턴해 주기도 한다
- 결국 Collection 리턴을 통해 좀더 유연하게 대처하자는 말

# item 48. 스트림 병렬화는 주의해서 적용하라

- 자바는 다양한 병렬화 기법들을 적용시켰는데, stream도 parallel메소드를 통해 병렬화를 지원한다
- 물론 작성은 쉬워졌지만, 잘 쓰긴 어렵다

```java
public static void main(String[] args) {
    primes().map(p -> Two.pow(p.initValueExact()).subtract(ONE))
        .filter(mersenne -> mersenne.isProbablePrime(50))
        .limit(20)
        .forEach(System.out::println);
}

static Stream<BigInteger> primes() {
    return stream.iterate(TWO, BigInteger::nextProbablePrime);
        }
```

- 해당 메소드를 실행하면 특정 시간이 걸리는데, 좀 효율 높이겠다고 parallel을 쓴 순간 무한반복이 발생한다.
- 소스가 Stream.iterate거나 중간 연산으로 limit을 쓰면 파이프라인 병렬화로는 성능개선이 안된다.
- 이런식으로 실제로 성능이 안좋아지거나, 작동이 안하는 경우들이 생길 수 있다
- 보통 스트림 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int범위, long 범위 일 때 병렬화 효과가 좋다
- 위의 자료구조들은 참조 지역성이 뛰어난데, 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다.
- 하지만 참조들이 가리키는 실제 객체가 메모리에서 서로 떨어져 있을 수 있는데, 그러면 참조 지역성이 나빠진다.
- 그런 경우 스레드는 데이터가 주 메모리에서 캐시 메모리로 전송되어 오기를 기다리며 대부분의 시간을 멍때리게 된다
- 따라서 참조 지역성은 다량의 데이터를 처리하는 벌크 연산을 병렬화할 때 아주 중요한 요소이다
- 참조 지역성이 가장 좋은건 배열인데, 메모리에 연속해서 저장되기 때문이다

<hr />

- 스트림의 파이프라인이 만약 순차적인 연산이면 병렬화 효과가 매우 축소된다
- 병렬화에 가장 적합한 것은 reduction인데, 파이프라인에서 만들어지는 모든 원소를 합치는 작업인데 병렬화에 적합하다
- 반대로 collect메소드는 병렬화에 부적합하다

<hr />

- 전반적으로 병렬화는 성능 최적화 수단이므로 다른 최적화와 마찬가지로 성능 테스트가 반드시 필요하다
- 보통 스트림 병렬화할 일이 매우 적어지고 코딩을 안하는 느낌이 들껀데, 실제로 그렇다
- 병렬화해서 효과를 보는 경우가 매우 적다
- 잘 취사선택 해서 쓰는게 필요하다

# item 49. 매개변수가 유효한지 검사하라
- 메소드와 생성자 대부분은 입력 매개변수의 값이 특정 조건을 만족하길 원한다
- 예를들어 null체크를 한다던가..
- 오류를 빨리 잡아야 한다는 일반 원칙 때문이기도 하다
- 메소드 내부가 실행되기 전에 매개변수를 확인하면 깔끔하게 예외를 던질 수 있다
- 매개변수 검사를 제대로못하면 다음과 같은 문제가 생길 수 있다
  - 메소드가 수행되는 중간에 모호한 예외를 던지며 실패할 수 있다
  - 더 나쁜건 메소드가 잘 수행됐는데 잘못된 결과를 반환하는 경우가 생긴다

<hr />

- public과 protected 메소드는 매개변수 값이 잘못됐을 때 던지는 예외를 문서화 해야 한다.

```java
/**
 * @param m 계수(양수여야 한다.)
 *          ...
 */
public BigItneger mod(BigInteger m) {
    if (m.signum() <= 0) {
        throw new Arithmetic...
    }
}
```

- 참고로 널검사는 Objects.nonNull을 쓰면 편하다
- assert를 통해서 유효성 검증도 가능하다

```java
public static void sort(int a) {
    assert a != null
        ...
}
```

- 지나치게 유효성 검사 비용이 높거나 하는 특이 케이스를 제외하곤 메소드 파라미터 유효성 검사를 실행해 줘야 한다

# item 50. 적시에 방어적 복사본을 만들라

- 자바는 메모리 충돌등의 오류에서 안전하다
- 하지만 아무리 안전해도 방어적인 프로그래밍은 당연히 필요
- 어떠한 객체든 그 객체의 허락 없이는 외부에서 내부를 수정하는 일은 불가능하다
- 잘 짜야하는데 아래같이

```java
public final class Period {
    private final Date start;
    private final Date end;
    
    public period(Date start, Date end) {
        if (start > end) {
            error
        }
        
        this.start = start;
        this.end = end;
    }
}
```

- 얼핏 보기엔 불변처럼 보이지만 Date자체가 변경이 가능해서 깨뜨릴 수 있다

```java
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
end.setYear(11);
```

- 자바 8 이후론 간단하게 Date대신 불변의 Instant(LocalDateTime 등) 을 쓰면 된다
- 하지만 옛날소스들은 Date의 흔적이 남아 있을 수 있다
- 이런 문제를 해결하기 위해선 방어적 복사를 해야한다.

```java
public final class Period {
  private final Date start;
  private final Date end;

  public period(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());
    if (start > end) {
      error
    }
  }
}
```

- 가급적 이런 문제가 발생하지 않으려면 불변객체를 쓰는게 사실 제일 깔끔하다