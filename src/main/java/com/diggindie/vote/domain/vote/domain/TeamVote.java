package com.diggindie.vote.domain.vote.domain;

import com.diggindie.vote.domain.team.domain.Team;
import com.diggindie.vote.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_vote")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_member_id", nullable = false, unique = true)
    private Member voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}

