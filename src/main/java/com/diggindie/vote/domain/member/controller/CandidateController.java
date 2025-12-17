package com.diggindie.vote.domain.member.controller;

import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.config.security.CustomUserDetails;
import com.diggindie.vote.common.enums.Part;
import com.diggindie.vote.common.response.Response;
import com.diggindie.vote.domain.member.dto.CandidateApplyResponse;
import com.diggindie.vote.domain.member.dto.CandidateListResponse;
import com.diggindie.vote.domain.member.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/candidates")
    public ResponseEntity<Response<CandidateListResponse>> getCandidates(
            @RequestParam("part") Part part
    ) {
        Response<CandidateListResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "파트장 후보 반환 API",
                candidateService.getCandidatesByPart(part)
        );
        return ResponseEntity.ok().body(response);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/votes/leaders/results")
    public ResponseEntity<Response<CandidateListResponse>> getCandidatesVote(
            @RequestParam("part") Part part
    ) {
        Response<CandidateListResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "파트장 투표 결과 반환 API",
                candidateService.getCandidateVoteByPart(part)
        );
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/candidates/apply")
    public ResponseEntity<Response<CandidateApplyResponse>> applyCandidate(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Response<CandidateApplyResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "파트장 후보 등록 API",
                candidateService.applyCandidate(userDetails.getMemberId())
        );
        return ResponseEntity.ok().body(response);
    }
}
