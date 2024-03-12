package users;
import database.Database;

public class User {
    public final int userID;
    
    public User(int userID) {
        this.userID = userID;
    }

    public static User newGuest() {
        return new User(Database.getNewUserID());
    }

    public int getUserID() {
        return userID;
    }
}