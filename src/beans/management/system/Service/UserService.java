package beans.management.system.Service;

import beans.management.system.DAO.UserDAO;
import beans.management.system.Model.User;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public User login(String email, String password) {
        return userDAO.authenticateUser(email, password);
    }
}
