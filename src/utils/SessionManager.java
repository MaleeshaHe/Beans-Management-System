package utils;  // Declare the package at the top
import beans.management.system.Model.User;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



public class SessionManager {

    private static User currentUser; // Store the current logged-in user

    // Set the current user after successful login
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Get the current logged-in user
    public static User getCurrentUser() {
        return currentUser;
    }
    
    // Get the user_id of the logged-in user
    public static int getCurrentUserId() {
        if (currentUser != null) {
            return currentUser.getUserId();  // Assuming User class has a getUserId() method
        }
        return -1;  // Return -1 if no user is logged in
    }

    // Clear the session data (log out)
    public static void logout() {
        currentUser = null;  // Clear the current user
    }

    // Check if there is a logged-in user
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Optionally, save the session data to a file (for persistence between application restarts)
    public static void saveSessionToFile() {
        User currentUser = getCurrentUser();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("session.dat"))) {
            out.writeObject(currentUser);  // Save the current user object to a file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Optionally, load session data from file when the application starts
    public static void loadSessionFromFile() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("session.dat"))) {
            User user = (User) in.readObject();  // Load the saved user object from the file
            setCurrentUser(user);  // Set the loaded user as the current user
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

