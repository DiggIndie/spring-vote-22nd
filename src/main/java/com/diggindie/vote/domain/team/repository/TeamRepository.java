package com.diggindie.vote.domain.team.repository;

import com.diggindie.vote.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}