package com.berkaykomur.service.impl;

import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.DtoUserIU;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.mapper.UserMapper;
import com.berkaykomur.model.User;
import com.berkaykomur.repository.MemberRepository;
import com.berkaykomur.repository.UserRepository;
import com.berkaykomur.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN') or #dtoUserIU.username==authentication.principal.username")
    public DtoUser updateUser(DtoUserIU dtoUserIU) {
        User user = userRepository.findByUsername(dtoUserIU.getUsername())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST, dtoUserIU.getUsername())));

        if (dtoUserIU.getPassword() != null && !dtoUserIU.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dtoUserIU.getPassword()));
        }
        if (dtoUserIU.getRole() != null) {
            user.setRole(dtoUserIU.getRole());
        }
        userRepository.save(user);
        return userMapper.toDtoUser(user);
    }
    @Override
    @PreAuthorize("hasRole('ADMIN') or #id==authentication.principal.userId")
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, id.toString())));

        if (user.getMember() != null) {
            boolean hasActiveLoans = user.getMember().getLoans().stream()
                    .anyMatch(loan -> loan.getReturnDate() == null);

            if (hasActiveLoans) {
                throw new BaseException(new ErrorMessage(MessagesType.GENERAL_EXCEPTION,
                        "Ödünç alınmış kitapları olan kullanıcı silinemez. Önce kitapları iade etmelisiniz."));
            }
            memberRepository.delete(user.getMember());
        }
        userRepository.delete(user);
    }
    private DtoUser convertToDto(User user) {
        DtoUser dto = new DtoUser();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

}