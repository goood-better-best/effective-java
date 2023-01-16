# 아이템 34 int 상수 대신 열거 타입을 사용하라
~~~ java 
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;

public static final int ORANGE_NAVEL = 0
public static final int ORANGE_TEMPLE = 1;
~~~
1. 정수 열거 패턴(int enum pattern)기법에는 단점이 많다. 
  - 자바에서 enum이 지원하기 전에 위첢 정수 상수를 한 묶음 선언해서 사용하곤 했다.
  - 타입 안전을 보장할 수 없으며 표현력도 좋지 않다. (예를들어 APPLE_FUJI를 사용해야할 곳에 ORANGE_NAVEL를 사용해도 둘 다 정수 0이기 때문에 컴파일 떄 문제가 없음)
  - 정수 열거 패턴을 사용한 프로그램은 깨지기 쉽다.
  - 컴파일 하면 클라이언트 파일에 그대로 새겨지기 때문에, 상수 값이 바뀌면 클라이언트도 반드시 재컴파일이 필요
3. 문자열 열거 패턴(string enum parttern)
  - 정수 대신 문자열 상수를 사용하는 변형 패턴
  - 상수 열거 패턴보다 더 나쁘다. 
  - 상수의 의미를 출려할 수 있으니 좋지만 경험이 부족한 프로그래머가 문자열 상수의 이름 대신 문자열 값을 그대로 하드코딩 하게 만들기 떄문

4. 열거 타입 (enum type)을 사용하라.
  - 자바의 열거 타입은 완전한 형태의 클래스라서 (단순한 정수값만 있는) 다른 언어의 열거 타입보다 훨씬 강력하다 
    - 다른 언어의 enum은 단순 값의 열거형으로만 가짐
    - 자바의 enum은 class로서 작동하여 다양한 동작 가능 (ex.메소드 작성 가능, 필드 추가 가능)
    - https://weyprecht.de/2019/10/16/enums-in-csharp-and-java/
  - 열거 타입 자체는 class
  - 상수 하나당 자신의 인스턴스 하나씩 만들어 public static final 필드로 공개
  - 생성자를 제공하지 않으므로 사실상 final -> 클라이언트가 인스턴스를 직접 생성하거나 확장할 수 없으니 열거 타입 인스턴스는 하나만 존재 
    - 열거타입 인스턴스는 통제된다. 
    - 열거타입은 싱글턴을 일반화한 형태 
  - 컴파일 타임 타입 안정성을 제공한다. 
    - 메서드 매개변수에 Apple enum을 사용했다면,다른 값을 넘기려고 하면 컴파일 오류 
  - 열거타입에는 각각의 이름공간이 있어서 이름이 같은 상수도 평화롭게 공존
    - enum에 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일하지 않아도 된다.
    - https://honbabzone.com/java/java-enum/
    - <img width="656" alt="image" src="https://user-images.githubusercontent.com/52403454/211151538-730c82b9-c0de-4f62-b729-3c29217f47e6.png">

  - Object메서드들을 높은 품질로 구현해놨고, comparable, serializable을 구현해뒀음.


5. 열거 타입에 메서드나 필드 추가
  - 메서드 추가 가능. 고차원의 추상 개념 하나를 완벽히 표현해낼 수 있음 
  - 열거 타입 상수 각각을 통성 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.
  - 열거 타입은 근본적으로 불변이라 모든 필드는 final이어야 한다. -> 음....Final 불변 아닌디?-> 필드 타입을 Primitive밖에 못써서 그런듯?
  - 필드를 public으로 선언해도 되지면 private으로 두고 별도의 public 접근자 메서드를 두는게 낫다 -> enum은 class 타입이니까 일반 Class쓰듯이?
 
  ```java
  public enum Planet {
     MERCURY(3.302e+23, 2.439e6),
     VENUS (4.869e+24, 6.052e6),
     EARTH (5.975e+24, 6.378e6),

     // ...

     private final double mass;		// 질량(단위 : 킬로그램)
     private final double redius; 	// 반지름(단위: 미터)
     private final double surfaceGravity; 	// 표면중력(단위: m / s^2)

     // 중력상수(단위: m^3 / kg s^2)
     private static final double G = 6.67300E-11;

     // 생성자
     Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
     }

     public double mass() { return mass; }
     public double radius() { return radius; }
     public double surfaceGravity() { return surfaceGravity; }

     public double surfaceWeight(double mass) {
        return mass * surfaceGravity; // F = ma
     }
  }
  ```

  - 아래는 표면 중력 계산 메서드 -> enum은 강력해서 짧게 작성 가능하다. 
    - 개인적으로는 계산 식를 메서드로 빼서 enum에서 정의하는게 더 좋아보임 -> 예제라서?

  ```java
  public class WeightTable {
     public static void main(String[] args) {
        double earthWeight = Double.parseDouble(args[0]);
        double mass = earthWeight / Planet.EARTH.surfaceGravity();

        for (Planet p : Planet.value())
           System.out.printf("%s에서의 무게는 %f이다.%n", p, p.surfaceWeight(mass));
     }
  }
  ```

  - 상수 제거에도 효율적  
    - 제거 후 다시 컴파일하면 유용항 메시지를 담은 컴파일 오류가 발생 
  - 메서드
    - 열거 타입을 선언한 클래스 혹은 그 패키지에서만 유용한 기능은 private나 package-private 메서드로 구현한다. 
  
  - 아래의 코드는 예쁘지 않고 깨지기 쉬운 코드이다. -> 새로운 상수 추가하면 case문도 추가해야함
  ```java
public enum Operation {
   PLUS, MINUS, TIMES, DIVIDE;
   
   // 상수가 뜻하는 연산을 수행한다.
   public double apply(double x, double y) {
      switch(this) {
         case PLUS: return x + y;
         case MINUS: return x - y;
         case TIMES: return x * y;
         case DIVIDE: return x / y;
      }
      throw new AssertionError("알 수 없는 연산: " + this);
   }
}
```


  - 상수별 메서드 구현(constant-specific method implementation)
    - apply가 추상메서드이므로 구현하지 않으면 컴파일 오류가 나기 때문에 까먹지 않을 수 있음 
  ```java

public enum Operation {
   PLUS { public double apply(double x, double y) { return x+y; }},
   MINUS { public double apply(double x, double y) { return x-y;}},
   TIMES { public double apply(double x, double y) { return x*y;}},
   DIVIDE { public double appply(double x, double y) { return x/y;}};
   
   public abstract double apply(double x, double y);
}
  ```
 - 상수별 메서드 구현을 상수별 데이터와 결합도 가능
  ```java
 public enum Operation {
   PLUS("+") {
      public double apply(double x, double y) { return x+y; }
   },
   MINUS("-") {
      public double apply(double x, double y) { return x-y; }
   },
   TIMES("*") {
      public double apply(double x, double y) { return x*y; }
   },
   DIVIDE("/") {
      public double apply(double x, double y) { return x/y; }
   };
   
   private final String symbol;
   
   Operation(String symbol) { this.symbol = symbol; }
   
   @Override
   public String toString() { return symbol; }
   public abstract double apply(double x, double y);
   
} 
```
  - enum은 상수 이름을 입력받아 그 이름에 해당하는 상수를 반환해주는 valueOf(String)메서드가 자동 생성된다. 
    - 재정의 하려거든 fromString메서드도 함꼐 제공하는 것을 고려 

  - 상수별 메서드 구현 단점
    - 코드를 공유하기 어렵다. 
  - 그렇다면 각 상수별 메서드 어떻게 구현해야할까?
    - 전략 열거타입 
```java
     enum PayrollDay {
   MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY,
   SATURDAY(PayType.WEEKEND), SUNDAY(PayType.WEEKEND);
   
   private final PayType payType;
   
   PayrollDay(PayType payType) { this.payType = payType; }
   
   int pay(int minutesWorked, int payRate) {
      return payType.pay(minutesWorked, payRate);
   }
   
   // 전략 열거 타입
   enum PayType {
      WEEKDAY {
         int overtimePay(int minsWorked, int payRate) { 
            return minsWorked <= MINS_PER_SHIFT ? 0 :
               (minsWorked - MINS_PER_SHIFT) * payRate / 2;
         }
      },
      WEEKEND {
         int overtimePay(int minsWorked, int payRate) {
            return minsWorked * payRate / 2;
         }
      };
      
      abstract int overtimePay(int mins, int payRate);
      private static final int MINS_PER_SHIFT = 8 * 60;
      
      int pay(int minsWorked, int payRate) {
         int basePay = minsWorked * payRate;
         return basePay + overtimePay(minsWorked, payRate);
      }
   }
}
```
  - 하지만 기존 열거 타입에 상수별 동작을 혼합해 넣을 때는 switch문이 좋은 선택이 될 수 있다. 
```java
// 반대 연산 
public static Operation inverse(Operation op) {
   switch(op) {
      case PLUS: return Operation.MINUS;
      case MINUS: return Operation.PLUS;
      case TIMES: return Operation.DIVIDE;
      case DIVIDE: return Operation.TIMES;
      
      default: throw new AssertionError("알 수 없는 연산: " + op);
   }
}
```


6. 그렇다면 enum은 언제 써야할까?
  - 필요한 원소를 컴파일 타임에 다 알 수 있는 집합이라면 항상 열거 타입을 사용하자 
    - ex) 태양계 행성, 한 주일, 체스 말, 메뉴 아이템, 연산 코드, 명령줄 플래그 
    - https://jojoldu.tistory.com/137
  - 열거타입에 정의된 상수 개수가 영원히 고정불변일 필요 없다.  
    - 나중에 추가돼도 바이너리 수준에서 호환되도록 설계되어있다. 
  
  
  
# 아이템 35 ordinal 메서드 대신 인스턴스 필드를 사용하라
- 대부분의 열거 타입 상수는 자연스럽게 하나의 정숫값에 대응. 
- ordinal 메서드 제공 : 해당 상수가 그 열거타입에서 몇 번째 위치인지 반환한, 메서드
- 필요하다면 필드에 저장해서 사용하자. 
- 해당 메서드는 EnumSet과 EnumMap과 같이 열거 타입 기반의 범용 자료구조에 쓸 목적으로 설계.
- https://techblog.woowahan.com/2527/


# 아이템 36 비트 필드 대신 EnumSet을 사용하라 
- 열거 값들이 집합으로 사용될 경우, 예전에는 각 상수에 서로 다른 2의 거듭제곱 값을 할당한 정수 열거 패턴을 사용해왔다
~~~ java
public class Text {
   public static final int STYLE_BOLD      = 1 << 0; // 1
   public static final int STYLE_ITALIC    = 1 << 1; // 2
   public static final int STYLE_UNDERLINE = 1 << 2; // 4
   public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8
   
   // 매개변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR한 값이다.
   public void applyStyles(int styles) { ... }
}
~~~
- 위와 같이 비트별 or를 사용해 여러 상수를 하나의 잡합으로 모을 수 있었고, 이렇게 만든 집합을 비트 필드라고 한다. 
- 단순 정수 열거 상수를 출력할 때보다 어렵다. 
- 원소 순회하기도 까다롭다.
- 최대 비트수를 계산해야한다. 
- 따라서 EnumSet을 쓰자!
  - Set 인터페이스 완벽 구현, 타입 안전, 다른 Set과 사용 가능 
  - 내부는 비트 벡터로 구현되어 있어 원소가 64개 이하라면 EnumSet을 long 변수 하나로 표현하여 비트 필드에 비견되는 성능을 보여준다
  - removeAll, retainAll같은 대량 작업은 비트를 효율적으로 처리할 수 있는 산술 연산을 사용하도록 구현 
  ~~~ java 
  public class Text {
   public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
   
   // 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다.
   public void applyStyles(Set<Style> styles) { ... }
  }
  
  text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
  ~~~
