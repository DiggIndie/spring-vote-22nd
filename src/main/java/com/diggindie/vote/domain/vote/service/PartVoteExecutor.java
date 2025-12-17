package com.diggindie.vote.domain.vote.service;

import com.diggindie.vote.domain.member.domain.Candidate;
import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.member.repository.CandidateRepository;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import com.diggindie.vote.domain.vote.domain.PartVote;
import com.diggindie.vote.domain.vote.dto.PartVoteRequestDto;
import com.diggindie.vote.domain.vote.repository.PartVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartVoteExecutor {

    private final PartVoteRepository partVoteRepository;
    private final CandidateRepository candidateRepository;
    private final MemberRepository memberRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(String loginId, PartVoteRequestDto request) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 이미 투표했는지 확인
        if (partVoteRepository.existsByVoterId(member.getId())) {
            throw new IllegalStateException("이미 투표하셨습니다.");
        }

        // 후보자 조회
        Candidate candidate = candidateRepository.findById(request.candidateId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후보자입니다."));

        // 자기 자신 안됨
        if (candidate.getMember().getId().equals(member.getId())) {
            throw new IllegalStateException("자기 자신에게는 투표할 수 없습니다.");
        }

        // 같은 파트인지 확인
        if (member.getPart() != candidate.getMember().getPart()) {
            throw new IllegalStateException("같은 파트의 후보자에게만 투표할 수 있습니다.");
        }

        PartVote vote = PartVote.builder()
                .voter(member)
                .candidate(candidate)
                .build();

        partVoteRepository.save(vote);
    }
}