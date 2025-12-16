package com.diggindie.vote.domain.vote.domain;

import com.diggindie.vote.domain.member.domain.Candidate;
import com.diggindie.vote.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "part_vote", indexes = {
        @Index(name = "idx_part_vote_candidate", columnList = "candidate_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_member_id", nullable = false, unique = true)
    private Member voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;
}
