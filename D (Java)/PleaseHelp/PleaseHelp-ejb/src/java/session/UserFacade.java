package session;

import entity.RoleDTO;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import entity.User;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.persistence.Query;

@Stateless
public class UserFacade implements UserFacadeLocal {

    @PersistenceContext(unitName = "PleaseHelp-PersistenceUnit")
    private EntityManager em;

    public UserFacade() {
    }

    private void create(User entity) {
        em.persist(entity);
    }

    private void edit(User entity) {
        em.merge(entity);
    }

    private void remove(User entity) {
        em.remove(em.merge(entity));
    }

    private void updateGroup(User user, boolean isNew) {
        // Update the GROUPS table so when the user changes their role
        Query updateStatement = em.createNativeQuery(
                isNew
                        ? "INSERT INTO Groups (PRIVILEGE, ID) VALUES(?, ?)"
                        : "UPDATE Groups g SET g.PRIVILEGE = ? WHERE g.ID = ?");
        updateStatement.setParameter(1, user.getRole().getPrivilege());
        updateStatement.setParameter(2, user.getId());
        updateStatement.executeUpdate();
    }

    @Override
    public User find(int userId) {
        User user = em.find(User.class, userId);
        return user;
    }

    @Override
    public List<User> findAll() {
        Query query = em.createNamedQuery("User.findAll");

        List<User> users = query.getResultList();

        return users;
    }
//
//    @Override
//    public boolean hasUser(int userId) {
//        return find(userId) != null;
//    }

    @Override
    public boolean addUser(User user) {

        User e = find(user.getId());

        if (e != null) {
            return false;
        }

        try {
            // Perform the encryption
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(user.getPassword().getBytes("UTF-8"));
            byte[] digest = md.digest();

            // Encode the encrypted bytes as a hex string
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                String hex = Integer.toHexString(0xff & digest[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }

            user.setPassword(sb.toString());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException exception) {
            return false;
        }

        create(user);
        updateGroup(user, true);

        return true;
    }

    @Override
    public boolean updateUser(User user) {

        User e = find(user.getId());

        if (e == null) {
            return false;
        }

        edit(user);
        updateGroup(user, false);

        return true;
    }

    @Override
    public boolean deleteUser(int userId) {

        User e = find(userId);

        if (e == null) {
            return false;
        }

        remove(e);
        return true;
    }

}
