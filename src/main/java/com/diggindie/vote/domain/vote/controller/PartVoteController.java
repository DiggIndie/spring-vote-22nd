package com.diggindie.vote.domain.vote.controller;

import com.diggindie.vote.domain.vote.dto.PartVoteRequestDto;
import com.diggindie.vote.domain.vote.service.PartVoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PartVoteController {

    private final PartVoteService partVoteService;

    @PostMapping("/votes/parts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> vote(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PartVoteRequestDto request
    ) {
        String loginId = userDetails.getUsername();
        partVoteService.vote(loginId, request);
        return ResponseEntity.ok("파트장 투표가 완료되었습니다.");
    }
}