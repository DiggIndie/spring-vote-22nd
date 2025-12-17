package com.diggindie.vote.domain.vote.service;

import com.diggindie.vote.domain.team.domain.Team;
import com.diggindie.vote.domain.team.repository.TeamRepository;
import com.diggindie.vote.domain.vote.dto.TeamVoteRequestDto;
import com.diggindie.vote.domain.vote.dto.TeamVoteResultDto;
import com.diggindie.vote.domain.vote.dto.TeamVoteResultResponse;
import com.diggindie.vote.domain.vote.repository.TeamVoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeamVoteService {

    private final TeamVoteRepository teamVoteRepository;
    private final TeamRepository teamRepository;
    private final RedissonClient redissonClient;

    private static final String VOTE_LOCK_PREFIX = "vote:lock:";

    private final TeamVoteExecutor voteExecutor;

    // @Transactional 없이 진행 (executor에서 트랜잭션!)
    public void vote(String loginId, TeamVoteRequestDto request) {
        String lockKey = VOTE_LOCK_PREFIX + loginId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(5, 3, TimeUnit.SECONDS);

            if (!acquired) {
                throw new IllegalStateException("요청이 많습니다. 잠시 후 다시 시도해주세요.");
            }

            // 별도 클래스 호출 → 프록시 타서 @Transactional 적용
            voteExecutor.execute(loginId, request);
            log.info("투표 완료 - loginId: {}, teamId: {}", loginId, request.teamId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("투표 처리 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public TeamVoteResultResponse getTeamVoteResults() {
        List<Team> teams = teamRepository.findAll();
        List<Object[]> voteCounts = teamVoteRepository.countVotesByTeam();

        Map<Long, Long> voteCountMap = voteCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        List<TeamVoteResultDto> teamVoteResults = teams.stream()
                .map(team -> new TeamVoteResultDto(
                        team.getId(),
                        team.getTeamName(),
                        team.getProposal(),
                        voteCountMap.getOrDefault(team.getId(), 0L)
                ))
                .toList();

        return new TeamVoteResultResponse(teamVoteResults);
    }

}

