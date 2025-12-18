package com.diggindie.vote.domain.member.repository;

import com.diggindie.vote.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByExternalId(String externalId);

    boolean existsByLoginId(String loginId);

}