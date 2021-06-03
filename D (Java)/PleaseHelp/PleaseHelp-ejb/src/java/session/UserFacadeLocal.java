package session;

import entity.User;
import java.util.List;
import javax.ejb.Local;

@Local
public interface UserFacadeLocal {

    List<User> findAll();

    User find(int userId);

//    boolean hasUser(int userId);

    boolean addUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(int userId);

}
