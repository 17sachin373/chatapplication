import java.io.*;
import java.net.*;
import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ChatClient extends Application {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // Localhost
    private static final int SERVER_PORT = 12345; // Same port as ChatServer
    private Socket socket;
    private PrintWriter out;
    private TextArea messageArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        // GUI Setup
        messageArea.setEditable(false); // Disable editing for the message display area
        TextField inputField = new TextField(); // Input field for user messages

        // Event handler to send messages when "Enter" is pressed
        inputField.setOnAction(event -> {
            if (out != null) { // Check if the server connection is established
                System.out.println("Message to send: " + inputField.getText());
                out.println(inputField.getText()); // Send message to the server
                inputField.clear(); // Clear the input field
            } else {
                System.out.println("Output stream (out) is null. Unable to send message.");
            }
        });

        VBox layout = new VBox(10, messageArea, inputField); // Layout for the JavaFX window
        Scene scene = new Scene(layout, 400, 300);

        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start the client logic in a new thread to avoid blocking the GUI
        new Thread(this::runClient).start();
    }

    private void runClient() {
        try {
            System.out.println("Connecting to server...");
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT); // Connect to the server
            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);

            out = new PrintWriter(socket.getOutputStream(), true); // For sending messages
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // For receiving messages

            String receivedMessage; // To store incoming messages
            while ((receivedMessage = in.readLine()) != null) {
                String message = receivedMessage; // Assign to a final variable for the lambda
                System.out.println("Message received from server: " + message);
                Platform.runLater(() -> messageArea.appendText(message + "\n")); // Update the GUI safely
            }
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Closing client connection...");
        if (socket != null && !socket.isClosed()) {
            socket.close(); // Close the connection when the application stops
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
