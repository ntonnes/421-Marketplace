import java.util.Scanner;
public class Menu {

    public static void menu(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\nMenu:");
            System.out.println("1. Making a review");
            System.out.println("2. Placeholder option 2");
            System.out.println("3. Placeholder option 3");
            System.out.println("4. Placeholder option 4");
            System.out.println("4. Placeholder option 5");
            System.out.println("6. Quit");

            System.out.print("Enter your choice: ");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("You selected option 1");
                    // Add your code for option 1 here
                    break;
                case 2:
                    System.out.println("You selected option 2");
                    // Add your code for option 2 here
                    break;
                case 3:
                    System.out.println("You selected option 3");
                    // Add your code for option 3 here
                    break;
                case 4:
                    System.out.println("You selected option 4");
                    // Add your code for option 4 here
                    break;
                case 5:
                    System.out.println("You selected option 4");
                    // Add your code for option 4 here
                    break;
                case 6:
                    System.out.println("Exiting the program...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (option != 6);

        scanner.close();
    }
}

