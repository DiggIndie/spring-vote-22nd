package com.diggindie.vote.domain.candidate.controller;

import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.config.security.CustomUserDetails;
import com.diggindie.vote.common.enums.Part;
import com.diggindie.vote.common.response.Response;
import com.diggindie.vote.domain.candidate.dto.CandidateApplyResponse;
import com.diggindie.vote.domain.candidate.dto.CandidateListResponse;
import com.diggindie.vote.domain.candidate.dto.PartVoteRequestDto;
import com.diggindie.vote.domain.candidate.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Candidate", description = "파트장 후보 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @Operation(summary = "파트장 후보 목록 조회", description = "특정 파트의 파트장 후보 목록을 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/candidates")
    public ResponseEntity<Response<CandidateListResponse>> getCandidates(
            @Parameter(description = "파트 (FRONTEND 또는 BACKEND)") @RequestParam("part") Part part
    ) {
        return ResponseEntity.ok().body(Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "파트장 후보 반환 API",
                candidateService.getCandidatesByPart(part)
        ));
    }

    @Operation(summary = "파트장 투표 결과 조회", description = "특정 파트의 파트장 후보별 득표수를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/votes/leaders/results")
    public ResponseEntity<Response<CandidateListResponse>> getCandidatesVote(
            @Parameter(description = "파트 (FRONTEND 또는 BACKEND)") @RequestParam("part") Part part
    ) {
        return ResponseEntity.ok().body(Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "파트장 투표 결과 반환 API",
                candidateService.getCandidateVoteByPart(part)
        ));
    }

    @Operation(summary = "파트장 후보 등록", description = "로그인한 사용자를 자신의 파트에 맞는 파트장 후보로 등록합니다.")
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

    @Operation(summary = "파트장 투표", description = "특정 파트장 후보에게 투표합니다.")
    @PostMapping("/votes/leaders")
    public ResponseEntity<Response<Void>> voteCandidate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PartVoteRequestDto request
    ) {
        candidateService.vote(userDetails.getExternalId(), request);
        return ResponseEntity.ok().body(Response.of(
                SuccessCode.INSERT_SUCCESS,
                true,
                "파트장 투표 완료",
                (Void) null
        ));
    }
}