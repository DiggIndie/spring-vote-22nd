package com.diggindie.vote.domain.member.repository;

import com.diggindie.vote.common.enums.Part;
import com.diggindie.vote.domain.member.domain.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    @Query("SELECT c FROM Candidate c JOIN FETCH c.member m WHERE m.part = :part")
    List<Candidate> findAllByPart(@Param("part") Part part);
}

