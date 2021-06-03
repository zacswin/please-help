package session;

import entity.Role;
import java.util.List;
import javax.ejb.Local;

@Local
public interface RoleFacadeLocal {

    List<Role> findAll();
    
    List<Role> findAllForCategorySelection();
    
    List<Role> findFindRolesForCategory(Integer categoryId);

    Role find(int roleId);

    boolean hasRole(int roleId);

    boolean addRole(Role role);

    boolean updateRole(Role role);

    boolean deleteRole(Integer roleId);

}
