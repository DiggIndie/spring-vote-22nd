package com.diggindie.vote.domain.candidate.service;

import com.diggindie.vote.common.enums.Part;
import com.diggindie.vote.domain.candidate.domain.Candidate;
import com.diggindie.vote.domain.candidate.dto.PartVoteRequestDto;
import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.candidate.dto.CandidateApplyResponse;
import com.diggindie.vote.domain.candidate.dto.CandidateDto;
import com.diggindie.vote.domain.candidate.dto.CandidateListResponse;
import com.diggindie.vote.domain.candidate.repository.CandidateRepository;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final MemberRepository memberRepository;
    private final RedissonClient redissonClient;
    private final PartVoteExecutor partVoteExecutor;

    private static final String PART_VOTE_LOCK_PREFIX = "vote:part:lock:";

    public CandidateListResponse getCandidatesByPart(Part part) {
        List<Candidate> candidates = candidateRepository.findAllByPart(part);

        List<CandidateDto> candidateDtos = candidates.stream()
                .map(candidate -> new CandidateDto(
                        candidate.getId(),
                        candidate.getMember().getMemberName(),
                        candidate.getMember().getPart().toString(),
                        null  // 투표 전에는 득표수 숨김
                ))
                .toList();

        return new CandidateListResponse(part.toString(), candidateDtos);
    }

    public CandidateListResponse getCandidateVoteByPart(Part part) {
        List<Candidate> candidates = candidateRepository.findAllByPart(part);

        List<CandidateDto> candidateDtos = candidates.stream()
                .map(candidate -> new CandidateDto(
                        candidate.getId(),
                        candidate.getMember().getMemberName(),
                        candidate.getMember().getPart().toString(),
                        (long) candidate.getVoteCount()  // 엔티티에서 직접 조회
                ))
                .toList();

        return new CandidateListResponse(part.toString(), candidateDtos);
    }

    @Transactional
    public CandidateApplyResponse applyCandidate(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (member.getCandidate() != null) {
            throw new IllegalArgumentException("이미 파트장 후보로 등록되어 있습니다.");
        }

        Candidate candidate = new Candidate(member);
        Candidate savedCandidate = candidateRepository.save(candidate);

        return new CandidateApplyResponse(
                savedCandidate.getId(),
                member.getMemberName(),
                member.getPart().toString()
        );
    }

    public void vote(String externalId, PartVoteRequestDto request) {
        String lockKey = PART_VOTE_LOCK_PREFIX + externalId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(3, 15, TimeUnit.SECONDS);
            if (!acquired) {
                throw new IllegalStateException("요청이 많습니다. 잠시 후 다시 시도해주세요.");
            }

            partVoteExecutor.execute(externalId, request);
            log.info("파트장 투표 완료 - externalId: {}, candidateId: {}", externalId, request.candidateId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("투표 처리 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }
}