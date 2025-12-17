package com.diggindie.vote.domain.candidate.controller;

import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.config.security.CustomUserDetails;
import com.diggindie.vote.common.enums.Part;
import com.diggindie.vote.common.response.Response;
import com.diggindie.vote.domain.candidate.dto.CandidateApplyResponse;
import com.diggindie.vote.domain.candidate.dto.CandidateListResponse;
import com.diggindie.vote.domain.candidate.dto.PartVoteRequestDto;
import com.diggindie.vote.domain.candidate.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/candidates")
    public ResponseEntity<Response<CandidateListResponse>> getCandidates(
            @RequestParam("part") Part part
    ) {
        return ResponseEntity.ok().body(Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "파트장 후보 반환 API",
                candidateService.getCandidatesByPart(part)
        ));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/votes/leaders/results")
    public ResponseEntity<Response<CandidateListResponse>> getCandidatesVote(
            @RequestParam("part") Part part
    ) {
        return ResponseEntity.ok().body(Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "파트장 투표 결과 반환 API",
                candidateService.getCandidateVoteByPart(part)
        ));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/candidates/apply")
    public ResponseEntity<Response<CandidateApplyResponse>> applyCandidate(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok().body(Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "파트장 후보 등록 API",
                candidateService.applyCandidate(userDetails.getMemberId())
        ));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/votes/leaders")
    public ResponseEntity<Response<Void>> voteCandidate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PartVoteRequestDto request
    ) {
        candidateService.vote(userDetails.getUserId(), request);  // userId = externalId
        return ResponseEntity.ok().body(Response.of(
                SuccessCode.INSERT_SUCCESS,
                true,
                "파트장 투표 완료",
                (Void) null
        ));
    }
}