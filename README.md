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