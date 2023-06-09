package com.tave.mapper.admin;

import com.tave.constant.ScoreType;
import com.tave.domain.member.MemberEntity;
import com.tave.domain.admin.MemberScoreNoteEntity;
import com.tave.dto.admin.MemberScoreNoteDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring", // 빌드 시 구현체 만들고 빈으로 등록
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, // 생성자 주입 전략
        unmappedTargetPolicy = ReportingPolicy.ERROR // 일치하지 않는 필드가 있으면 빌드 시 에러
)
public interface MemberScoreNoteMapper {

    @Mappings({
            @Mapping(source = "member",target = "member"),
            @Mapping(target = "id",ignore = true),
            @Mapping(target = "createAt",ignore = true),
            @Mapping(target = "modifiedAt",ignore = true)
    })
    MemberScoreNoteEntity toEntity(MemberScoreNoteDto.MemberScoreNotePostDto memberScoreNotePostDto,MemberEntity member);



    @Mapping(source = "member.id",target = "memberId")
    MemberScoreNoteDto.MemberScoreNoteResponseDto toResponseDto(MemberScoreNoteEntity memberScoreNoteEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "member",ignore = true),
            @Mapping(target = "createAt",ignore = true),
            @Mapping(target = "modifiedAt",ignore = true)
    })
    public void updateFromPatchDto(MemberScoreNoteDto.MemberScoreNotePatchDto memberScoreNotePatchDto, @MappingTarget MemberScoreNoteEntity memberScoreNoteEntity);

}
