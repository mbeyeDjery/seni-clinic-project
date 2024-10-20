package fr.seni.auth.services;


import fr.seni.auth.mappers.AppUserMapper;
import fr.seni.auth.repositories.AppUserRepository;
import fr.seni.core.dtos.AppUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserMapper appUserMapper;
    private final AppUserRepository appUserRepository;

    public AppUserDto create(AppUserDto appUser) {
        return appUserMapper.toDto(appUserRepository.save(appUserMapper.toEntity(appUser)));
    }

    public AppUserDto update(AppUserDto appUser) {
        return appUserMapper.toDto(appUserRepository.save(appUserMapper.toEntity(appUser)));
    }

    public void delete(AppUserDto appUser) {
        appUserRepository.delete(appUserMapper.toEntity(appUser));
    }

    public  List<AppUserDto> findAll(){
        return appUserRepository.findAll().stream().map(appUserMapper::toDto).collect(Collectors.toList());
    }

    public AppUserDto findOne(String userId){
        return appUserMapper.toDto(appUserRepository.findByIdUser(userId));
    }

    public AppUserDto findByUsername(String username){
        return appUserMapper.toDto(appUserRepository.findByUsername(username));
    }

    public AppUserDto findByEmail(String email){
        return appUserMapper.toDto(appUserRepository.findByEmail(email));
    }

    public AppUserDto findByTelephone(String telephone){
        return appUserMapper.toDto(appUserRepository.findByTelephone(telephone));
    }

    public  List<AppUserDto> findAllByEnabled(Boolean enabled){
        return appUserRepository.findByEnabled(enabled).stream().map(appUserMapper::toDto).collect(Collectors.toList());
    }
}
