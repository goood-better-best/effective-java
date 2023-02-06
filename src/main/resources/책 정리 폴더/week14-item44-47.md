#Item44. 표준 함수형 인터페이스를 사용하라.

- 함수 객체를 매개변수로 받는  정적 팩터리나 생성자를 더 많이 만들도록 하자.

```java
//LinkedHashMap의 removeEldestEntry
//람다로 사용했다면 ?
protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
    return size() > 100;
}
```

```java
@Functionallnterface interface EldestEntryRemovalFunction<K,V>{
    boolean reniove(Map<K,V> map, Map.Entry<K,V> eldest) ; 
}
```
- 이러한 방법도 좋지만.. 이미 같은 모양의 인터페이스가 존재
- java.util.function
- BiPrededicate<Map<K,V>, Map.Entry<K,V>>

<hr/>

### 기본 함수형 인터페이스 (참고)
- UnaryOperator<T> ( T apply(T t ) )
- BinaryOperator<T> ( T apply(T t, T t2 ) )
- Predicate<T> ( boolean test(T t) )
- Function<T,R> ( R apply(T t))
- Supplier<T> (T get())
- Consumer<T> (void accept(T t))

<hr />

- 기본타입인 int, long, double용으로 3가지 변환이 되지만 , Function만 특이하게 매개변수화 되었음.
- Function의 변형은 9가지로 존재 (ex. long을 받아 int 반환 LongToIntFunction, int[] 인수를 받아 long반환 ToLongFunction<int[]> )
- 인수를 2개씩 받는 변형은 2가지 -> 9가지
  - BiPredicare<T,U>
  - BiFunction<T,U,R>
    - 3가지 변형 존재
    - ToIntBiFunction<T,U> , ToLongBiFunction<T,U>, ToDoubleBiFunction<T,U>
  - BiConsumer<T,U>
  - Consumer
    - ObjDoubleConsumer<T>
    - ObjIntConsumer<T>
    - ObjLongConsumer<T>
- BooleanSupplier 
  - boolean을 반환하도록 한 Supplier의 변형
- 총 43개 존재

<hr />

### 표준 함수형 인터페이스 대부분은 기본 타입만 지원한다.
- 그렇다고 박싱된 기본 타입을 사용하지는 말자 (성능 문제)

### 언제 함수형 인터페이스 인가?
- 자주 쓰이며, 이름 자체가 용도를 명확히 설명해준다.
- 반드시 따라야하는 규약이 있다.
- 유용한 디폴트 메서드를 제공할 수 있다.

### @FunctionalInterface
- 람다용으로 설계된 것임을 문서상으로 알려준다.
- 해당 인터페이스가 추상 메서드를 오직 하나만 가지고 있어야한다.
- 누군가가 실수로 메서드를 추가하지 못하게 막아준다.

-> 항상 직접 만든 함수형 인터페이스라면 항상 @FunctionalInterface 애너테이션을 사용하자.

### 다중 정의를 하지말 것
- 사용의 모호함을 안겨준다. (ExceutorService submit 메서드, Callable<T>를 받는 것과 Runnable을 받는 것을 다중정의)

<hr>

# Item45. 스트림을 주의해서 사용하라
## 스트림?
  - stream은 데이터 원소의 유한 혹은 무한 시퀀스이다.
  - 스트림 파이프라인은 이 원소들로 수행하는 연산 단계의 개념이다.
  - 소스 스트림 시작 -> 중간 연산 -> 종단 연산
  - 스트림 파이프라인은 지연평가 (lazy evaluation)
    - 종단 연산이 없다면 무한 스트림
  - 메서드 연쇄를 지원하는 플루언트 API (fluent API)
    - 단 하나의 표현식
  - 병렬로 실행하려면 parallel 메서드 호출하면 되나, 효과를 볼 수 있는 상황은 많이 없다.

### 짧고 간결해야 한다. (코드 45- 1~3 참고)
  - 람다에서 매개변수이름은 잘지어야 가독성이 유지된다.
    
### 자바는 char용 스트림을 지원하지 않는다
  - "Hello world!".chars().forEach(System.out::print);
  - 721011081081113211911111410810
  - char 처리는 stream을 삼가는 편이 낫다.

### 코드 블록의 장점
- 지역변수를 수정할 수 있다. 
  - final , 사실상 final인 변수만 읽을 수 있음
    - 지역변수는 쓰레드 공유가 안된다.
    - 람다는 별도의 쓰레드에서 실행이 가능
    - 다른 쓰레드의 스택 영역의 지역변수를 복사하여 참조한다.
    - 복사본은 final 이어야한다.
- return 을 통해 메서드 빠져나가거나 , break or continue문 사용가능
- 메서드 선언에 명시된 검사 예외를 던진다.

### 언제 스트림인가?
- 원소들의 시퀀스를 일관되게 변환
- 원소들의 시퀀스를 필터링
- 원소들의 시퀀스를 하나의 연산을 사용해 결합
- 원소들의 시퀀스를 컬렉션에 모은다
- 원소들의 시퀀스를 특정 조건을 만족하는 원소를 찾는다.
- 여러 단계를 통과한다면 , 동시접근이 힘든 케이스니 고려해 볼 것
  - 원래의 값을 잃는 구조 

<hr>

### 평탄화 (flattening)

- flatMap -> 스트림의 원소 각각을 스트림으로 매핑한 다음 , 그 스트림들의 다시 하나의 스트림으로 합침 ( 평탄화 )

```java
private static List<Card> newDeck() {
    List<Card> result = new ArrayListcQ;
    for (Suit suit : Suit.valuesO){
        for (Rank rank : Rank.vaLuesO){
            result.add(new Card(suit, rank));
        }
    return result;
    }
}

/////


private static List<Card> newDeck(){
        return Streaa.of(Suit.vatuesO)
        .flatMap(suit->Stream.of(Rank.valuesQ)
        .rnap(rank->new Card(suit,rank)))
        .collect(toList0);
        }

```

<hr>
  
#Item46. 스트림에서는 부작용(side effect) 없는 함수를 사용하라

- 스트림 패러다임은 일련의 변환 (transformation)으로 재구성하는 것이다.

```java
//좋지 않은 예시다. 반복적이고 길고 유지보수에 좋지않다.
//groupingBy를 이용해보자.
Map<String, Long> freq = newHashMap.O; try(Strearn<String>words=newScanner(file).tokens){
    words.forEach(word -> {
        freq.merge(word.toLowerCaseO, 1L, Long::sum); }):
        }
```

- forEach 연산은 스트림 계산 결과를 보고할 때만 쓰고 계산시에는 사용하지 말자.
- 위 코드는 Collector를 사용
  - java.util.stream.Collectors (39개의 메서드)
    - toList() 리스트
    - toSet() 집합
    - toCollection(collectionFactory) 지정된 컬렉션 타입

###  Collections의 36개 메서드
- http://bit.ly/2MvTOAR
- toList
- toSet
- toMap
- groupingBy
  - 집합 값을 가지는 map
- joining
  - [came, saw, conquered]...

