package cmd_app;
import database.User;

public interface Page {
    void display(User user);
    void go(int option);
}