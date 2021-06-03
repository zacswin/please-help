package session;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import entity.Category;
import entity.CategoryDTO;
import entity.Role;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;

@DeclareRoles({"administrator", "user", "readonly"})
@Stateless
public class CategoryManagement implements CategoryManagementRemote {
    
    @EJB
    private CategoryFacadeLocal categoryFacade;

    public static Category createCategoryFromDTO(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }
        Category category = new Category(categoryDTO.getId(), categoryDTO.getName(), categoryDTO.getColour());
        List<Role> roles = categoryDTO.getRoles().stream().map(RoleManagement::createRoleFromDTO).collect(Collectors.toList());
        category.setRoleList(roles);
        return category;
    }

    public static CategoryDTO createDTOFromCategory(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getColour(),
                category.getRoleList().stream().map(RoleManagement::createDTOFromRole).collect(Collectors.toList()));
    }

    @Override
    public List<CategoryDTO> findAll() {
        return categoryFacade.findAll().stream().map(CategoryManagement::createDTOFromCategory).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed("administrator")
    public CategoryDTO find(Integer id) {
        return createDTOFromCategory(categoryFacade.find(id));
    }

    @Override
    @RolesAllowed("administrator")
    public boolean addCategory(CategoryDTO categoryDTO) {
        Category category = createCategoryFromDTO(categoryDTO);
        return categoryFacade.addCategory(category);
    }

    @Override
    @RolesAllowed("administrator")
    public boolean editCategory(CategoryDTO categoryDTO) {
        Category category = createCategoryFromDTO(categoryDTO);
        return categoryFacade.updateCategory(category);
    }

    @Override
    @RolesAllowed("administrator")
    public boolean deleteCategory(Integer categoryId) {
        return categoryFacade.deleteCategory(categoryId);
    }

}
