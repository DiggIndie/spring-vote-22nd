package com.diggindie.vote.domain.vote.repository;

import com.diggindie.vote.domain.vote.domain.TeamVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamVoteRepository extends JpaRepository<TeamVote, Long> {

    @Query("SELECT tv.team.id, COUNT(tv) FROM TeamVote tv GROUP BY tv.team.id")
    List<Object[]> countVotesByTeam();

    boolean existsByVoterId(Long voterId);

    boolean existsByTeamIdAndVoterId(Long teamId, Long voterId);
}

