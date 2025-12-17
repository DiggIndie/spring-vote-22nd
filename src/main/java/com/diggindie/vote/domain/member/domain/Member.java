package com.diggindie.vote.domain.member.domain;

import com.diggindie.vote.common.enums.Part;
import com.diggindie.vote.common.enums.Role;
import com.diggindie.vote.domain.candidate.domain.Candidate;
import com.diggindie.vote.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "external_id", nullable = false, length = 36, unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false, length = 20)
    private Part part;

    @Column(name = "login_id", nullable = false, unique = true, length = 20)
    private String loginId;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "membername", nullable = false, length = 10)
    private String memberName;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Candidate candidate;

    @Column(name = "has_voted_team", nullable = false)
    private boolean hasVotedTeam = false;

    @Column(name = "has_voted_candidate", nullable = false)
    private boolean hasVotedCandidate = false;

    @Builder
    public Member(Role role, Team team, Part part, String loginId, String email, String password, String memberName) {
        this.externalId = UUID.randomUUID().toString();
        this.role = Role.ROLE_USER;
        this.team = team;
        this.part = part;
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.memberName = memberName;
        this.hasVotedTeam = false;
        this.hasVotedCandidate = false;
    }

    public void markTeamVoted() {
        this.hasVotedTeam = true;
    }

    public void markCandidateVoted() {
        this.hasVotedCandidate = true;
    }
}