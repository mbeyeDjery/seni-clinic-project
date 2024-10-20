package fr.seni.auth.domain;

import fr.seni.auth.utils.Constantes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @Size(max = 254)
    @Column(name = "id_user", nullable = false, length = 254)
    private String idUser;

    @NotNull
    @Pattern(regexp = Constantes.LOGIN_REGEX)
    @Size(min = 4, max = 50)
    @Column(name = "username", nullable = false, length = 254)
    private String username;

    @Size(max = 50)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 254)
    private String firstName;

    @Size(max = 50)
    @NotNull
    @Column(name = "last_name", nullable = false, length = 254)
    private String lastName;

    @Email
    @Size(max = 254)
    @Column(name = "email", length = 254)
    private String email;

    @Size(max = 25)
    @Column(name = "telephone", length = 254)
    private String telephone;

    @Size(max = 254)
    @Column(name = "image_url", length = 254)
    private String imageUrl;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

}