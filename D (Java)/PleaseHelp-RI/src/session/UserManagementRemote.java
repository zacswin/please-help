package session;

import javax.ejb.Remote;
import entity.UserDTO;
import java.util.List;

@Remote
public interface UserManagementRemote {

    List<UserDTO> findAll();

    UserDTO find(Integer userId);

    boolean addUser(UserDTO userDTO);

    boolean editUser(UserDTO userDTO);

    boolean deleteUser(Integer userId);
    
    UserDTO getMe();
}
