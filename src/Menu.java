import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Menu extends Application {
    @Override
    public void start(Stage primaryStage) {

        // Create title wrapped in a centered HBox
        Label title = new Label("421 Marketplace");
        title.getStyleClass().add("title");
        HBox hbox = new HBox(title);
        hbox.setAlignment(Pos.CENTER);

        // Create buttons for tasks and quit
        Button button1 = new Button("Task 1");
        Button button2 = new Button("Task 2");
        Button button3 = new Button("Task 3");
        Button button4 = new Button("Quit");

        // Add action listeners to buttons to redirect to tasks or quit
        button1.setOnAction(e -> System.out.println("You selected task 1"));
        button2.setOnAction(e -> System.out.println("You selected task 2"));
        button3.setOnAction(e -> System.out.println("You selected task 3"));
        button4.setOnAction(e -> {
            System.out.println("Exiting the program...");
            System.exit(0);
        });

        // Create a VBox and add the buttons to it
        VBox vbox = new VBox(10, button1, button2, button3, button4);
        vbox.setAlignment(Pos.CENTER);

        // Add the title HBox and options VBox to a BorderPane within the scene
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hbox); 
        borderPane.setCenter(vbox);
        Scene scene = new Scene(borderPane, 400, 400);

        // Apply the dark theme CSS stylesheet
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());

        // Set the scene in the stage and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("421 Marketplace");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}