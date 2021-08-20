package com.github.wnebyte.console.sample;

import com.github.wnebyte.console.Console;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class ConsoleTest extends Application {

    private static final double WIDTH = 895;

    private static final double HEIGHT = 515;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Console console = new Console();
        console.setCallback(System.out::println);
        console.println("com.github.wnebyte.console [Version 1.0.0]" + System.lineSeparator());
        stage.setScene(new Scene(console));
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setTitle("Kommandotolken");
        stage.show();
    }
}
