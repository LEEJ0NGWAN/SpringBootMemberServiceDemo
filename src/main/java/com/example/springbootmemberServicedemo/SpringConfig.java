package com.example.springbootmemberServicedemo;

import com.example.springbootmemberServicedemo.repository.JdbcMemberRepository;
import com.example.springbootmemberServicedemo.repository.JdbcTemplateMemberRepository;
import com.example.springbootmemberServicedemo.repository.MemberRepository;
import com.example.springbootmemberServicedemo.repository.MemoryMemberRepository;
import com.example.springbootmemberServicedemo.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    private final DataSource dataSource;

    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
//        return new MemoryMemberRepository();
//        return new JdbcMemberRepository(dataSource);
        return new JdbcTemplateMemberRepository(dataSource);
    }
}

