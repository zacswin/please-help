package session;

import entity.Role;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import entity.User;
import entity.UserDTO;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;

@DeclareRoles({"administrator", "user", "readonly"})
@Stateless
public class UserManagement implements UserManagementRemote {

    @Resource
    SessionContext ctx;

    @EJB
    private UserFacadeLocal userFacade;

    @EJB
    private RoleFacadeLocal roleFacade;

    private User createUserFromDTO(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail(), userDTO.getPassword());
        Role role = roleFacade.find(userDTO.getRole().getId());
        user.setRole(role);

        return user;
    }

    private UserDTO createDTOFromUser(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPassword(), RoleManagement.createDTOFromRole(user.getRole()));
    }

    @Override
    @RolesAllowed("administrator")
    public boolean addUser(UserDTO userDTO) {
        User user = createUserFromDTO(userDTO);
        return userFacade.addUser(user);
    }

    @Override
    @RolesAllowed("administrator")
    public boolean editUser(UserDTO userDTO) {
        User user = createUserFromDTO(userDTO);
        return userFacade.updateUser(user);

    }

    @Override
    @RolesAllowed("administrator")
    public boolean deleteUser(Integer userId) {
        return userFacade.deleteUser(userId);
    }
    
    @Override
    @RolesAllowed("administrator")
    public List<UserDTO> findAll() {
        return userFacade.findAll().stream().map(this::createDTOFromUser).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed("administrator")
    public UserDTO find(Integer id) {
        return createDTOFromUser(userFacade.find(id));
    }

    @Override
    @RolesAllowed({"readonly", "user", "administrator"})
    public UserDTO getMe() {
        Principal callerPrincipal = ctx.getCallerPrincipal();
        String userId = callerPrincipal.getName();

        User user = userFacade.find(Integer.parseInt(userId));
        return createDTOFromUser(user);
    }

}
