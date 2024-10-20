package fr.seni.core.dtos;


import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto implements Serializable {

    private String idUser;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private String imageUrl;
    private Boolean enabled;
    private Set<AppRoleDto> roles;
}
