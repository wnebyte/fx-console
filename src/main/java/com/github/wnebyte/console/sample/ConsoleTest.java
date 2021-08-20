package com.github.wnebyte.console.sample;

import com.github.wnebyte.console.Console;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ConsoleTest extends Application {

    private static final double WIDTH = 895;

    private static final double HEIGHT = 515;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Console console = new Console(Console.Style.LINUX);
        console.setCallback(System.out::println);
        console.setPrefix(">>");
        console.println("com.github.wnebyte.console [Version 1.0.0]")
                .ln()
                .ready();
        stage.setScene(new Scene(console));
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setTitle("Kommandotolken");
        stage.show();
    }
}
