# item 68 일반적으로 통용되는 명명 규칙을 따르라 
- 자바의 명명규칙
1. 철자 규칙
  - 패키지, 클래스, 인터페이스, 메서드, 필드 타입 변수는 특별한 이유가 없으면 반드시 따라야한다. 
  - 패키지,모듈 : 요소를 점(.)으로 구분, 8자 이하의 짧은 단어(의미가 통하는 약어)
  - 클래스와 인터페이스 : 하나 이상의 단어로 구성, 각 단어는 대문자로 시작, 통용되는 줄임말을 제외하면 줄여쓰지 않음. 첫 글자만 대문자 (파스칼 케이스)
  - 메서드와 필드 : 첫 글자만 소문자로 쓰는 것을 빼면 클래스 명명규칙과 같음. (카멜 케이스)
  - 상수 : 모두 대문자로 쓰고 단어 사이는 밑줄로 구분 (밑줄을 사용하는 건 상수 필드가 유일)
  - 지역변수 : 약어 ok
  - 타입 매개변수 : 임의의 타입 T, 컬렉션 원소타입 E, 맵의 키와 값에는 K/V, 예외 X, 메서드 반환 타입에는 R, 그 외의 타입 시퀀스 T,U,V, T1,T2,T3
  - ![image](https://user-images.githubusercontent.com/52403454/226360591-acf19eae-b733-46f7-b807-0db2de7a709a.png)
<br>

2. 문법
  - 철자 규칙보다 유연하고 논란이 많다. 
  - 객체를 생성할 수 있는 클래스 : 명사, 명사구
  - 객체를 생성할 수 없는 클래스 : 복수형 명사.
  - 인터페이스 이름 : 클래스와 똑같이 짓거나 able, ible로 끝나는 형용사
  - 애너테이션 : 규칙이 따로 없음
  - 메서드 이름 : 목적어를 포함한 동사, 동사구
    - boolean반환 : is,has로 시작
    - 해당 인스턴스의 속성 반환 : 명사, 명사구, get으로 시작하는 동사구 -> get의 형태만 써야한다는 주장도 있음 (빈약한 주장)
    - 객체의 타입을 바꿔서 다른 타입의 또 다른 객체를 반환하는 인스턴스 메서드 : toType ( toString, toArray)
    - 객체의 내용을 다른 뷰로 보여주는 메서드 : asType
    - 객체의 값을 기본 타입 값으로 반환하는 메서드 : typeValue
    - 정적팩터리 메서드 : from, of, valueOf...
  - 철자 규칙보다 덜중요하다 

<br><br><br><br>

# 10장 예외
# item 69 예외는 진짜 예외 상황에만 사용하라 
~~~ java 
try {
    int i = 0;
    while (true)
        range[i++].climb()
} catch (ArrayIndexOutOfBoundsException e) {
}
~~~ 
- 어이없는 예시,,ㅎ
  - JVM은 배열에 접근할 떄마다 경계를 넘는지 검사를 한다. 반복문에서 경계 검사를 하면 같은 일이 중복된다고 생각했을 수 있다.
  - 틀린 이유 세가지 
    1. 예외는 예외 상황에 쓸 용도로 설계되어 있으므로 JVM구현자 입장에서는 명확한 검사만큼 빠르게 만들어야 할 동기가 약하다.
    2. 코드를 try-catch안에 넣으면 JVM이 적용할 수 있는 최적화가 제한된다.
    3. 배열을 순회하는 표준 관용구는 앞서 걱정한 중복 검사를 수행하지 않는다. JVM이 알아서 최적화해 없애준다, 

  - 예외를 사용한 쪽이 표준 관용구보다 훨씬 느리다. 
  - 제대로 동작하지 않을 수 있다. 반복문 안에 버그 있으면 디버깅이 어려울 것 
<br>

- 예외는 오직 예외 상황에서만 써야한다. 절대로 일상적인 제어 흐름용으로 쓰여선 안된다.
  - 표준적이고 수비게 이해되는 관용구를 사용하라. 
  - 실제로 성능이 좋아지더라도, 자바 플랫폼이 꾸준히 개선되고 있으니 최적화로 얻은 상대적인 성능 우위가 오래가지 않을 수 있다.
  - 과하게 영리한 기법에 숨겨진 버그의 유지보수가 더 어렵다.
<br>

- 잘 설계된 API라면 클라이언트가 정상적인 제어 흐름에서 예외를 사용할 일이 없게 해야 한다. 
  - 특정 상태에서만 호출할 수 있는 '상태 의존적'메서드를 제공하는 클래스는 '상태 검사'메서드도 함께 제공해야한다.
  - ex) Iterator, next, hasNext

~~~ java 
List<Car> cars = new ArrayList<>();
for (Iterator<Car> i = cars.iterator(); i.hasNext();) {
   ... 
}

//Iterator가 hasNext제공 안했다면....
//컬렉션을 이런식으로 순회하지 말것!
List<Car> cars = new ArrayList<>();
try {
    Iterator<Car> i = cars.iterator();
    while (true) {
        Car car = i.next();
    }
    
} catch (NoSuchElementException e) {
}
~~~
  - 아래의 코드는 헷갈리며 속도도 느리고, 엉뚱한 곳에서 발생한 버그를 숨기기도 한다. 
  - 상태검사 메서드 대신 -> 올바른 상태가 아닐 떄 Optional, null을 반환하는 방법도 있다.
  - 상태검사 메서드/ 옵셔널/ 특정 값 중 하나를 선택하는 지침
    1. 외부 동기화 없이 여러 스레드가 동시에 접근할 수 있거나, 외부 요인으로 상태가 변할 수 없다면 **옵셔널이나 특정 값 사용**
    2. 성능이 중요한 상황에서 상태 검사 메서드가 상태 의존적 메서드의 작업 일부를 중복 수행한다면 **옵셔널이나 특정 값 사용 **
    3. 다른 모든 경우에는 상태 검사 메서드가 조금 더 낫다. 가동성이 살짝 더 좋고, 잘못 사용 시 발견 쉬움. 상태검사 메서드 호출을 깜빡했다면 상태 의존적 메서드가 예외를 던져 버그를 확실히 드러냄 

- 예외는 예외 상황에서 쓸 의도로 설계되었다. 정상적은 제어 흐름에서 사용해서는 안되며, 이를 프로그래머에게 강요하는 API도 만들어서는 안된다. 
<br><br><br><br>


# item 70 복구할 수 있는 상황에서는 검사 예외를, 프로그래밍 오류에는 런타임 예외를 사용하라. 
![image](https://user-images.githubusercontent.com/52403454/226372991-40051eae-68d9-469f-9d50-72115b5696e0.png)
<br>

자바에서 문제 상황을 알리는 타입(throwable)
  - 검사 예외 (Checked Exception) : 호출하는 쪽에서 복구하리라 여겨지는 상황이라면 검사 예외를 사용 
    - 검사 예외를 사용하면 그 예외를 catch로 잡아 처리하거나 더 바깥쪽으로 전파하도록 강제하게 된다. 
    - API설계자는 API사용자에게 검사 예외를 던져주어 그 상황에서 회복해내라고 요구. 
  - 비검사 예외 : 복구가 불가능하거나 더 실행해봐야 득보다는 실이 많은 경우. 적절한 오류 메시지를 뱉으며 중단.
    - 런타임 예외 (Unchecked Exception) : 대부분은 전제 조건을 만족하지 못했을 때 바생 
      - 프로그래밍 오류를 나타낼 때 사용 
      - 클라이언트가 API명세에 기록된 제약을 지키지 못했다는 뜻. 
      - ex) ArrayIndexOutOfBoundException -> 배열의 인덱스는 0부터 '배열의 크기 -1'사이어야 한다는 전제조건 지켜지지 않았을 때 
      - 문제점 : 복구할 수 있는 상황인지, 프로그래밍 오류인지가 명확히 구분되지 않는다. 자원 고갈은 말도 안되는 크기의 배열을 할당해 생긴 프로그래밍 오류일 수도 있고 자원이 부족해서 발생한 문제일 수 도 잇다. 
        -  API설계자의 판단 -> 확신하기 어렵다면 아마 비검사 예외 
    - 에러 : JVM의 자원 부족, 불변식 깨짐 등 더 이상 수행이 불가능 할 때 사용 
      - error 클래스를 상속해서 하위 클래스를 만드는 일은 자제. 직저 구현하는  비검사 throwable은 모두 RuntimeException의 하위 클래스여야 한다. error는 throw로 던지지도 말아야한다.

- Exception, RuntimeException, Error를 상속하지 않는 throwable을 만들 수도 있다. 
  - java 언어 명세에서 직접 이런 throwable을 다루지는 않지만 암묵적으로 일반적인 검사 예외처럼 다룬다
  - 그렇다면 언제 사용? -> 절대로 사용하지 마라 ㅎ
<br>

- exception은 어떤 메서드라도 정의할 수 있는 완벽한 객체 
  - exception없었다면 예외를 일으킨 사오항에 대한 정보를 코드로 전달하게 되고, 오류 메세지를 파싱해야하는데 정말 나쁜 습관(item12)
  - throwable클래스는 대부분 오류 메시지 포맷을 상세히 기술하지 않는데, jvm이나 릴리즈에 따라 포맷이 달라질 수 있다는 뜻이다. -> 직접 문자열 파싱해서 얻은 코드는 깨지기 쉽고, 다른 환경에서 작동안할 수 있다.
  <br>
  
- checked exception은 복구할 수 있는 조건일 때 발생.
  - 예외 상황을 벗어나는 데 필요한 정보를 알려주는 메서드를 함께 제공하는 것이 중요 
  - ex) 쇼핑몰에서 물건을 구입하려는데 카드 잔고가 부족하여 검사 예외 발생. -> 잔고가 얼마나 부족한지를 알려주는 접근자 메서드 제공.  

<br><br><br><br>

# item 70 필요없는 검사 예외 사용은 피하라
- checked예외 장점
  - checked 예외는 발생한 문제를 프로그래머가 처리하여 안정성을 높이게끔 해준다
- checked예외 단점
  - 하지만, checked 예외가 발생하는 메서드를 호출 하는 곳에서는 catch하여 처리하고나, 전파를 해야하기 때문에 API에 부담 
  - checked 예외는 스트림 안에서 직접 사용 불가능하기 때문에 자바 8부터 부담이 더 커짐 
<br>

- checked 예외를 사용해도 좋은 경우 -> 이외에는 unchecked를 사용하자
  - API를 사용해도 발생할 수 있는 예외
  - 프로그래머가 의미있는 조치를 취할 수 있는 경우 
<br>

- checked예외가 프로그래머에게 지우는 부담
  - 다른 checked 예외가 존재하면 catch문 하나 추가하지만, checked예외가 하나 뿐이라면 그 예외 때문에 try블록을 추가하고 스트림에서 사용 못한다. 
<br>

- checked예외를 회피하는 방법
  - 적절한 결과 타입을 담은 옵셔널 반환. -> 예외가 발생한 이유를 알려주는 부가 정보를 담을 수 없다. 
  - checked 예외를 던지는 메서드를 2개로 쪼개 비검사 예외로 바꾸기 

~~~ java 
//before
try {
    Obj.action(args);    
} catch (TheCheckedException e) {
    //do something
}

//after 
if (obj.actionPermitted(args)) {
    obj.action(args);
} else {
    //do something
}
~~~
- actionPermitted -> 상태검사 메서드 
- 외부 동기화 없이 여러 스레드가 동시에 접근할 수 있거나, 외부 요인에 의해 상태 변할 수 있다면 이 리팩토링은 적절하지 않다. 
  - actionPermitted와 action호출 사이에 객체의 상태가 변할 수 있기 때문 
  - actionPermitted와 action의 작업 일부를 중복 수행해도 적절하지 않음 

<br><br><br><br>

# item 72 표준 예외를 사용하라
- 표준 예외 사용시 장점
  - 다른 사람이 API를 사용하기 쉬워진다.
  - 예외 클래스 수가 적을 수록 메모리 사용량도 줄고 클래스를 적재하는 시간도 적게 걸린다.
<br>

- 가장 많이 사용되는 표준 예외 
  - IllegalArgumentException : 호출자가 인수로 부적절한 값을 넘길 때 던지는 예외 
  - IllegalStatteExcpetion : 대상 객체의 상태가 호출된 메서드를 수행하기에 적절하지 않을 때. ex) 초기화가 제대로 안된 객체를 사용하려고 할 때 
- 메서드가 던지는 모든 예외를 잘못된 인수나 상태라고 뭉뚱그릴 수 있겠지만 ( IllegalArgumentException,IllegalStatteExcpetion) 그 중 특수한 일부는 따로 구분해 쓴다
  - null값 허용하지 않은 메서드에는 NullPointException 
  - 시퀀스 허용 범위를 넘는 값을 건낼 때 IndexOutOfBoundsException
  - ConcourrentModificationException: 단일 스레드에서 사용하려고 설계한 객체를 여러 스레드가 동시에 수행하려고 할 때 
  - UnsupportedOperationException : 클라이언트가 요청한 동작을 대상 객체가 지원하지 않을 때 
<br>

- Exception, RuntimException, Throwable, Error는 직접 재사용하지 말자. 추상클래스라고 생각할 것 
- 상황에 부합하는 예외가 있다면 항상 표준 예외를 사용하자 
- 더 많은 정보를 주길 우너한다면 표준 예외를 확장해서 사용하자. 
- 예외는 직렬화 가능한 것 만으로도 새로 만들지 않아야 하는 이유가 된다. (12장)
- 인수값이 무엇이든 어차피 실패했을 거라면 IllegalStateException을, 그렇지 않으면 IllegalArgumentException을 던지자 
<br><br><br><br>


# item 73 추상화 수준에 맞는 예외를 던지라
- 수행하려는 일과 상관 없는 예외가 튀어나오면 당황하고, 다음 릴리즈 버전에서 구현 방식을 바꾸면 다른 예외가 튀어나와 기존 프로그램이 깨진다
  - 예외 번역으로 해결 
- 예외 번역 (exception translation) 상위 계층에서는 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외르 던져야 한다. 
~~~ java
try {
   ... // 저수준 추상화를 이용한다.
} catch (LowerLevelException e) {
   // 추상화 수준에 맞게 번역한다.
   throw new HigherLevelException(...);
}
~~~

- AbstractSequentialList 예제 
- <img width="676" alt="image" src="https://user-images.githubusercontent.com/52403454/226875212-15a939be-577b-4ded-8c12-7d3634103a13.png">
- AbstractList
- <img width="422" alt="image" src="https://user-images.githubusercontent.com/52403454/226912637-56bfca1c-d0ab-44a7-ab2c-b97502940814.png">

<br>

- 예외를 번역할 때 저수준 예외가 디버깅에 도움이 된다면 예외 연쇄를 사용
- 예외 연쇄 (excpiton chaining) : 근본 원인인 저수준 예외를 고수준 예외로 실어 보내는 방식 
  - 별도의 접근자 메서드 통해 저수준 예외를 꺼내볼 수 있다. 
~~~ java
// 예외 연쇄 
try {
   ... // 저수준 추상화를 이용한다.
} catch (LowerLevelException cause) {
   // 저수준 예외를 고수준 예외에 실어 보낸다.
   throw new HigherLevelException(cause);
}

// 예외 연쇄용 생성자
// 고수준 예외의 생성자는 상위 클래스의 생성자에 이 '원인'을 건네주어, 최종적으로 Throwable(Throwable) 생성자까지 건네지게 한다.
class HigherLevelException extends Exception {
   HigherLevelException(Throwable cause) {
      super(cause);
   }
}
~~~
- 대부분의 표준 예외는 예외 연쇄용 생성자를 가지고 있음 
- 무턱대고 예외를 전파하는 것보다야 예외 번역이 우수한 방법이지만, 남용은 곤란하다. 
- 가능하다면 저수준 메서드가 반드시 성공하도록 하여 아래 계층에서는 예외가 발생하지 않도록 하는 것이 좋다. 
- 차선책 : 아래 계층에서 예외를 피할 수 없다면 상위 계층에서 그 예외를 조용히 처리하여 문제를 API호출자에게 전파하지 않는 방법이 있다 -> 로깅 기능해서 처리할 것 
