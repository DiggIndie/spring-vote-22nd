package com.diggindie.vote.domain.vote.service;

import com.diggindie.vote.domain.team.domain.Team;
import com.diggindie.vote.domain.team.repository.TeamRepository;
import com.diggindie.vote.domain.vote.dto.TeamVoteResultDto;
import com.diggindie.vote.domain.vote.dto.TeamVoteResultResponse;
import com.diggindie.vote.domain.vote.repository.TeamVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamVoteService {

    private final TeamVoteRepository teamVoteRepository;
    private final TeamRepository teamRepository;

    public TeamVoteResultResponse getTeamVoteResults() {
        List<Team> teams = teamRepository.findAll();
        List<Object[]> voteCounts = teamVoteRepository.countVotesByTeam();

        Map<Long, Long> voteCountMap = voteCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        List<TeamVoteResultDto> teamVoteResults = teams.stream()
                .map(team -> new TeamVoteResultDto(
                        team.getId(),
                        team.getTeamName(),
                        team.getProposal(),
                        voteCountMap.getOrDefault(team.getId(), 0L)
                ))
                .toList();

        return new TeamVoteResultResponse(teamVoteResults);
    }
}

