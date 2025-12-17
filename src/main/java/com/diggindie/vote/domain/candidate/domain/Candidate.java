package com.diggindie.vote.domain.candidate.domain;

import com.diggindie.vote.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    public Candidate(Member member) {
        this.member = member;
    }

}