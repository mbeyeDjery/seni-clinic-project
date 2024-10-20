package fr.seni.auth.repositories;

import fr.seni.auth.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

    AppUser findByEmail(String email);
    AppUser findByIdUser(String idUser);
    AppUser findByUsername(String username);
    AppUser findByTelephone(String telephone);
    List<AppUser> findByEnabled(Boolean enabled);
}