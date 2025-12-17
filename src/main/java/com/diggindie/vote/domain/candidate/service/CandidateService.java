package com.diggindie.vote.domain.candidate.service;

import com.diggindie.vote.common.enums.Part;
import com.diggindie.vote.domain.candidate.domain.Candidate;
import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.candidate.dto.CandidateApplyResponse;
import com.diggindie.vote.domain.candidate.dto.CandidateDto;
import com.diggindie.vote.domain.candidate.dto.CandidateListResponse;
import com.diggindie.vote.domain.candidate.repository.CandidateRepository;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import com.diggindie.vote.domain.vote.repository.PartVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final PartVoteRepository partVoteRepository;
    private final MemberRepository memberRepository;

    public CandidateListResponse getCandidatesByPart(Part part) {
        List<Candidate> candidates = candidateRepository.findAllByPart(part);
        List<Object[]> voteCounts = partVoteRepository.countVotesByCandidate();

        Map<Long, Long> voteCountMap = voteCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        List<CandidateDto> candidateDtos = candidates.stream()
                .map(candidate -> new CandidateDto(
                        candidate.getId(),
                        candidate.getMember().getMemberName(),
                        candidate.getMember().getPart().toString(),
                        null
                ))
                .toList();

        return new CandidateListResponse(part.toString(), candidateDtos);
    }

    public CandidateListResponse getCandidateVoteByPart(Part part) {
        List<Candidate> candidates = candidateRepository.findAllByPart(part);
        List<Object[]> voteCounts = partVoteRepository.countVotesByCandidate();

        Map<Long, Long> voteCountMap = voteCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        List<CandidateDto> candidateDtos = candidates.stream()
                .map(candidate -> new CandidateDto(
                        candidate.getId(),
                        candidate.getMember().getMemberName(),
                        candidate.getMember().getPart().toString(),
                        voteCountMap.getOrDefault(candidate.getId(),0L)
                ))
                .toList();

        return new CandidateListResponse(part.toString(), candidateDtos);
    }

    @Transactional
    public CandidateApplyResponse applyCandidate(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 이미 후보로 등록되어 있는지 확인
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
}
