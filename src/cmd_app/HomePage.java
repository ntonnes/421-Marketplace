package cmd_app;
import database.Customer;
import database.User;

public class HomePage implements Page {
    private User user;

    HomePage(User user) {
        this.user = user;
    }

    @Override
    public void display(User user) {
        System.out.println("Main Menu:\n\t(1) Search\n\t(2) View Cart\n\t(3) View Past Orders\n\t(4) Leave a Review \n\t(5) ");
    }  

    @Override
    public void go(int option) {
        switch (option) {
            case 1:
                App.push(new SearchPage());
                break;
            case 2:
                App.push(new CartPage());
                break;
            case 3:
                if (user instanceof Customer) {
                    App.push(new OrdersPage());
                } else {
                    System.out.println("You must be logged in to view this page.");
                }
                break;
            case 4:
                if (user instanceof Customer) {
                    App.push(new ReviewPage());
                } else {
                    System.out.println("You must be logged in to view this page.");
                }
                break;
            case 5:
                break;
            default:
                System.out.println("Invalid option. Try again.");
        }
    }
}
