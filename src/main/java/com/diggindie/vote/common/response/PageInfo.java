package com.diggindie.vote.common.response;

public record PageInfo(
        int page,
        int size,
        boolean hasNext,
        long totalElements,
        int totalPages
) {}