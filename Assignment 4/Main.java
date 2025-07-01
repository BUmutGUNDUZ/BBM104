import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // Holds a reference to the main GameScene instance
    public static GameScene gameScene;

    @Override
    public void start(Stage stage) {
        // Create the game scene and set it on the stage
        gameScene = new GameScene(stage);
        stage.setScene(gameScene.getScene());
    }

    public static void main(String[] args) {
        launch(args);
    }
}