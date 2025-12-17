package com.diggindie.vote.domain.team.controller;

import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.response.Response;
import com.diggindie.vote.domain.team.dto.TeamListResponse;
import com.diggindie.vote.domain.team.dto.TeamVoteRequestDto;
import com.diggindie.vote.domain.team.service.TeamService;
import com.diggindie.vote.domain.team.service.TeamVoteExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final RedissonClient redissonClient;
    private final TeamVoteExecutor teamVoteExecutor;

    private static final String TEAM_VOTE_LOCK_PREFIX = "vote:team:lock:";

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/teams")
    public ResponseEntity<Response<TeamListResponse>> getTeamList() {

        Response<TeamListResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "팀 목록 반환 API",
                teamService.getTeamList()
        );
        return ResponseEntity.ok().body(response);
    }

    public void vote(String externalId, TeamVoteRequestDto request) {
        String lockKey = TEAM_VOTE_LOCK_PREFIX + externalId; // 투표자 기준 락
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(3, 15, TimeUnit.SECONDS); // wait 3s, lease 15s
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

