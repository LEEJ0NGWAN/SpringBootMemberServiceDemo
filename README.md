이 프로젝트는 인프런 강의 "김영한의 스프링 입문 코드로 배우는 스프링 부트, 웹 MVC, DB 접근 기술"을 토대로 만들어졌습니다.

# 프로젝트 기본 세팅

- 환경
    - Java 11
    - IntelliJ IDEA CE
    - Gradle
- 스프링 부트 이니셜라이저 세팅

    [https://start.spring.io](https://start.spring.io/)

    - Project → Gradle
    - Spring Boot → 2.3.x
    - Language → Java
    - Packaging → jar
    - groupId → com.example
    - artifactId → spring-boot-memberService-demo
    - dependencies
        - Spring-Web
        - Tyhmeleaf (HTML 탬플릿 엔진)

# 비즈니스 요구사항

- 데이터
    - 사용자
        - id:Long
        - name:String
- 기능
    - 사용자 등록
    - 사용자 조회
- 데이터 저장소가 선정되지 않았음 → 어떤 유형의 DB에도 구속되지 않도록 인터페이스 리포지토리 구현

### 일반적인 웹 애플리케이션 계층 구조

![web-application](./image/web-application.png)

- 컨트롤러 → 웹 MVC 패턴의 전형적인 컨트롤러 컴포넌트 역할
- 서비스 → 핵심 비즈니스 로직을  구현하는 컴포넌트
- 리포지토리 → DB에 접근; 도메인 객체를 DB에 저장 및 관리
- 도메인 → 비즈니스에서 사용되는 데이터 객체(비즈니스 도메인 객체)

    e.g., 회원, 주문, 게시글 등등 DB에 저장되는 레코드(인스턴스)에 대응되어 관리되는 개체들

## 클래스 의존 관계

![class-dependency](./image/class-dependency.png)

- MemberService

    목표 기능(회원 가입, 회원 조회)을 실제 구현하는 클래스

- MemberRepository

    데이터 저장소가 선정되지 않았기 때문에, 구현될 리포지토리의 요구 형태만 기술한 리포지토리 인터페이스

- MemoryMemberRepository

    데이터 저장소가 메모리 기반의 저장소라고 가정할 때, MemberRepository 인터페이스를 구현한 메모리 구현체 리포지토리

# Domain 만들기

### Member

```jsx
package com.example.springbootmemberServicedemo.domain;

public class Member {

    private Long id; // db의 pk와 매칭
    private String name; // 회원 이름

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

# Repository 만들기

### MemberRepository(Interface)

```jsx
package com.example.springbootmemberServicedemo.repository;

import com.example.springbootmemberServicedemo.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);

    List<Member> findAll();

}
```

- Optional

    Java 8에서 추가된 객체 타입; 값이 없을 때 Null을 대체하기 위한 용도

### MemoryMemberRepository

```jsx
package com.example.springbootmemberServicedemo.repository;

import com.example.springbootmemberServicedemo.domain.Member;

import java.util.*;

public class MemoryMemberRepository implements MemberRepository {

    private Map<Long, Member> store = new HashMap<>();
    private static Long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);

        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public static void setSequence(Long sequence) {
        MemoryMemberRepository.sequence = sequence;
    }

    public void clearStore() {
        store.clear();
        MemoryMemberRepository.setSequence(0L);
    }
}
```

MemberRepository의 메모리 구현체

동시성 문제가 고려되지 않았음

→ 실무는 ConcurrentHashMap, AtomicLong 등을 사용하여 동시성 문제 극복

# Service 만들기

## 메소드 네이밍

- repository

    비즈니스 로직과는 별개로, 데이터에 접근하기 위해 필요한 원초적인 기능에 걸맞게 네이밍

    e.g., save, find ...

- service

    실제 비즈니스 로직을 나타내므로, 기획이나 요구사항에 맞게 네이밍

    e.g., join, find ...

### MemberService

`repository` 패키지를 생성 후, 내부에 MemberService 클래스 생성

```jsx
package com.example.springbootmemberServicedemo.service;

import com.example.springbootmemberServicedemo.domain.Member;
import com.example.springbootmemberServicedemo.repository.MemberRepository;
import com.example.springbootmemberServicedemo.repository.MemoryMemberRepository;

import java.util.List;
import java.util.Optional;

public class MemberService {

    private final MemberRepository memberRepository = new MemoryMemberRepository();

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    // 회원 가입
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 검사

        memberRepository.save(member);

        return member.getId();
    }

    // 전체 회원 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 특정 회원 조회
    public Optional<Member> findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
```

# TestCase

JUnit, assertj 와 같은 테스트 프레임워크를 이용하여 테스트케이스를 작성

`src/test/java/아티팩트이름` 내부에 패키지를 생성하고 Test용 클래스 파일을 만드는 것이 통상적

테스트 하고자 하는 클래스의 이름 뒤에 `Test` 를 붙이는 것도 통상적

### MemoryMemberRepositoryTest

- src

    → test

    → java

    → com.example.springbootmemberServicedemo

    → repository (패키지)

    ⇒ MemoryMemberRepositoryTest

```jsx
package com.example.springbootmemberServicedemo.repository;

import com.example.springbootmemberServicedemo.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
//import org.junit.jupiter.api.Assertions;

class MemoryMemberRepositoryTest {

    MemoryMemberRepository repos = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        repos.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member();

        member.setName("TestUser");
        repos.save(member);

        Member result = repos.findById(member.getId()).get();

        // JUnit Assertion
        // Assertions.assertEquals(member, result);가

        // assertj Assertion
        assertThat(member).isEqualTo(result);

    }

    @Test
    public void findByName() {
        Member member1 = new Member();
        member1.setName("Test1");
        repos.save(member1);

        Member member2 = new Member();
        member2.setName("Test2");
        repos.save(member2);

        Member result = repos.findByName("Test1").get();
        assertThat(member1).isEqualTo(result);

    }

    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setName("Test1");
        repos.save(member1);

        Member member2 = new Member();
        member2.setName("Test2");
        repos.save(member2);

        List<Member> result = repos.findAll();

        assertThat(result.size()).isEqualTo(2);

    }
}
```

- `JUnit` 프레임워크 혹은 `assertj` 프레임워크를 이용해 assert 검증을 기능별로 실시

    → `assertj` 프레임워크의 `assertThat` 메소드는 static 메소드로 static 임포트로 불러와서 사용

- `@AfterEach` 애노테이션을 이용하여 각 기능의 테스트 뒤에 실행할, 일종의 콜백을 설정할 수 있음

    → 각 테스트 후 리포지토리를 초기화 시켜줌으로써 각 기능 테스트들이 서로에게 관여되지 않도록 설정

- 각 테스트는 독립적이어야함 → 테스트 간의 의존관계나 순서가 있는 것은 결코 좋은 것이 아님
- 이런 테스트케이스를 먼저 만들고 로직을 만드는 방법이 TDD