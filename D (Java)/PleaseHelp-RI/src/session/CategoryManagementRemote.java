package session;

import javax.ejb.Remote;
import entity.CategoryDTO;
import java.util.List;

@Remote
public interface CategoryManagementRemote {

    List<CategoryDTO> findAll();

    CategoryDTO find(Integer categoryId);

    boolean addCategory(CategoryDTO categoryDTO);

    boolean editCategory(CategoryDTO categoryDTO);

    boolean deleteCategory(Integer categoryId);

}
