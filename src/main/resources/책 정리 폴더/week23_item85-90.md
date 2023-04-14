# item85. 자바 직렬화의 대안을 찾으라

- ObjectInputStream의 readObject 메소드를 호출하면서 역직렬화를 통한 보안 문제가 발생할 수 있다.
- readObject 메소드는 클래스패스 안의 거의 모든 타입의 객체를 만들어 낼 수 있는 생성자

```java
static byte[] bomb() {
    Set<Object> root = new HashSet<>();
    Set<Object> s1 = root;
    Set<Object> s2 = new HashSet<>();
    
    for (int i = 0; i < 100; i++) {
        Set<Object> t1 = new HashSet();
        Set<Object> t2 = new HashSet();
        t1.add("foo");
        s1.add(t1);
        s1.add(t2);
        s2.add(t1);
        s2.add(t2);
    }
    
    return serialize(root); // 간결하게 하기 위해 이 메소드의 코드는 생략
}
```

- 이 코드를 역직렬화 하기 위해선 영원히 걸린다.
- 추가로 스택도 터져버리고
- 이런 문제를 대처하기위해선
- 그냥 역직렬화를 하지 않는 것
- 우리가 쓰는 시스템에서 자바 직렬화를 쓸 이유는 전혀 없다

<hr />

- 대안으로 쓰이는 것이 크로스-플랫폼 구조화된 데이터 표현 (cross-platform structured-data representation)
- 장점으로는 훨씬 간단하다
- 임의 객체 그래프를 자동으로 직렬/역직렬화 하지 않는다.
- 대신 속성-값 쌍으로 간단히 구조화된 데이터 객체를 사용
- 보통 JSON을 씀

<hr />

- 레거시 시스템에는 아직 직렬화들이 남아있는데, 어쩔 수 없이 써야되면 역직렬화를 하지 않도록 하자
- 그리고 계속해서 직렬화를 쓰는곳이 있다면 JSON같은거로 바꾸도록 노력해야 한다

# item86. Serializable을 구현할지는 신중히 결정하라
- 특정 클래스의 인스턴스를 직렬화 하려면 Serializable을 구현하면 된다.
- 근데 한번 릴리즈하면 수정하기가 어렵다.
- Serializable을 구현하면 직렬화된 바이트 스트림 인코딩도 하나의 공개 API가 된다.
- 이제 이 클래스가 퍼지면 그 직렬화 형태도 영원히 지원해야 한다
- 즉 기본 직렬화 형태에선 private과 package-private들도 공개되는 꼴 (캡슐화가 깨짐)
- 이 이후에 클래스를 수정해버리면 원래의 직렬화상태와 달라지게 된다.
- 지저분해지고 관리가 어려워진다
- 전반적인 설명이 직렬화를 하면 안되는 클래스에 대해 설명하고 있다
- 상속용으로 설계된 클래스, 그리고 직렬화를 하면 테스트가 어려워 지는 등
- 결국은 쓰지 말라는 말

# item87. 커스텀 직렬화 형태를 고려해보라
- 기본 Serializable을 구현한 클래스는 추후에 변경되면 관리가 아주 어렵다
- 실제로 BigInteger도 같은 문제가 있다
- 객체의 물리적 표현과 논리적 내용이 같으면 기본 직렬화를 쓰긴 해도 된다

```java
import java.io.Serializable;

public class Name implements Serializable {
    private final String lastName;
    private final String firstName;
    private final String middleName;
    ...
}
```

- 일반적이로 이름은 이 3개니까 이렇게 사용해도 된다.
- 쓰기에 별로 안좋은 구조는 아래와 같음

```java
import java.io.Serializable;

public final class StringList implements Serializable {
    private int size = 0;
    private Entry head = null;
    
    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }
    
    ...
}
```

- 객체의 물리적 표현과 논리적 표현의 차이가 클 때 기본 직렬화 형태를 사용하면 네 가지 문제가 생긴다.

```java
1. 공개 API가 현재의 내부 표현 방식에 영구히 묶인다.
2. 너무 많은 공간을 차지함
3. 시간이 너무 많이걸림
4. 스택 오버 플로를 일으킬 수 있다.
```

- 합리적인 직렬화 형태는 어떻게 될까
- 단순히 리스트가 포함한 문자열의 개수를 적은 다음 그 뒤로 문자열을 나열하는 수준이면 된다.

```java
import java.io.IOException;
import java.io.Serializable;

public final class StringList implements Serializable {
    private transient int size = 0;
    private transient Entry head = null;

    // 직렬화 안됨
    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }

    public final void add(String s) { ...}

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(size);
        
        for (Entry e = head; e != null; e = e.next) {
            s.writeObject(e.data);
        }
    }
    
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int numElements = s.readInt();
        
        for (int i = 0; i < numElements; i++) {
            add((String)s.readObject());
        }
    }
    ...
}
```

- 어떤 직렬화 형태를 택하든 직렬화 가능 클래스 모두에 직렬 버전 UID를 명시적으로 부여하자.

```java
private static final long serialVersionUID = 무작위 Long 값
```

- 이걸 바꾸면 안됨

# item88. readObject 메소드는 방어적으로 작성하라

- 아이템 50에서는 불변인 날짜 범위 클래스를 만드는 데 가변인 Date 필드를 이용했음.
- 불변식을 지키고 불변을 유지하기 위해 생성자와 접근자에서 Date객체를 방어적으로 복사하느라 코드가 상당히 길어짐

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

- 이 클래스를 직렬화 하기로 해보면
- Period 객체의 물리적 표현이 논리적 표현과 부합하므로 기본 직렬화 형태를 사용해도 나쁘지 않아보임
- implements Serializable만 추가하면 되는데, 이러면 불변식을 더이상 보장하지 못한다.
- 이 문제를 고치려면 Period의 readObject 메소드가 defaultReadObject를 호출한 다음 역직렬화 된 객체가 유효한지 검사해야 한다

```java
private void readObject(ObjectInputStream s) {
    s.defaultReadObject();
    
    // 비교 로직 추가
    if (start.compareTo(end) > 0) {
        ..
    }
}
```

- 추가로 방어적 복사를 해야 역직렬화 시에 데이터 변조가 일어나지 않는다.

# item89. 인스턴스 수를 통제해야 한다면 readResolve보다는 열거 타입을 사용하라

- 아이템 3에서 싱글턴 패턴을 보여줬었음

```java
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() {}
    
    public void leaveTheBuilding() { ... }
}
```

- 여기에 implements Serializable을 추가하는 순간 더이상 싱글턴이 아니다
- readObject를 제공하더라도 초기화 될때 만들어진 인스턴스와는 별개인 인스턴스를 반환하게 된다
- 이럴 때 사용하는게 readResolve

```java
private Object readResolve() {
    return INSTANCE;
}
```

- 이 메소드는 역직렬화한 객체는 무시하고 클래스 초기화할 때 만들어진 Elvis 인스턴스를 반환한다.
- 이럴 경우 모든 인스턴스를 transient로 선언해야 한다.
- 사실 readResolve를 인스턴스 통제 목적으로 사용한다면 객체 참조 타입 인스턴스 필드는 모두 transient로 선언해야 한다.
- 공격하는 방법은 간단한데 만약 transient필드가 아니라면, 필드의 내용들은 readResolve가 실행되기 전에 역직렬화 된다.
- 그렇게 되면 잘 조작된 스트림을 써서 참조 필드의 내용이 역직렬화 되는 시점에 그 역직렬화된 인스턴스의 참조를 훔칠 수 있다.

# item90. 직렬화된 인스턴스 대신 직렬화 프록시 사용을 검토하라

- 계속 얘기한 것 처럼 역직렬화를 사용하면 언어의 정상적인 매커니즘인 생성자 이외의 방법으로 인스턴스 생성이 가능하다
- 버그와 보안에 문제가 있다는건데 이걸 막기 위한 기법이 직렬화 프록시
- 클래스의 논리적 상태를 정밀하게 표현하는 중첩 클래스를 설계해 private static으로 선언한다
- 이 중첩 클래스가 바로 바깥 클래스의 직렬화 프록시

```java
import java.io.Serializable;

private static class SerializationProxy implements Serializable {
    private final Date start;
    private final Date end;

    SerializableProxy(Period p) {
        this.start = p.start;
        this.end = p.end;
    }
    
    private static final long serialVersionUID = 123123123123123123;
}
```

- 이제 바깥클래스에 다음 writeReplace 메소드를 추가한다.

```java
private Object writeReplace() {
    return new SerializationProxy(this);    
}
```

- 이러면 바깥 클래스의 인스턴스 대신 프록시 인스턴스를 반홚나다.
- 즉 먼저 프록시로 먼저 직렬화가 됨
- 이제 막을려면

```java
private void readObject(ObjectInputStream stream) {
    thorw new InvalidObjectException("프록시가 필요합니다")
}
```

- 그리고 readResolve 메소드를 SerializationProxy 클래스에 추가해준다
- 이런식으로 방어를 해줄 수있음

