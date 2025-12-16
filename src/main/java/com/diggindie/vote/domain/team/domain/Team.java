package com.diggindie.vote.domain.team.domain;

import com.diggindie.vote.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "proposal", length = 200)
    private String proposal;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();
}