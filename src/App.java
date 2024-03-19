import java.util.Stack;

import database.users.Customer;
import database.users.User;

public class App {
    private Customer user;
    private static Stack<Page> history = new Stack<Page>();
    private static Page currentPage;
    private User currentUser;

    public void run(){
        while (true) {
            Banner.display(currentUser);
            currentPage.display();
        }
    }

    public static void main(String[] args) {
        App app = new App();
        currentPage = new HomePage();
        app.run();
    }

    public static void push(Page page) {
        history.push(currentPage);
        currentPage = page;
    }

    public void pop() {
        currentPage = history.pop();
    }

    public void goHome() {
        while (!history.isEmpty()) {
            pop();
        }
    }

}