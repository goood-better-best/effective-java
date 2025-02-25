## 아이템 40. @Override 애너테이션을 일관되게 사용하라

- @Override는 메서드 선언에만 달 수 있으며, 이 애너테이션이 달렸다는 것은 상위 타입의 메서드를 재정의했음을 뜻함.
- 이 애너테이션을 일관되게 사용하면 여러 가지 악명 높은 버그들을 예방해줌.
- **상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달아야 함.**
- @Override 애너테이션을 안 달아도 되는 한 가지 예외사항
    - 구체 클래스에서 상위 클래스의 추상 메서드를 재정의할 때는 굳이 @Override를 달지 않아도 됨.
        - 구체 클래스인데 아직 구현하지 않은 추상 메서드가 남아 있다면 컴파일러가 그 사실을 바로 알려주기 때문.
- @Override는 클래스뿐 아니라 인터페이스의 메서드를 재정의할 때도 사용할 수 있음.
- 상위 클래스가 구체 클래스든 추상 클래스든 마찬가지로 추상 클래스나 인터페이스에서는 상위 클래스나 상위 인터페이스의 메서드를 재정의하는 모든 메서드에 @Override를 다는 것이 좋음.

## 아이템 41. 정의하려는 것이 타입이라면 마커 인터페이스를 사용하라.

- 마커 인터페이스 (marker interface)
    - 아무 메서드도 담고 있지 않고, 단지 자신을 구현하는 클래스가 특정 속성을 가짐을 표시해주는 인터페이스.
    - 여러 인터페이스를 상속받으며 본인의 기능은 없고 여러 인터페이스를 묶어주는 역할만 하는 인터페이스.
- 마커 애너테이션 (marker annotation)
    - 요소가 하나도 정의되지 않은 애너테이션
    - ex) @Test, @Deprecated, @Override
- 마커 인터페이스의 장점
    
    ① 마커 인터페이스는 이를 구현한 클래스의 인스턴스들을 구분하는 타입으로 쓸 수 있으나, 마커 애너테이션은 그렇지 않음.
    
    - 마커 인터페이스는 어엿한 타입이기 때문에 마커 애너테이션을 사용했다면 런타임에야 발견될 오류를 컴파일타임에 잡을 수 있음.
    
    ② 적용 대상을 보다 더 정밀하게 지정 할 수 있음.
    
    - 적용 대상(@Target)을 ElementType.TYPE으로 선언한 애너테이션은 모든 타입(클래스, 인터페이스, 열거 타입, 애너테이션)에 달 수 있음.
    - 하지만 마커 인터페이스로 정의했다면 그냥 마킹하고 싶은 클래스에서만 그 인터페이스를 구현(인터페이스라면 확장)하면 됨.
        - 그러면 마킹된 타입은 자동으로 그 인터페이스의 하위 타입임이 보장됨.
- 마커 애너테이션의 장점
    - 거대한 애너테이션 시스템의 지원을 받음.
        - 애너테이션을 적극 활용하는 프레임워크에서는 마커 애너테이션을 쓰는 쪽이 일관성을 지키는 데 유리함.
- 마커 인터페이스를 사용해야 하는 경우
    - 클래스와 인터페이스 외의 프로그램 요소 (모듈, 패키지, 필드, 지역변수 등)에 마킹해야할 때 애너테이션을 쓸 수밖에 없음.
        - 클래스와 인터페이스만이 인터페이스를 구현하거나 확장할 수 있기 때문.

## 아이템 42. 익명 클래스보다는 람다를 사용하라

- 문자열을 길이순으로 정렬
    
    ```java
    Collection.sort(words, new Comparator<String>() {
      public int compare(String s1, String s2){
        return Integer.compare(s1.length(), s2.length());
      }
    });
    ```
    
    - 정렬을 담당하는 추상 전략은 Comparator 인터페이스가 구현.
    - 문자열을 정렬하는 구체적인 전략은 익명 클래스가 구현.
- 문자열 길이순으로 정렬 - 람다식으로 구현
    
    ```java
    Collection.sort(words, (s1,s2) -> Integer.compare(s1.length(), s2.length()));
    
    ⬇︎
    // 람다 자리에 비교자 생성 메서드를 사용하여 더 간결하게 표현
    Collection.sort(words, comparingInt(String::length));
    
    ⬇︎
    // List인터페이스의 sort를 이용하여 더 간결하게 표현
    words.sort(comparingInt(String::length));
    ```
    
    - 컴파일러가 문맥을 살펴 타입을 추론하기 때문에 매개변수(s1, s2), 반환값의 타입은 명시하지 않아도 됨.
        - 타입을 명시해야 코드가 더 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하는 것이 좋음.

- 람다와 익명 클래스의 특징
    - 람다는 이름도 없고 메서드나 클래스와 다르게 문서화도 할 수 없으므로 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 함.
    - 람다는 함수형 인터페이스에서만 쓰임.
    - 람다는 자신을 참조할 수 없음.
        - 람다에서의 this 키워드는 바깥 인스턴스를 가리킴.
        - 반면 익명 클래스에서의 this는 익명 클래스의 인스턴스 자신을 가리킴.
            - 함수 객체가 자신을 참조해야 한다면 반드시 익명 클래스를 써야함.
    - 람다도 익명 클래스와 같이 직렬화 형태가 구현별로 다를 수 있으므로 람다를 직렬화하는 일은 극히 삼가야 함.
    - 익명 클래스는 함수형 인터페이스가 아닌 타입의 인스턴스를 만들 때만 사용해야 함.

## 아이템 43. 람다보다는 메서드 참조를 사용하라

- 메서드 참조(method reference)는 람다보다 더 간결하게 만듦.
- 키가 맵 안에 없다면 키와 숫자 1을 매핑하고, 이미 있다면 기존 매핑 값을 증가시킴.
    
    ```java
    // 람다
    map.merge(key, 1, (count, incr) -> count + incr);
    
    ⬇︎
    // 메서드 참조를 사용하여 더 간결하게 표현
    map.merge(key, 1 Integer::sum);
    ```
    
    - 매개변수 수가 늘어날수록 메서드 참조로 제거할 수 있는 코드양도 늘어남.

- 람다와 메서드 참조의 특징
    - 람다로 할 수 없는 일이라면 메서드 참조로도 할 수 없음.
    - 메서드 참조에는 기능을 잘 드러내는 이름을 지어줄 수 있고 설명을 문서로 남길 수도 있음.
    - 주로 메서드와 람다가 같은 클래스에 있을 때는 메서드 참조보다 람다가 간결함.
        - GoshThisClassNameIsHumongous 클래스 안에 다음 코드가 있을 때 메서드로 대체하면 아래와 같음.
            
            ```java
            // 메서드 참조를 사용하여 구현
            service.execute(GoshThisClassNameIsHumongous::action);
            
            ⬇︎
            // 람다로 더 간결하게 표현
            service.execute(() -> action());
            ```
            
            - 메서드 참조 쪽은 더 짧지도, 더 명확하지도 않기 때문에 람다 쪽이 더 나음.

- 메서드 참조의 유형 5가지
    - 정적 메서드를 가리키는 메서드 참조
    - 인스턴스 메서드를 참조하는 유형
        - 수신 객체를 특정하는 한정적 인스턴스 메서드 참조
            - 한정적 참조는 근본적으로 정적 참조와 비슷하며, 함수 객체가 받는 인수와 참조되는 메서드가 받는 인수가 똑같음.
    - 수신 객체를 특정하지 않는 비한정적 인스턴스 메서드 참조
        - 이를 위해 수신 객체 전달용 매개변수가 매개변수 목록의 첫 번째로 추가되며, 그 뒤로는 참조되는 메서드 선언에 정의된 매개변수들이 뒤따름.
    - 클래스 생성자를 가리키는 메서드 참조
    - 배열 생성자를 가리키는 메서드 참조
