package com.diggindie.vote.domain.candidate.service;

import com.diggindie.vote.domain.candidate.domain.Candidate;
import com.diggindie.vote.domain.candidate.dto.PartVoteRequestDto;
import com.diggindie.vote.domain.candidate.repository.CandidateRepository;
import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartVoteExecutor {

    private final MemberRepository memberRepository;
    private final CandidateRepository candidateRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(String externalId, PartVoteRequestDto request) {
        Member member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (member.isHasVotedCandidate()) {
            throw new IllegalStateException("이미 파트장 투표를 완료하였습니다.");
        }

        Candidate candidate = candidateRepository.findById(request.candidateId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후보자입니다."));

        if (candidate.getMember().getId().equals(member.getId())) {
            throw new IllegalStateException("자기 자신에게는 투표할 수 없습니다.");
        }

        if (member.getPart() != candidate.getMember().getPart()) {
            throw new IllegalStateException("같은 파트의 후보자에게만 투표할 수 있습니다.");
        }

        candidate.increaseVoteCount();
        member.markCandidateVoted();
    }
}