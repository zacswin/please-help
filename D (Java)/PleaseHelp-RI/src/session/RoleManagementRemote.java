package session;

import javax.ejb.Remote;
import entity.RoleDTO;
import java.util.List;

@Remote
public interface RoleManagementRemote {

    List<RoleDTO> findAll();
    
    List<RoleDTO> findAllForCategorySelection();

    RoleDTO find(Integer roleId);

    boolean addRole(RoleDTO roleDTO);

    boolean editRole(RoleDTO roleDTO);

    boolean deleteRole(Integer roleId);

}
