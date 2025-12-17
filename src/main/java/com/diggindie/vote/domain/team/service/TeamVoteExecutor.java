package com.diggindie.vote.domain.team.service;

import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import com.diggindie.vote.domain.team.domain.Team;
import com.diggindie.vote.domain.team.dto.TeamVoteRequestDto;
import com.diggindie.vote.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamVoteExecutor {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(String externalId, TeamVoteRequestDto request) {
        Member member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (member.isHasVotedTeam()) {
            throw new IllegalStateException("이미 팀 투표하셨습니다.");
        }

        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        if (member.getTeam().getId().equals(team.getId())) {
            throw new IllegalStateException("자신이 소속한 팀에는 투표할 수 없습니다.");
        }

        team.increaseVoteCount();
        member.markTeamVoted();
    }
}
