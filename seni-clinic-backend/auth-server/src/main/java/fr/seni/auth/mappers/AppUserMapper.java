package fr.seni.auth.mappers;

import fr.seni.auth.domain.AppUser;
import fr.seni.core.dtos.AppUserDto;
import fr.seni.core.dtos.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper extends EntityMapper<AppUserDto, AppUser> {
}
