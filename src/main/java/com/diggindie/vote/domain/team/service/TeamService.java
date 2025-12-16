package com.diggindie.vote.domain.team.service;

import com.diggindie.vote.domain.team.domain.Team;
import com.diggindie.vote.domain.team.dto.TeamDto;
import com.diggindie.vote.domain.team.dto.TeamListResponse;
import com.diggindie.vote.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamListResponse getTeamList() {
        List<Team> teams = teamRepository.findAll();

        List<TeamDto> teamDtos = teams.stream()
                .map(team -> new TeamDto(
                        team.getId(),
                        team.getTeamName(),
                        team.getProposal()
                ))
                .toList();

        return new TeamListResponse(teamDtos);
    }
}

