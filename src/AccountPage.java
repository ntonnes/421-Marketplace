import java.io.IOException;

import database.users.Customer;
import database.users.User;
import jline.console.ConsoleReader;

public class AccountPage implements Page {
    private ConsoleReader console;
    
    AccountPage() {
    }

    @Override
    public void display(User user) {
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            String name = customer.getName();
            System.out.println("Hello, " + name + "!");
            System.out.println("Account Information:");
            System.out.println("UserID:" + customer.getUserID());
            System.out.println("Name: " + customer.getName());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Password: " + customer.getPassword());
            System.out.println("Date of Birth: " + customer.getDob());
            System.out.println("----------------------------------");
            System.out.println("Account Options:");
            System.out.println("\t(1) Change Name\n\t(2) Change Email\n\t(3) Change Password");
        } else {
            System.out.println("You must be logged in to view this page.");
        }
    }

    @Override
    public void go(int option) {
        switch(option) {
            case 1:
            case 2:
            case 3:
                changeInfo(option);
                break;
            default:
                System.out.println("Invalid option. Try again.");
        }
    }

    private void changeInfo(int option) {
        try {
            console = new ConsoleReader();
            System.out.println("Enter new " + (option == 1 ? "name: " : (option == 2 ? "email: " : "password: ")));
            String newInfo = console.readLine();
            if (newInfo.isEmpty()) {
                System.out.println("Invalid input. Try again.");
                return;
            }
            Customer customer = (Customer) App.getUser();
            switch(option) {
                case 1:
                    customer.setName(newInfo);
                    break;
                case 2:
                    customer.setEmail(newInfo);
                    break;
                case 3:
                    customer.setPassword(newInfo);
                    break;
            }
            System.out.println("Your " + (option == 1 ? "name" : (option == 2 ? "email" : "password")) + " has been updated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
