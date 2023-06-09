package com.tave.service.team;

import com.tave.domain.admin.AdminEntity;
import com.tave.domain.team.TeamEntity;
import com.tave.dto.team.TeamDto;
import com.tave.exception.BusinessLogicException;
import com.tave.exception.ExceptionCode;
import com.tave.mapper.team.TeamMapper;
import com.tave.repository.admin.AdminRepository;
import com.tave.repository.team.TeamRepository;
import com.tave.repository.admin.TeamScoreNoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final AdminRepository adminRepository;
    private final TeamScoreNoteRepository teamScoreNoteRepository;

    private final TeamMapper teamMapper;

    @Transactional
    public TeamDto.TeamResponseDto createTeam(TeamDto.TeamPostDto teamPostDto) {
        AdminEntity adminEntity = null;
        if(teamPostDto.getAdminId()!=null)
            adminEntity = adminRepository.findById(teamPostDto.getAdminId())
                    .orElseThrow(()->new BusinessLogicException(ExceptionCode.CREATE_FAIL_TEAM));
        return teamMapper.toResponseDto(teamRepository.save(teamMapper.toEntity(teamPostDto, adminEntity)));
    }

    public TeamDto.TeamResponseDto getTeam(Long teamId) {
        return teamMapper.toResponseDto(teamRepository.findById(teamId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.TEAM_NOT_FOUND)));
    }

    @Transactional
    public TeamDto.TeamResponseDto updateTeam(TeamDto.TeamPatchDto teamPatchDto) {
        TeamEntity teamEntity = teamRepository.findById(teamPatchDto.getId())
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.TEAM_NOT_FOUND));
        AdminEntity adminEntity = null;
        if(teamPatchDto.getAdminId()!=null)
            adminEntity = adminRepository.findById(teamPatchDto.getAdminId())
                    .orElseThrow(()->new BusinessLogicException(ExceptionCode.ADMIN_NOT_FOUND));
        //update
        teamMapper.updateFromPatchDto(teamPatchDto, adminEntity, teamEntity);
        //entity->dto 후 return
        return teamMapper.toResponseDto(teamRepository.findById(teamPatchDto.getId())
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.UPDATE_FAIL_TEAM)));
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        teamRepository.deleteById(teamId);
        log.info("TeamEntity Id: {} is deleted", teamId);
    }

    @Transactional
    public Integer getTeamScore(Long teamId) {
        if (!teamRepository.existsById(teamId))
            throw new EntityNotFoundException("TeamId: " + teamId + " not exists!!");
        return teamScoreNoteRepository.getTeamScoreByTeamId(teamId).orElse(0);
    }

    @Transactional
    public List<TeamDto.TeamResponseDto> getAllTeam(){

        List<TeamEntity> teamEntities = teamRepository.getAllTeam();
        List<TeamDto.TeamResponseDto> teamResponseDtos = new ArrayList<>();

        for (TeamEntity teamEntity : teamEntities){
            TeamDto.TeamResponseDto teamResponseDto = teamMapper.toResponseDto(teamEntity);
            teamResponseDtos.add(teamResponseDto);
        }

        return teamResponseDtos;
    }
}
