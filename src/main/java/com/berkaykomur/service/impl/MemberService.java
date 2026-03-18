package com.berkaykomur.service.impl;

import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.DtoMemberIU;
import com.berkaykomur.dto.UpdateRoleRequest;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.mapper.MemberMapper;
import com.berkaykomur.model.Member;
import com.berkaykomur.model.User;
import com.berkaykomur.repository.MemberRepository;
import com.berkaykomur.repository.UserRepository;
import com.berkaykomur.service.IMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements IMemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final MemberMapper memberMapper;

    @Override
    @Transactional(readOnly = true)
    public DtoMember findMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST,id.toString())));
        return memberMapper.toDtoMember(member);
    }
    @Override
    @PreAuthorize("hasRole('ADMIN') or #memberId==authentication.principal.memberId")
    public DtoMember updateMemberById(Long memberId, DtoMemberIU dtoMemberIU) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST,memberId.toString())));
        memberMapper.updateMemberFromDto(dtoMemberIU, member);
        return memberMapper.toDtoMember(memberRepository.save(member));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly=true)
    public List<DtoMember> findAllMembers() {
        return memberMapper.toDtoListMember(memberRepository.findAll());
    }
    @Override
    @Transactional(readOnly=true)
    @PreAuthorize("#username==authentication.principal.username")
    public DtoMember findMemberByUsername(String username) {
        User user = userRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST,username)));

        if (user.getMember() == null) {
            throw new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, "Kullanıcıya ait member bilgisi bulunamadı"));
        }
        return memberMapper.toDtoMember(user.getMember());
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public DtoMember updateMemberRole(Long id, UpdateRoleRequest role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, id.toString())));
        if (user.getMember() == null) {
            throw new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, "Kullanıcıya ait member bilgisi bulunamadı"));
        }
        user.setRole(role.getRole());
        userRepository.save(user);
        return memberMapper.toDtoMember(user.getMember());
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly=true)
    public Long findMemberIdByUsername(String username) {
        return memberRepository.findMemberIdByUsername(username)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessagesType.NO_RECORD_EXIST, "Üye bulunamadı: " + username)));
    }

}