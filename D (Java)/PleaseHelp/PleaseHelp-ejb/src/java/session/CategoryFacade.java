package session;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import entity.Category;
import java.util.List;
import javax.persistence.Query;

@Stateless
public class CategoryFacade implements CategoryFacadeLocal {

    @PersistenceContext(unitName = "PleaseHelp-PersistenceUnit")
    private EntityManager em;

    public CategoryFacade() {
    }

    private void create(Category entity) {
        em.persist(entity);
    }

    private void edit(Category entity) {
        em.merge(entity);
    }

    private void remove(Category entity) {
        em.remove(em.merge(entity));
    }

    private boolean hasCategory(int categoryId) {
        return find(categoryId) != null;
    }

    @Override
    public Category find(int categoryId) {
        return em.find(Category.class, categoryId);
    }

    @Override
    public List<Category> findAll() {
        Query query = em.createNamedQuery("Category.findAll");

        List<Category> categorys = query.getResultList();

        return categorys;
    }

    @Override
    public boolean addCategory(Category category) {
        if (hasCategory(category.getId())) {
            return false;
        }
        
        create(category);

        return true;
    }

    @Override
    public boolean updateCategory(Category category) {
        if (hasCategory(category.getId())) {
            return false;
        }

        edit(category);

        return true;
    }

    @Override
    public boolean deleteCategory(Integer categoryId) {
        Category e = find(categoryId);

        // If a ticket has this category, we cannot delete (due to foreign key violation)
        Query tickets = em.createNamedQuery("Ticket.findByCategory");
        tickets.setParameter("category", e);

        if (tickets.getResultList().size() > 0) {
            return false;
        }

        remove(e);

        return true;
    }

}
