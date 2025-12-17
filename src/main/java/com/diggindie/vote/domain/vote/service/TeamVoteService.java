package com.diggindie.vote.domain.vote.service;

import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import com.diggindie.vote.domain.team.domain.Team;
import com.diggindie.vote.domain.team.repository.TeamRepository;
import com.diggindie.vote.domain.vote.domain.TeamVote;
import com.diggindie.vote.domain.vote.dto.TeamVoteRequestDto;
import com.diggindie.vote.domain.vote.repository.TeamVoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeamVoteService {

    private final TeamVoteRepository teamVoteRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final RedissonClient redissonClient;

    private static final String VOTE_LOCK_PREFIX = "vote:lock:";

    public void vote(Long memberId, TeamVoteRequestDto request) {
        String lockKey = VOTE_LOCK_PREFIX + memberId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락 획득 시도
            boolean acquired = lock.tryLock(5, 3, TimeUnit.SECONDS);

            if (!acquired) {
                throw new IllegalStateException("요청이 많습니다. 잠시 후 다시 시도해주세요.");
            }

            // 실제 투표 로직 실행
            doVote(memberId, request);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("투표 처리 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    protected void doVote(Long memberId, TeamVoteRequestDto request) {
        if (teamVoteRepository.existsByVoterId(memberId)) {
            throw new IllegalStateException("이미 투표하셨습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        TeamVote vote = TeamVote.builder()
                .voter(member)
                .team(team)
                .build();

        teamVoteRepository.save(vote);
        log.info("투표 완료 - memberId: {}, teamId: {}", memberId, request.getTeamId());
    }

}