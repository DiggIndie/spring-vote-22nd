package com.diggindie.vote.domain.vote.controller;

import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.response.Response;
import com.diggindie.vote.domain.vote.dto.TeamVoteResultResponse;
import com.diggindie.vote.domain.vote.service.TeamVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamVoteController {

    private final TeamVoteService teamVoteService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/votes/teams")
    public ResponseEntity<Response<TeamVoteResultResponse>> getTeamVoteResults() {

        Response<TeamVoteResultResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "팀 투표 결과 반환 API",
                teamVoteService.getTeamVoteResults()
        );
        return ResponseEntity.ok().body(response);
    }
}

