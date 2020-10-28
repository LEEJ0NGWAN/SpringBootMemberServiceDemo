package com.example.springbootmemberServicedemo.service;

import com.example.springbootmemberServicedemo.domain.Member;
import com.example.springbootmemberServicedemo.repository.MemberRepository;
import com.example.springbootmemberServicedemo.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MemberServiceIntegrationTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void join() {
        // given
        Member member = new Member();
        member.setName("TEST");

        // when
        Long saveId = memberService.join(member);

        // then
        Member result = memberService.findMember(saveId).get();
        assertThat(member.getName()).isEqualTo(result.getName());
    }

    @Test
    public void duplicated_join_exception() {
        // given
        Member member1 = new Member();
        member1.setName("TEST");

        Member member2 = new Member();
        member2.setName("TEST");

        // when
        memberService.join(member1);

        /*
        try {
            memberService.join(member2);
            fail("");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
        */

        // then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }
}

