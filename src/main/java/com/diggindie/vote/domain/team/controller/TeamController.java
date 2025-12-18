package com.diggindie.vote.domain.team.controller;

import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.config.security.CustomUserDetails;
import com.diggindie.vote.common.response.Response;
import com.diggindie.vote.domain.team.dto.TeamListResponse;
import com.diggindie.vote.domain.team.dto.TeamVoteRequestDto;
import com.diggindie.vote.domain.team.service.TeamService;
import com.diggindie.vote.domain.team.service.TeamVoteExecutor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Team", description = "팀 관련 API")
@RestController
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "팀 목록 조회", description = "모든 팀의 목록을 조회합니다.")
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

    @Operation(summary = "팀 투표", description = "특정 팀에 투표합니다. 자신이 소속된 팀에는 투표할 수 없습니다.")
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
