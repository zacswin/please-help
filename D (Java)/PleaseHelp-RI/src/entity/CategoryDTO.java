package entity;

import java.io.Serializable;
import java.util.List;

public class CategoryDTO implements Serializable {

    private Integer id;
    private String name;
    private String colour;
    private List<RoleDTO> roles;

    public CategoryDTO(Integer id, String name, String colour, List<RoleDTO> roles) {
        this.id = id;
        this.name = name;
        this.colour = colour;
        this.roles = roles;
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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

}
