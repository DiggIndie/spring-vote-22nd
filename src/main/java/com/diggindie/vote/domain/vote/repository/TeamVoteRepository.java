package com.diggindie.vote.domain.vote.repository;

import com.diggindie.vote.domain.vote.domain.TeamVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamVoteRepository extends JpaRepository<TeamVote, Long> {

    boolean existsByVoterId(Long memberId);
}