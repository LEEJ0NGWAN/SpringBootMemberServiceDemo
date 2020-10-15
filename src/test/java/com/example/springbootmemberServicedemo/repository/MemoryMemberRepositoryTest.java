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

