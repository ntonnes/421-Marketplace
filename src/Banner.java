import database.users.*;

public class Banner {
    public static void display(User user) {

        System.out.println("Welcome to the 421 MarketPlace!");

        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            String name = customer.getName();
            System.out.println("(6) Back | (7) Home | Hello, " + name + "! | (8) Account | (9) Logout | (0) Exit");
        
        } else {
            System.out.println("(6) Back | (7) Home | (8) Login | (9) Sign up | (0) Exit");
        }

        System.out.println("--------------------------------------------------");
        System.out.println();
    }
}
