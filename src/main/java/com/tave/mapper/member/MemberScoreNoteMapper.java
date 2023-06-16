package com.tave.mapper.member;

import com.tave.constant.Type;
import com.tave.domain.member.MemberEntity;
import com.tave.domain.member.MemberScoreNoteEntity;
import com.tave.dto.member.MemberScoreNoteDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring", // 빌드 시 구현체 만들고 빈으로 등록
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, // 생성자 주입 전략
        unmappedTargetPolicy = ReportingPolicy.ERROR // 일치하지 않는 필드가 있으면 빌드 시 에러
)
public interface MemberScoreNoteMapper {

    @Mappings({
            @Mapping(source = "memberScoreNotePostDto.type",target = "type",qualifiedByName = "toType"),
            @Mapping(source = "member",target = "member"),
            @Mapping(target = "id",ignore = true)
    })
    MemberScoreNoteEntity toEntity(MemberScoreNoteDto.MemberScoreNotePostDto memberScoreNotePostDto,MemberEntity member);

    @Named("toType")
    public static Type toType(Type type) {
        return type;
    }


    @Mapping(source = "member.id",target = "memberId")
    MemberScoreNoteDto.MemberScoreNoteResponseDto toResponseDto(MemberScoreNoteEntity memberScoreNoteEntity);
}
