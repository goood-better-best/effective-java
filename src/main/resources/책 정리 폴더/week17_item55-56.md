# 아이템 55. 옵셔널 반환은 신중히 하라
- Optional<T>는 null이 아닌 T타입 참조를하나 담거나, 혹은 아무것도 담지 않을 수 있음.
- Optional<T>가 Collection<T>를 구현하지는 않았지만, 옵셔널은 원소를 최대 1개 가질 수 있는 '불변' 컬렉션임.
    - 보통은 T를 반환해야 하지만 특정 조건에서는 아무것도 반환하지 않아야 할 때 T 대신 Optional<T>를 반환하도록 선언하면 됨.
    - 옵셔널을 반환하는 메서드는 예외를 던지는 몌서드보다 유연하고 사용하기 쉬우며, null을 반환하는 메서드보다 오류 가능성이 작음.

- 컬렉션에서 최댓값을 구함 (컬렉션이 비었으면 예외를 던짐)

    ```java
    public static <E extends Comparable<E>> E max(Collection<E> c) { 
    	if (c.isEmpty())
    		throw new IllegalArgumentException("빈 컬렉션") ;
    
    	E result = null; 
    	for (E e : c)
    		if (result == null || e.compareTo(result) > 0) 
    			result = Objects.requireNonNull(e);
    
    	return result;
    }
    ```

    - 이 메서드에 빈 컬렉션을 건네면 IllegalArgumentException을 던짐.
- 컬렉션에서 최댓값을 구해 Optional<E>로 반환

    ```java
    public static <E extends Comparable<E>> optional<E> max(Collection<E> c) {
    	if (c.isEmpty())
    		return Optional.enipty();
    
    	E result = null; 
    	for (E e : c)
    		if (result == null || e.conipareTo(result) > 0) 
    			result = Objects.requireNonNull(e);
    
    	return Optional.of(result);
    }
    ```

    - **빈 옵셔널은 Optional.empty()로 만들고, 값이 든 옵셔널은 Optional.of(value)로 생성함.**
        - Optional.of(value)에 null을 넣으면 NullPointerException을 던짐.
        - null값도 허용하는 옵셔널을 만들려면 Optional.ofNullable(value)를 사용하먼 됨.
        - **옵셔널을 반환하는 메서드에서는 절대 null을 반환하지 말아야 함.**
            - 옵셔널을 도입한 취지를 완전히 무시하는 행위임.
- 스트림의 종단 연산 중 상당수가 옵셔널을 반환함.
    - 컬렉션에서 최댓값을 구해 Optional로 반환함. - 스트림버전

        ```java
        public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        	return c.stream().max(Comparator.naturalOrder()); 
        }
        ```

- 메서드가 옵셔널을 반환한다면 클라이언트는 값을 받지 못했을 때 취할 행동을 선택해야 함.
    - 옵셔널 활용1 - 기본값을 정해둘 수 있음.

        ```java
        String lastWordlnLexicon = max(words).orElse("단어 없음");
        ```

    - 읍셔널 활용2 - 원하는 예외를 던질 수 있음.

        ```java
        Toy myToy = max(toys).orElseThrow(TemperTantrumException::new);
        ```

        - 실제 예외가 아니라 예외 팩터리를 건넨 건데 이렇게 하면 예외가 실제로 발생하지 않는 한 예외 생성 비용은 들지 않음.
    - 옵셔널 활용3 - 항상 값이 채워져 있다고 가정

        ```java
        Element lastNobleGas = max(Elements.NOBLE_GASES).get();
        ```

        - 옵셔널에 항상 값이 채워져 있다고 확신한다면 그냥 곧바로 값을 꺼내 사용함.
        - 잘못 판단한 것이라면 NoSuchElementException이 발생함.

- isPresent 메서드는 안전 밸브 역할의 메서드로, 옵셔널이 채워져 있으면 true를, 비어 있으면 false를
  반환함.
    - 실제로 isPresent를 쓴 코드 중 상당수는 앞서 언급한 메서드들로 대체할 수 있으며, 그렇게하면 더 짧고 명확하고 용법에 맞는 코드가 됨.
    - 예제) 부모 프로세스의 프로세스ID를 출력하거나, 부모가 없다면 ''N/A"를 출력하는 코드.

        ```java
        Optional<ProcessHandle> parentProcess = ph.parent(); 
        System.out.println("부모 PID: " + (parentProcess.isPresent() ?
        	String.valueOf(parentProcess.get().pid()) : "N/A"));
        
        ⬇︎ Optional의 map을 사용
        
        System.out.println("부모PID: " +
        	ph.parent().map(h -> String.valueOf(h.pid())).orElse("N/A"));
        ```

- 스트림을 사용한다면 옵셔널들을 Stream<Optional<T>>로 받아서, 그중 채워진 옵셔널들에서 값을 뽑아 Stream<T>에 건네 담아 처리하는 경우가 드물지 않음.

    ```java
    streamOfOptionals
    	.filter(OptionaL::isPresent) 
    	.map(Optional::get)
    ```

    - 옵셔널에 값이 있다면(Optional::isPresent) 그 값을 꺼내 (Optional::get) 스트림에 매핑함.
- 자바 9에서는 Optional에 stream() 메서드가 추가됨.
    - 옵셔널에 값이 있으면 그 값을 원소로 담은 스트림으로, 값이 없다면 빈스트림으로 변환함.
    - 이를 Stream의 flatMap 메서드와 조합하면 위의 코드를 다음처럼 명료하게 바꿀 수 있음.

        ```java
        streamOfOptionals
        	.flatMap(Optional::stream)
        ```

- 반환값으로 옵셔널을 사용한다고 해서 무조건 득이 되는 건 아님.
    - 컬렉션, 스트림, 배열, 옵셔널같은 컨테이너 타입은 옵셔널로 감싸면 안됨.
    - 빈 Optional<List<T>>를 반환하기보다는 빈 List<T>를 반환하는 게 좋음.
- **결과가 없을 수 있으며, 클라이언트가 이 상황을 특별하게 처리해야 한다면 Optional<T>를 반환함.**
    - 그래서 성능이 중요한 상황에서는 옵셔널이 맞지 않을 수 있음.
- 박싱된 기본 타입을 담는 옵셔널은 값을 두 겹이나감싸기 때문에 기본 타입 자쳬보다 무거울 수밖에 없음.
    - Optionallnt, OptionalLong, OptionaLDouble 사용
- 옵셔널을 맵으로 사용하면 절대 안됨.
    - 키 자체가 없는 경우나 키는 있지만 그 키가 속이 빈 옵셔널인 경우에 쓸데없이 복잡성만 높여서 혼란과 오류 가능성을 키움.

### 정리

- 값을 반환하지 못할 가능성이 있고, 호출할 때마다 반환값이 없을 가능성을 염두에 둬야 하는 메서드라면 옵셔널을 반환해야 할 상황일 수 있음.
- 하지만 옵셔널 반환에는 성능저하가 뒤따르니, 성능에 민감한 메서드라면 null을 반환하거나 예외를 던지는 편이 나을 수 있음.
- 옵셔널을 반환값 이외의 용도로 쓰는 경우는 매우 드문 일.





# 아이템 56. 공개된 API 요소에는 항상 문서화 주석을 작성하라
- 자바독은 소스코드 파일에서 문서화 주석(doc comment, 자바독 주석)이라는 특수한 형태로 기술된 설명을 추려 API 문서로 변환해줌.
- **API를 올바로 문서화하려면 공개된 모든 클래스, 인터페이스, 몌서드, 필드 선언에 문서화 주석을 달아야 함.**
    - 직렬화할 수 있는클래스라면 직렬화 형태에 관해서도 적어야 함.
    - 기본 생성자에는 문서화 주석을 달 방법이 없으니 공개 클래스는 절대 기본 생성자를 사용하면 안됨.

- **메서드용 문서화 주석에는 해당 메서드와 클라이언트 사이의 규약을 명료하게 기술해야 함.**
- 상속용으로 설계된 클래스의 메서드가 아니라면 그 메서드가 어떻게 동작하는지가 아니라 무엇을 하는지를 기술해야 함.
    - 즉, how가 아닌 what을 기술.
- 문서화 주석에는 클라이언트가 해당 메서드를 호출하기 위한 전제조건(precondition)을 모두 나열해야 함.
    - 또한 메서드가 성공적으로 수행된 후에 만족해야 하는 사후조건(postcondition)도 모두 나열해야 함.
- 전제조건과 사후조건뿐만 아니라 부작용도 문서화해야 함.
    - 부작용이란 사후조건으로 명확히 나타나지는 않지만 시스템의 상태에 어떠한 변화를 가져오는 것을 뜻함.
- 메서드의 계약을 기술하려면 모든 매개변수에 @param 태그를, 반환 타입이 void가 아니라면 @return 태그를, 발생할 가능성이 있는 모든 예외에 @throws 태그를 달아야 함.
- 관례상 @param태그와 @return 태그의 설명은 해당 매개변수가 뜻하는 값이나 반환값을 설명하는 명사구를 씀.
- @throw 태그의 설명은 if로 시작해 해당 예외를 던지는 조건을 설명하는 절이 뒤따름.
- 문서화 주석의 예

    ```java
    /**
    * Returns the element at the specified position in this list. 
    *
    * <p>This method is <i>not</i> guaranteed to run in constant
    * time. In some implementations it may run in time proportional
    * to the element position.
    *
    * @param  index index of element to return; must be
    *         non-negative and less than the size of this list
    * @return the element at the specified position in this list
    * @throws IndexOutOfBoundsException if the index is out of range 
    *				  ({@code index < 0 || index >= this.size()})
    */
    E get(int index);
    
    ⬇︎ 번역
    
    /**
    * 이 리스트에서 지정한 위치의 원소를 반환한다， 
    *
    * <p>이 메서드는 상수 시간에 수행됨을 보장하지 <i>않는다</i>. 구현에 따라
    * 원소의 위치에 비례해 시간이 걸릴 수도 있다， 
    *
    * @param  index 반환할원소의인덱스; 0 이상이고 리스트 크기보다 작아야 한다.
    * @return 이 리스트에서 지정한 위치의 원소
    * @throws IndexOutOfBoundsException index가 범위를벗어나면,
    *         즉, ({@code index < 0 || index >= this.size()})이면 발생한다.
    */
    E get(int index);
    ```

    - HTML 태그(p 태그와 i태그)
        - 자바독 유틸리티는 문서화 주석을 HTML로 변환하므로 문서화 주석 안의 HTML 요소들이 최종
          HTML문서에 반영됨.
    - @throws 절에 사용한 {@code} 태그.
        - 첫 번째, 태그로 감싼 내용을 코드용 폰트로 렌더링함.
        - 두 번째, 태그로 감싼 내용에 포함된 HTML 요소나 다른 자바독 태그를 무시함.
            - HTML메타문자인 < 기호 등을 별다른 처리 없이 바로 사용할 수 있음.
            - 문서화 주석에 여러 줄로 된 코드 예시를 넣으려면 {@code} 태그를 다시 <pre> 태그로
              감싸면 됨.
                - <pre>{@code … 코드 ... }</pre> 형태로 쓰면 됨.

- 클래스를 상속용으로 설계할 때는 **자기사용 패턴(self-usc pattern)**에 대해서도 문서에 남겨 다른 프로그래머에게 그 메서드를 올바로 재정의하는 방법을 알려줘야 함.
    - 자기사용 패턴은 자바 8에 추가된 @implSpec 태그로 문서화함.
    - 해당 메서드와 하위 클래스 사이의 계약을 설명하여, 하위 클래스들이 그 메서드를 상속하거나 super 키워드를 이용해 호출할 때 그 메서드가 어떻게 동작하는지를 명확히 인지하고 사용하도록 해줘야 함.

    ```java
    /**
    * Returns true if this collection is empty. 
    *
    * @implSpec
    * This implementation returns {@code this.size() == 0}. 
    *
    * @return true if this collection is empty
    */
    public boolean isEmpty() { ... }
    
    ⬇︎ 번역
    
    /**
    * 이 컬렉션이 비었다면 true를 반환한다. 
    *
    * @implSpec
    * 이 구현은 {@codethis.size() == 0}의 결과를 반환한다. 
    *
    * @return 이 컬렉션이 비었다연 true, 그렇지 않으면 false
    */
    public boolean isEmpty() { ... }
    ```


- API 설명에 <, >, & 등의 HTML 메타문자를 포함시키기
    - {@literal} 태그로 감싸는 것으로 이 태그는 HTML 마크업이나 자바독 태그를 무시하게 해줌.

    ```java
    /*
    * A geometric series converges if {@literal |r| < 1}.
    */
    
    ⬇︎
    
    |r| < 1이면 기하 수열이 수렴한다.
    ```


- 각 문서화 주석의 첫 번째 문장은 해당 요소의 요약 설명(summary description)으로 간주됨.
- 요약 설명은 반드시 대상의 기능을 고유하게 기술 해야함.
    - 헷갈리지 않으려면 **한 클래스(혹은인터페이스) 안에서 요약 설명이 똑같은 멤버(혹은생정자)가 둘 이상이면 안됨.**
    - 다중정의된 메서드들의 설명은 같은 문장으로 시작하는 게 자연스럽겠지만 문서화 주석에서는 허용되지 않음.
- 요약 설명에서는 마침표(.)에 주의해야 함.
    - 예컨대 문서화 주석의 첫 문장이 "머스터드 대령이나 Mrs. 피콕 같은 용의자."라면 첫번째 마침표가 나오는 "머스터드 대령이나 Mrs."까지만 요약 설명이 됨.
    - 의도치 않은 마침표를 포함한 텍스트를 {@literal}로 감싸주면 됨.

        ```java
        /**
        * A suspect, such as Colonel Mustard or {@UteraL Mrs. Peacock}.
        */
        public class Suspect { ... }
        
        // **자바 10 이후** - {@summary}라는 요약 설명 전용 태그가 추가되어 깔끔하게 처리할 수 있음.
        /**
        * {@summary A suspect, such as Colonel Mustard or Mrs. Peacock.} 
        */
        public enum Suspect { ... }
        ```

- 메서드와 생성자의 요약 설명은 해당 메서드와 생성자의 동작을 설명하는(주어가 없는) **동사구**여야 함.

    ```java
    /**
    ArrayList(int initia1Capacity) : 지정한 초기 용량을 갖는 빈 리스트를 생성한다.
    Collection.size() : 이 컬렉션 안의 원소 개수를 반환한다.
    */
    ```

- 클래스, 인터페이스, 필드의 요약 설명은 대상을 설명하는 **명사절**이어야 함.

    ```java
    /**
    Instant : 타임라인상의 특정 순간(지점) 
    Math.PI: 원주율(pi)에 가장 가까운 double 값
    */
    ```


- 자바 9부터는 자바독이 생성한 HTML 문서에 검색(색인)기능이 추가됨.
    - 클래스, 메서드, 필드 같은 API 요소의 색인은 자동으로 만들어지며, 원한다면 {@index} 태그를 사용해  API에서 중요한 용어를 추가로 색인화할 수 있음.
    - 단순히 색인으로 만들 용어를 태그로 감싸면 됨.

        ```java
        // This method complies with the {@index IEEE 754} standarth
        ```


- **제네릭 타입이나 제네릭 메서드를 문서화할 때는 모든 타입 매개변수에 주석을 달아야함.**

    ```java
    /**
    * An object that maps keys to values. A map cannot contain
    * duplicate keys; each key can map to at most one value. 
    *
    * (Remainder omitted) 
    *
    * @param<K> the type of keys maintained by this map 
    * @param <V> the type of mapped values
    */
    public interface Map<K, '/> { ... } 
    
    ⬇︎ 번역
    
    /**
    * 키와 값을 매핑하는 객체. 맵은 키를 중복해서 가질 수 없다.
    * 즉, 키 하나가 가리킬 수 있는 값은 최대 1개다. 
    *
    * (나머지 설명은 생략) 
    *
    * @param<K> 이 맵이 관리하는 키의 타입 
    * @param<V> 매핑된 값의 타입
    */
    public interface Map<K, '/> { ... }
    ```


- 열거 타입을 문서화할 때는 상수들에도 주석을 달아야 함.

    ```java
    /**
    * An instrument section of a symphony orchestra.
    */
    public enum OrchestraSection {
    	/** Woodwinds, such as flute, clarinet, and oboe. */ 
    	WOODWIND,
    
    	/** Brass instruments, such as french horn and trumpet. */ 
    	BRASS,
    	
    	/** Percussion instruments, such as timpani and cymbals. */ 
    	PERCUSSION,
    	
    	/** Stringed instruments7 such as violin and cello. */ 
    	STRING;
    } 
    ```


- 애너테이션 타입을 문서화할 때는 멤버들에도 모두 주석을 달아야 함.
    - 애너테이션 타입의 요약 설명은 프로그램 요소에 이 애너테이션을 단다는 것이 어떤 의미인지를 설명하는 동사구로 함.

    ```java
    /**
    * Indicates that the annotated method is a test method that 
    * must throw the designated exception to pass.
    */
    @Retention(RetentionPolicy.RUNTIME) 
    @Target (Elementlype.METHOD)
    public @interface ExceptionTest {
    	/**
    	* The exception that the annotated test method must throw 
    	* in order to pass. (The test is permitted to throw any
    	* subtype of the type described by this class object.)
    	*/
    Class<? extends Throwable> value();
    ```

- 패키지를 설명하는 문서화 주석은 package-info.java 파일에 작성함.
    - 이 파일은 패키지 선언을 반드시 포함해야 하며 패키지 선언 관련 애너테이션을 추가로 포함할 수도 있음.
- 모듈 시스템을 사용한다면 모듈 관련 설명은 module-info.java 파일에 작성하면 됨.
- API 문서화에서 자주 누락되는 설명
    - 스레드 안전성과 직렬화 가능성.
    - **클래스 혹은 정적 메서드가 스레드 안전하든 그렇지않든, 스레드 안전 수준을 반드시 API 설명에 포함해야함.**

### 정리

- 문서화 주석은 API를 문서화하는 가장 훌륭하고 효과직인 방법.
- 공개API라면 빠짐없이 설명을 달아야함.
- 표준규약을 일관되게 지키자.
- 문서화 주석에 임의의 HTML 태그를 사용할 수 있음.


