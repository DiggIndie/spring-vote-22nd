package com.diggindie.vote.domain.member.dto;

import java.util.List;

public record CandidateListResponse(
        String part,
        List<CandidateDto> candidates
) {
}

