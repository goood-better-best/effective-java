# item 57.  지역변수의 범위를 최소화하라

1. 클래스와 멤버의 접근 권한을 최소화 하라 ( item 15 )와 취지 비슷

2. 지역변수의 범위를 줄이는 가장 강력한 기법은 '가장 처음 쓰일 때 선언하기'

3. 거의 모든 지역변수는 선언과 동시에 초기화할 것
   - 단 try-catch문에서는 예외이다.

4. 반복문은 독특한 방식으로 변수 범위를 최소화 해준다.
   - 사용 원소와 반복자의 유효범위가 반복문 종료와 함께 끝난다.
   - 변수의 유효범위가 for문의 범위와 일치하여 여러 반복문을 써도 서로 아무런 영향을 주지않는다.
     ( 단 , for문일 경우이다. while문은 그렇지 못한 케이스가 있기 떄문에 for문을 권장 )
~~~ java

Iterator<Elenient> i = c.Iterator();
while (i. hasNext()) {
        doSomethingCi.nextC));
}
Iterator<Element> i2 = c2.iterator();
    
while (i.hasNext()) {
    // 버그!
    doSomethingElse(i2.nextC));
}
~~~
    
- for문은 while문보다 짧아서 가독성도 좋다!

5. 메서드를 작게 유지하고 한가지 기능에 집중하는 것이다.
    - 기능별로 쪼개라!


# item 58. 전통적인 for 문보다는 for-each문을 사용하라

- while을 쓰는 것보다 전통적인 for문이 낫지만.. 더 나은건 ..
  - 반복자와 인덱스 변수는 코드를 지저분하게 한다.
  - 향상된 for문 (enhned for statement)을 사용하자
~~~ java
  for (Element e : elements) {
     // e로 무언가를한다，
    }
~~~
- 컬렉션 중첩해 순회시 for-each문의 이점은 커진다.
~~~ java
// 버그를 찾아보자    
enum Suit { CLUB, DIAMOND, HEART, SPADE }
enum Rank { ACE, DEUCE, ThREE, FOUR, FIVE, SIX, SEVEN, EIGHT,
NINE, TEN, JACK, QUEEN, KING }

static CoUection<Suit> suits = Arrays.asList(Suit.valuesO);
 static Collection<Rank> ranks = Arrays.asList(Rank.valuesO);
 
List<Card>deck=newArrayList<>();
 for(IteratorzSuit>i=suits.iteratorO;i.hasNextO;)
    for (Iterator<Rank> j = ranks.iteratorO; j.hasNextO; ) 
        deck.add(newCard(i.nextO, j.nextO)); 
~~~
- Rank 하나 마다 불리는 마지막 줄의 i.netx() 숫자가 바닥이 나면 NoSuchElementException 발생

~~~ java
for (Suit suit : suits)
    for (Rank rank : ranks)
        deck.add(new Card(suit, rank));
~~~ 
- 간결하게 변경 가능

### for-each 문을 사용할 수 없는 상황 세가지
- 파괴적인 필터링 (destructive filtering)
  - 컬렉션 순회 후 원소를 제거해야한다면 remove메서드를 호출해야 함. 자바 8부터는 Colletion의 removeIf 메서드를 사용해 컬렉션을 명시적 순회를 피할 수 있다.?
  - 사용할 수없는게 아니라 사용할 필요가 없는건 아닌지...
  
- 변형(transforming)
  - 일부 혹은 전체 원소를 교체햐애한다면 리스트의 반복자나 배열의 인덱스를 사용해야한다.

- 병렬 반복(parallel iteration)
  - 병렬 순회해야한다면 반복자와 인덱스 변수를 사용해 엄격하게 제어해야한다.

# item 59. 라이브러리를 익히고 사용하라
- 무작위 정수를 만드는 코드
~~~ java
static Random rnd = new Random();
   
static mnt random(int n) {
    return Math.abs(rnd.nextlntO) % n;
 }
~~~
- n이 그리크지않은 2의 제곱수라면 얼마지나지 않아 수열이 반복된다. 
- n이 2의 제곱수가 아니라면 평균적으로 몇몇 숫자가 더 반복된다. n이 크면 더 두드러진다.
- 지정한 바깥수가 종종 튀어 나올수 있다.
  - rnd.nextInt()가 음수가 아닌 정수로 매핑하기 떄문
  - nextInt()가 Integer.MIN_VALUE 를 반환하면 Math.abs도 Integer.MIN_VALUE를 반환

### 검증된 표준라이브러리를 사용하라!
- 위 문제는 자바 7부터 ThreadLocalRandom(표준 라이브러리) 으로 대체하면 대부분 잘 작동한다.
    - 고품질의 무작위 수와 속도도 더 빠르다고...
    - 포크조인 풀이나 병렬 스트림에서는 SplittableRandom을 사용하라
- 두번째로 핵심적인 일과 상관없이 시간 허비를 줄일 수 있다.
  - 하부 공사보다는 어플리케이션 개발에 집중 가능하다.
- 세번째 장점은 노력하지 않아도 성능이 지속적으로 개선된다
- 네번쨰 이점은 기능이 점점 많아진다.
- 마지막 낯익은 코드가 된다!

- 메이저 릴리스마다 주목할 만한 수많은 기능이 라이브러리 추가 된다
- 라이브러리가 방대하다면 적어도 하위 패키지에는 익숙해지자
  - java.lang
  - java.util
  - java.io
  - 이하 컬렉션 프레임워크와 스트림 라이브러리 (item 45-48)
  - java.uitl.concurrent
- 자바에서 없다면 서드파티를 이용해야한다면 구글의 구아바 정도?

# item 60. 정확한 답이 필요하다면 float와 double은 피하라
- 부동소수점 연산에 쓰이며, 넓은 범위의 수를 근사치 계산하도록 설계
  - 따라서 정확한 결과를 필요할 때 사용하면 안된다?
  - 특히 금융관련 계산과는 맞지 않는다
    - 0.1혹은 10의 거듭제곱수를 표현할 수 없기에
~~~ java
  System.out.java(1.00 - 9 * 0.10);
  //0.1 x , 0.09999999999999998을 출력한다.
~~~

- 금융계산에는 BigDecimal, int 혹은 long 을 사용해야 한다.
- 하지만 BigDecimal에는 단점이 존재
  - 기본 타입보다 쓰기가 훨씬 불편하고 느리다
- 대안으로 int와 long을 사용할 수 있지만 값의 크기가 제한되며, 소수점 관리를 직접해야한다.

[정리]
- 소수점 추적은 시스템에 맡겨, 불편함이나 성능 저하를 신경 쓰지 않겠다면 BigDecimal을 사용하라
  - 여덞가지 반올림 모드 존재 
- 9자리 십진수 -> int
- 18자리 십진수 -> long
- 18자리 넘어가면 BigDecimal

# item 61. 박싱된 기본 타입보다는 기본 타입을 사용하라
- 자바의 데이터 타입
  - 기본 타입 
    - int, double, boolean
    - 위 기본 타입을 참조 타입으로 바꿀수 있다. Integer, Double, Boolean
  - 참조 타입 - String, List

- 두 타입의 차이
  - 기본 타입은 값만 가지고 있으나, 박싱된 기본 타입은 값에 더해 식별성 (identity)란 속성을 갖는다.
  - 기본 타입의 값은 언제나 유효하나, 박싱된 기본 타입은 유효하지 않은 값 , 즉 null 값을 가질 수 있다.
  - 기본 타입이 박싱된 기본 타입보다 시간과 메모리 사용면에서 효율적이다.

~~~ java
//
  Comparator<Integer> naturalOrder = 
  (i,j)->(i<j)? -1 : (i == j ? 0 : 1);
//
  natural.Order.compare (new Integer(42), new Iriteger(42))
~~~

- 위 두번쨰 식에서 두 인스턴스가 42로 같으므로 0을 출력해야하나 실제로는 1을 출력
  - 즉 첫번째가 크다고 주장, 첫째와 둘째가 서로 다른 객체이므로 비교결과 false
  - i < j는 잘넘어가지만 i==j에서 false 발생
  - 박싱된 기본 타입에 ==연산자는 오류가 일어난다.
- Comparator.natuaralOrdal()을 사용하여 같은 타입을 비교할 수 있다.
- 오토박싱을 이용해서 구현 가능
~~~ java
Comparator<Integer> naturalOrder = (iBoxed, jBoxed) -> { 
  mnt i = iBoxed, j = jBoxed; // 오토박싱
  returni<j7-1:(i==j ? 0 : 1 ); 
};
~~~

- 기본 타입과 박싱된 기본 타입을 혼용할 경우, 박싱된 기본 타입의 박싱이 자동으로 풀린다!
~~~ java
//i 가 42라면 NPE 발생 
public class Unbelievable {
 static Integer i;
public static void main(String[] args) {
 if (i == 42)
  System.out.printin("믿을수 없군!"); 
}
}
~~~
- 심각한 성능적 문제도 존재
  - 번갈아 가면 박싱과 언박싱이 일어난다.
~~~ java
public static void main(StringE] args) { 
Long sum= 0L;
  for (long I = 0; i <= Integer.F4AX_VALUE; i- +) {
   sum += 1;
  }
System.out.println(sum);
~~~

### 그러면 언제 박싱된 기본 타입을?
1. 컬렉션의 원소, 키, 값으로 쓰일때
2. 리플렉션을 통해 메서드를 호출할 때