package com.diggindie.vote.domain.candidate.dto;

import java.util.List;

public record CandidateListResponse(
        String part,
        List<CandidateDto> candidates
) {
}

