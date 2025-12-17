package com.diggindie.vote.domain.vote.repository;

import com.diggindie.vote.domain.vote.domain.PartVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartVoteRepository extends JpaRepository<PartVote, Long> {

    @Query("SELECT pv.candidate.id, COUNT(pv) FROM PartVote pv GROUP BY pv.candidate.id")
    List<Object[]> countVotesByCandidate();

    boolean existsByVoterId(Long memberId);
}

