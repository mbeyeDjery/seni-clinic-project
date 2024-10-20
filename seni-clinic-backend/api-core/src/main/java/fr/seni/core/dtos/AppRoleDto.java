package fr.seni.core.dtos;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppRoleDto {
    private String roleId;
    private String roleName;
}
