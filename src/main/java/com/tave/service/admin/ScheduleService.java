package com.tave.service.admin;

import com.tave.domain.admin.AdminEntity;
import com.tave.domain.admin.ScheduleEntity;
import com.tave.dto.admin.ScheduleDto;
import com.tave.exception.BusinessLogicException;
import com.tave.exception.ExceptionCode;
import com.tave.mapper.admin.ScheduleMapper;
import com.tave.repository.admin.AdminRepository;
import com.tave.repository.admin.ScheduleRepository;
import com.tave.repository.member.MemberRepository;
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
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final ScheduleMapper scheduleMapper;

    @Transactional
    public ScheduleDto.ScheduleResponseDto createSchedule(Long adminId, ScheduleDto.SchedulePostDto schedulePostDto) {
        AdminEntity adminEntity = adminRepository.findById(adminId).orElseThrow();
        return scheduleMapper.toResponseDto(scheduleRepository.save(scheduleMapper.toEntity(schedulePostDto, adminEntity)));
    }

    public ScheduleDto.ScheduleResponseDto getSchedule(Long scheduleId) {
        return scheduleMapper.toResponseDto(scheduleRepository.findById(scheduleId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.SCHEDULE_IS_NOT_EXIST)));
    }

    @Transactional
    public ScheduleDto.ScheduleResponseDto updateSchedule(ScheduleDto.SchedulePatchDto schedulePatchDto) {
        ScheduleEntity scheduleEntity = scheduleRepository.findById(schedulePatchDto.getId())
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.SCHEDULE_IS_NOT_EXIST));
        scheduleMapper.updateFromPatchDto(schedulePatchDto, scheduleEntity);
        //entity->dto 후 return
        return scheduleMapper.toResponseDto(scheduleRepository.findById(schedulePatchDto.getId())
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.UPDATE_FAIL_SCHEDULE)));
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
        log.info("ScheduleEntity Id: {} is deleted", scheduleId);
    }

    @Transactional
    public void addAttendanceMemberId(Long scheduleId, Long memberId) throws EntityNotFoundException {
        memberRepository.findById(memberId).orElseThrow(()->new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        scheduleRepository.findById(scheduleId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.SCHEDULE_IS_NOT_EXIST)).addAttendanceMemberId(memberId);
        log.info("Member Id: {} is added in Schedule Id: {}", memberId, scheduleId);
    }

    @Transactional
    public List<ScheduleDto.ScheduleResponseDto> getAllSchedule() {

        List<ScheduleEntity> scheduleEntities = scheduleRepository.getAllSchedule();
        List<ScheduleDto.ScheduleResponseDto> scheduleResponseDtos = new ArrayList<>();

        for (ScheduleEntity scheduleEntity : scheduleEntities) {
            ScheduleDto.ScheduleResponseDto scheduleResponseDto = scheduleMapper.toResponseDto(scheduleEntity);
            scheduleResponseDtos.add(scheduleResponseDto);
        }

        return scheduleResponseDtos;
    }
}

