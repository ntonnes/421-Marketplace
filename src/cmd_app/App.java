package cmd_app;
import java.util.Scanner;
import java.util.Stack;

import database.Customer;
import database.User;

public class App {
    final static Scanner scanner = new Scanner(System.in);
    private static Stack<Page> history = new Stack<Page>();
    private static Page currentPage;
    private static User currentUser;

    public void run(){
        while (true) {
            displayBanner();
            currentPage.display(currentUser);
            goOption();
        }
        
    }

    public static void main(String[] args) {
        App app = new App();
        currentUser = new User();
        push(new HomePage(currentUser));
        app.run();
    }

    public static void push(Page page) {
        currentPage = page;
        history.push(currentPage);
    }

    public static void pop() {
        if (history.size() > 0) {
            currentPage = history.pop();
        }
    }

    public static void goHome() {
        while (!history.isEmpty()) {
            pop();
        }
        push(new HomePage(currentUser));
    }

    public static User getUser() {
        return currentUser;
    }

    public void goOption() {
        
        final String prompt = "\nSelect an option: ";
        System.out.print(prompt);

        while (!scanner.hasNextInt()) {
            System.out.println("That's not a valid option! Try again.");
            System.out.print(prompt);
            scanner.next();
        }

        int option = scanner.nextInt();
        switch (option) {
            case 0:
                if (currentUser instanceof Customer) {
                    logout();
                }
                System.out.println("Thank you for shopping with us! Goodbye!");
                System.exit(0);
                break;

            case 6:
                pop();
                break;

            case 7:
                goHome();
                break;

            case 8:
                if (currentUser instanceof Customer) {
                    new AccountPage().display(currentUser);
                } else {
                    push(new LoginPage());
                }
                break;
            
            case 9:
                if (currentUser instanceof Customer) {
                    logout();
                } else {
                    push(new SignupPage());
                }
                break;

            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                currentPage.go(option);
                break;
            
            default:
                System.out.println("Invalid option. Try again.");
                goOption();
        }
    }

    public void close() {
        scanner.close();
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void login(User user) {
        setCurrentUser(user);
        goHome();
    }

    public static void logout() {
        setCurrentUser(new User());
        System.out.println("You have been logged out.");
        goHome();
    }

    public void displayBanner() {

        System.out.println("\n\nWelcome to the 421 MarketPlace!\n");

        if (currentUser instanceof Customer) {
            Customer customer = (Customer) currentUser;
            String name = customer.getName();
            System.out.println("(6) Back | (7) Home | Hello, " + name + "! | (8) Account | (9) Logout | (0) Exit");
        
        } else {
            System.out.println("(6) Back | (7) Home | (8) Login | (9) Sign up | (0) Exit");
        }

        System.out.println("--------------------------------------------------");
        System.out.println();
    }
}