import database.users.User;

public interface Page {
    void display(User user);
    void go(int option);
}