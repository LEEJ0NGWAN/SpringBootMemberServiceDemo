package com.example.springbootmemberServicedemo.service;

import com.example.springbootmemberServicedemo.domain.Member;
import com.example.springbootmemberServicedemo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

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
