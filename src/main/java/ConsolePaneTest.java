import model.ConsolePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class ConsolePaneTest extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ConsolePane console = new ConsolePane();
        primaryStage.setScene(new Scene(console));
        primaryStage.setWidth(675);
        primaryStage.setHeight(500);
        primaryStage.show();
}
}
