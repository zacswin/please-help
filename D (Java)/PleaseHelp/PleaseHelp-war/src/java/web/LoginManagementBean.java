package web;

import entity.UserDTO;
import javax.inject.Named;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import session.UserManagementRemote;

@Named(value = "loginManagementBean")
@RequestScoped
public class LoginManagementBean implements Serializable {

    private static final String LOGOUT = "logout";

    private UserDTO me;

    @EJB
    private UserManagementRemote userManagement;

    public LoginManagementBean() {
    }

    @PostConstruct
    public void loadData() {
        me = userManagement.getMe();
    }

    public String logOut() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        try {
            request.logout();

        } catch (Exception ex) {
        }

        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        session.invalidate();

        me = null;
        return LOGOUT;
    }

    public UserDTO getMe() {
        return me;
    }

    public void setMe(UserDTO me) {
        this.me = me;
    }

}
