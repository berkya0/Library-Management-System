package com.berkaykomur.mapper;

import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.RegisterRequest;
import com.berkaykomur.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    DtoUser toDtoUser(User user);

}
