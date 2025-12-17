package com.diggindie.vote.domain.vote.service;

import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import com.diggindie.vote.domain.team.domain.Team;
import com.diggindie.vote.domain.team.repository.TeamRepository;
import com.diggindie.vote.domain.vote.domain.TeamVote;
import com.diggindie.vote.domain.vote.dto.TeamVoteRequestDto;
import com.diggindie.vote.domain.vote.repository.TeamVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteExecutor {

    private final TeamVoteRepository teamVoteRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void execute(String loginId, TeamVoteRequestDto request) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (teamVoteRepository.existsByVoterId(member.getId())) {
            throw new IllegalStateException("이미 투표하셨습니다.");
        }

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        TeamVote vote = TeamVote.builder()
                .voter(member)
                .team(team)
                .build();

        teamVoteRepository.save(vote);
    }
}