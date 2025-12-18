package com.diggindie.vote.domain.team.service;

import com.diggindie.vote.domain.team.domain.Team;
import com.diggindie.vote.domain.team.dto.TeamDto;
import com.diggindie.vote.domain.team.dto.TeamListResponse;
import com.diggindie.vote.domain.team.dto.TeamVoteRequestDto;
import com.diggindie.vote.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final RedissonClient redissonClient;
    private final TeamVoteExecutor teamVoteExecutor;

    private static final String TEAM_VOTE_LOCK_PREFIX = "vote:team:lock:";

    public TeamListResponse getTeamList() {
        List<Team> teams = teamRepository.findAll();

        List<TeamDto> teamDtos = teams.stream()
                .map(team -> new TeamDto(
                        team.getId(),
                        team.getTeamName(),
                        team.getProposal()
                ))
                .toList();

        return new TeamListResponse(teamDtos);
    }

    public void vote(String externalId, TeamVoteRequestDto request) {
        String lockKey = TEAM_VOTE_LOCK_PREFIX + request.teamId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(3, 15, TimeUnit.SECONDS);
            if (!acquired) {
                throw new IllegalStateException("요청이 많습니다. 잠시 후 다시 시도해주세요.");
            }

            teamVoteExecutor.execute(externalId, request);
            log.info("팀 투표 완료 - externalId: {}, teamId: {}", externalId, request.teamId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("투표 처리 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }
}

