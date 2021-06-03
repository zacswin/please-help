package web;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import entity.CategoryDTO;
import entity.RoleDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import session.CategoryManagementRemote;
import session.RoleManagementRemote;

@Named(value = "categoryManagementBean")
@Stateless
public class CategoryManagementBean implements Serializable {

    @EJB
    private CategoryManagementRemote categoryManagement;

    @EJB
    private RoleManagementRemote roleManagement;

    private List<CategoryDTO> categories;
    private List<RoleDTO> roles;

    private Integer id;
    private String name;
    private String colour;
    private List<RoleDTO> selectedRoles;
    private String saveResult;

    // This class allows for comparison between the 'roles' list (containing ALL roles available)
    // and the 'selectedRoles' list (containing the current selection, pre-populated from the database)
    @FacesConverter(value = "roleConverter")
    public static class RoleConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext,
                UIComponent component, String value) {
            return new RoleDTO(Integer.parseInt(value), "", "");
        }

        @Override
        public String getAsString(FacesContext facesContext,
                UIComponent component, Object object) {
            return ((RoleDTO) object).getId().toString();
        }
    }

    public CategoryManagementBean() {
        selectedRoles = new ArrayList<RoleDTO>();
        clearFields();
    }

    private void clearFields() {
        id = 1;
        name = "";
        colour = "";
        saveResult = "";
        selectedRoles.clear();
    }

    @PostConstruct
    private void loadData() {
        categories = categoryManagement.findAll();
        roles = roleManagement.findAllForCategorySelection();
    }

    private void updateCategoryDetails(CategoryDTO category) {
        clearFields();
        id = category.getId();
        name = category.getName();
        colour = category.getColour();
        selectedRoles = category.getRoles();
    }

    public String renderNewCategory() {
        clearFields();
        return "success";
    }
    
    public String renderEditCategory(Integer categoryId) {
        CategoryDTO category = categoryManagement.find(categoryId);
        if (category == null) {
            return "failure";
        }

        updateCategoryDetails(category);
        return "success";
    }

    public String renderDeleteCategory(Integer categoryId) {
        CategoryDTO category = categoryManagement.find(categoryId);
        if (category == null) {
            return "failure";
        }

        updateCategoryDetails(category);

        return "success";
    }

    public String editCategory() {
        if (categoryManagement.editCategory(new CategoryDTO(id, name, colour, selectedRoles))) {
            saveResult = "success";
            loadData();
        } else {
            saveResult = "failure";
        }

        return saveResult;
    }

    public String newCategory() {
        if (categoryManagement.addCategory(new CategoryDTO(id, name, colour, selectedRoles))) {
            saveResult = "success";
            loadData();
        } else {
            saveResult = "failure";
        }

        return saveResult;
    }

    public String deleteCategory() {
        if (categoryManagement.deleteCategory(id)) {
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

    public List<CategoryDTO> getCategorys() {
        return categories;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public List<RoleDTO> getRoles() {
        System.out.println(roles);
        return roles;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public List<RoleDTO> getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(List<RoleDTO> selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public String getSaveResult() {
        return saveResult;
    }
}
