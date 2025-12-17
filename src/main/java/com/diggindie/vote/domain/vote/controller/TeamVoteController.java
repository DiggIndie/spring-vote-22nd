package com.diggindie.vote.domain.vote.controller;

import com.diggindie.vote.domain.vote.dto.TeamVoteRequestDto;
import com.diggindie.vote.domain.vote.service.TeamVoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamVoteController {

    private final TeamVoteService teamVoteService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> vote(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TeamVoteRequestDto request
    ) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        teamVoteService.vote(memberId, request);
        return ResponseEntity.ok("투표가 완료되었습니다.");
    }
}
