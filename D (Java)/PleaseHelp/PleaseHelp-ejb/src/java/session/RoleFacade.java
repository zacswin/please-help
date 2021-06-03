package session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import entity.Role;
import entity.User;
import java.util.List;
import javax.persistence.Query;

@Stateless
public class RoleFacade implements RoleFacadeLocal {

    @PersistenceContext(unitName = "PleaseHelp-PersistenceUnit")
    private EntityManager em;

    public RoleFacade() {
    }

    private void create(Role entity) {
        em.persist(entity);
    }

    private void edit(Role entity) {
        em.merge(entity);
    }

    private void remove(Role entity) {
        em.remove(em.merge(entity));
    }

    @Override
    public Role find(int roleId) {
        return em.find(Role.class, roleId);
    }

    @Override
    public List<Role> findAll() {
        Query query = em.createNamedQuery("Role.findAll");

        List<Role> roles = query.getResultList();

        return roles;
    }

    @Override
    public List<Role> findAllForCategorySelection() {
        Query query = em.createNamedQuery("Role.findByNotPrivilege");
        query.setParameter("privilege", "administrator");

        List<Role> roles = query.getResultList();

        return roles;
    }

    @Override
    public boolean hasRole(int roleId) {
        return find(roleId) != null;
    }

    @Override
    public boolean addRole(Role role) {
        // check again - just to play it safe
        Role e = find(role.getId());

        if (e != null) {
            // could not add one
            return false;
        }
        create(role);

        return true;
    }

    @Override
    public boolean updateRole(Role role) {
        Role e = find(role.getId());

        if (e == null) {
            return false;
        }

        edit(role);
        // Update the GROUPS table so when users login they have the new user group
        Query users = em.createNamedQuery("User.findByRole");
        users.setParameter("role", e);

        Query updateStatement = em.createNativeQuery("UPDATE Groups g SET g.PRIVILEGE = ? WHERE g.ID = ?");
        updateStatement.setParameter(1, e.getPrivilege());
        for (Object user : users.getResultList()) {
            updateStatement.setParameter(2, ((User) user).getId());
            updateStatement.executeUpdate();
        }

        return true;
    }

    @Override
    public boolean deleteRole(Integer roleId) {

        Role e = find(roleId);
        // If a user has this role, we cannot delete (due to foreign key violation)
        Query users = em.createNamedQuery("User.findByRole");
        users.setParameter("role", e);

        if (users.getResultList().size() > 0) {
            return false;
        }

        if (e == null) {
            return false;
        }

        remove(e);

        return true;
    }

    @Override
    public List<Role> findFindRolesForCategory(Integer categoryId) {
        Query query = em.createNamedQuery("Role.findAll");

        List<Role> roles = query.getResultList();

        return roles;
    }

}
