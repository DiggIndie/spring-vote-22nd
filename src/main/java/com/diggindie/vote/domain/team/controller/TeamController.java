package com.diggindie.vote.domain.team.controller;

import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.response.Response;
import com.diggindie.vote.domain.team.dto.TeamListResponse;
import com.diggindie.vote.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
}

