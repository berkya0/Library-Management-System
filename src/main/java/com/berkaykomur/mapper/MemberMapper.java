package com.berkaykomur.mapper;

import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.DtoMemberIU;
import com.berkaykomur.model.Member;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",uses = {LoanMapper.class, UserMapper.class})
public interface MemberMapper {
    DtoMember toDtoMember(Member member);
    List<DtoMember> toDtoListMember(List<Member> members);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMemberFromDto(DtoMemberIU memberIU, @MappingTarget Member member);

}

