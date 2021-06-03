package web;

import entity.RoleDTO;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import entity.UserDTO;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import session.RoleManagementRemote;
import session.UserManagementRemote;

@Named(value = "userManagementBean")
@Stateless
public class UserManagementBean implements Serializable {

    @EJB
    private UserManagementRemote userManagement;

    @EJB
    private RoleManagementRemote roleManagement;

    private List<UserDTO> users;
    private List<RoleDTO> roles;

    private Integer id;
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private Integer roleId;
    private String saveResult;

    public UserManagementBean() {
        clearFields();
    }

    private void clearFields() {
        id = 1;
        name = "";
        email = "";
        password = "";
        saveResult = "";
    }

    public void validatePasswordPair(FacesContext context,
            UIComponent componentToValidate,
            Object pwdValue) throws ValidatorException {

        // get password
        String pwd = (String) pwdValue;

        // get confirm password
        UIInput cnfPwdComponent = (UIInput) componentToValidate.getAttributes().get("confirmPassword");
        String cnfPwd = (String) cnfPwdComponent.getSubmittedValue();

        if (!pwd.equals(cnfPwd)) {
            FacesMessage message = new FacesMessage(
                    "Password and Confirm Password must match");
            context.addMessage("confirmPassword", message);
        }
    }

    @PostConstruct
    private void loadData() {
        users = userManagement.findAll();
        roles = roleManagement.findAll();
    }

    private void updateUserDetails(UserDTO user) {
        clearFields();
        id = user.getId();
        name = user.getName();
        email = user.getEmail();
        password = user.getPassword();
        roleId = user.getRole().getId();
    }

    public String renderEditUser(Integer userId) {
        UserDTO user = userManagement.find(userId);
        if (user == null) {
            return "failure";
        }
        updateUserDetails(user);
        return "success";
    }

    public String renderDeleteUser(Integer userId) {
        UserDTO user = userManagement.find(userId);
        if (user == null) {
            return "failure";
        }

        updateUserDetails(user);

        return "success";
    }

    public String renderNewUser() {
        clearFields();
        return "success";
    }

    public String editUser() {
        if (userManagement.editUser(new UserDTO(id, name, email, password, new RoleDTO(roleId, "", "")))) {
            saveResult = "success";
            loadData();
        } else {
            saveResult = "failure";
        }

        return saveResult;
    }

    public String newUser() {
        if (userManagement.addUser(new UserDTO(id, name, email, password, new RoleDTO(roleId, "", "")))) {
            saveResult = "success";
            loadData();
        } else {
            saveResult = "failure";
        }

        return saveResult;
    }

    public String deleteUser() {
        if (userManagement.deleteUser(id)) {
            saveResult = "success";
            loadData();
        } else {
            saveResult = "failure";
        }

        return saveResult;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public String getSaveResult() {
        return saveResult;
    }
}
