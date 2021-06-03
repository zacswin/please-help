package session;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import entity.Role;
import entity.RoleDTO;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;

@DeclareRoles({"administrator", "user", "readonly"})
@Stateless
public class RoleManagement implements RoleManagementRemote {

    @Resource
    SessionContext ctx;

    @EJB
    private RoleFacadeLocal roleFacade;

    public static Role createRoleFromDTO(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }
        return new Role(roleDTO.getId(), roleDTO.getName(), roleDTO.getPrivilege());
    }

    public static RoleDTO createDTOFromRole(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleDTO(role.getId(), role.getName(), role.getPrivilege());
    }

    @Override
    @RolesAllowed("administrator")
    public List<RoleDTO> findAll() {
        return roleFacade.findAll().stream().map(RoleManagement::createDTOFromRole).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed("administrator")
    public List<RoleDTO> findAllForCategorySelection() {
        return roleFacade.findAllForCategorySelection().stream().map(RoleManagement::createDTOFromRole).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed("administrator")
    public RoleDTO find(Integer id) {
        return createDTOFromRole(roleFacade.find(id));
    }

    @Override
    @RolesAllowed("administrator")
    public boolean addRole(RoleDTO roleDTO) {
        Role role = createRoleFromDTO(roleDTO);
        return roleFacade.addRole(role);
    }

    @Override
    @RolesAllowed("administrator")
    public boolean editRole(RoleDTO roleDTO) {
        Role role = createRoleFromDTO(roleDTO);
        return roleFacade.updateRole(role);
    }

    @Override
    @RolesAllowed("administrator")
    public boolean deleteRole(Integer roleId) {
        return roleFacade.deleteRole(roleId);
    }

}
