package session;

import entity.Category;
import java.util.List;
import javax.ejb.Local;

@Local
public interface CategoryFacadeLocal {

    List<Category> findAll();

    Category find(int categoryId);

    boolean addCategory(Category category);

    boolean updateCategory(Category category);

    boolean deleteCategory(Integer categoryId);

}
