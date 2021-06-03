package web;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import entity.RoleDTO;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import session.RoleManagementRemote;

@Named(value = "roleManagementBean")
@Stateless
public class RoleManagementBean implements Serializable {

    @EJB
    private RoleManagementRemote roleManagement;

    private List<RoleDTO> roles;

    private Integer id;
    private String name;
    private String privilege;
    private String saveResult;

    public RoleManagementBean() {
        clearFields();
    }

    private void clearFields() {
        id = 1;
        name = "";
        privilege = "";
        saveResult = "";
    }

    @PostConstruct
    private void loadData() {
        roles = roleManagement.findAll();
    }

    private void updateRoleDetails(RoleDTO role) {
        clearFields();
        id = role.getId();
        name = role.getName();
        privilege = role.getPrivilege();
    }

    public String renderEditRole(Integer roleId) {
        RoleDTO role = roleManagement.find(roleId);
        if (role == null) {
            return "failure";
        }

        updateRoleDetails(role);
        return "success";
    }

    public String renderDeleteRole(Integer roleId) {
        RoleDTO role = roleManagement.find(roleId);
        if (role == null) {
            return "failure";
        }

        updateRoleDetails(role);

        return "success";
    }

    public String renderNewRole() {
        clearFields();
        return "success";
    }

    public String editRole() {
        if (roleManagement.editRole(new RoleDTO(id, name, privilege))) {
            saveResult = "success";
            loadData();
        } else {
            saveResult = "failure";
        }

        return saveResult;
    }

    public String newRole() {
        if (roleManagement.addRole(new RoleDTO(id, name, privilege))) {
            saveResult = "success";
            loadData();
        } else {
            saveResult = "failure";
        }

        return saveResult;
    }

    public String deleteRole() {
        if (roleManagement.deleteRole(id)) {
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

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public String getSaveResult() {
        return saveResult;
    }
}
