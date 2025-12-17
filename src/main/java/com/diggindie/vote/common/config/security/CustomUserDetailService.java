package com.diggindie.vote.common.config.security;

import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        return new CustomUserDetails(member.getId(), member.getLoginId(), member.getRole());
    }


    public CustomUserDetails loadByExternalId(String externalId) {

        Member member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        return new CustomUserDetails(member.getId(), member.getExternalId(), member.getRole());
    }
}
