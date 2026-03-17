package com.berkaykomur.service;

import java.util.List;

import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.DtoMemberIU;
import com.berkaykomur.dto.UpdateRoleRequest;
import com.berkaykomur.enums.Role;

public interface IMemberService {

    DtoMember findMemberById(Long id);
    DtoMember updateMemberById(Long id, DtoMemberIU dtoMemberIU);
    List<DtoMember> findAllMembers();
    DtoMember findMemberByUsername(String username);
    DtoMember updateMemberRole(Long id, UpdateRoleRequest newRole);
    Long findMemberIdByUsername(String username);
}