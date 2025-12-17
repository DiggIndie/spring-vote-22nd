package com.diggindie.vote.domain.team.controller;

import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.config.security.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/votes/teams")
    public ResponseEntity<Response<Void>> voteTeam(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TeamVoteRequestDto request
    ) {
        teamService.vote(userDetails.getExternalId(), request);  // getUserId() → getExternalId()
        return ResponseEntity.ok().body(Response.of(
                SuccessCode.INSERT_SUCCESS,
                true,
                "팀 투표 완료",
                (Void) null
        ));
    }
}

