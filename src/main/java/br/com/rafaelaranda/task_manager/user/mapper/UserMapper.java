package br.com.rafaelaranda.task_manager.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rafaelaranda.task_manager.user.dto.AuthenticationDTO;
import br.com.rafaelaranda.task_manager.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", expression = "java(Email.of(dto.email()))")
    @Mapping(target = "password", expression = "java(br.com.rafaelaranda.task_manager.user.vo.Password.fromPlainText(dto.password()))")
    UserEntity toEntity(AuthenticationDTO dto);

    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "password", ignore = true)
    AuthenticationDTO toDTO(UserEntity entity);
}
