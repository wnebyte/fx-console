package com.github.wnebyte.consolefx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.*;

public class ConsoleTest extends Application {

    private static final double WIDTH = 895;

    private static final double HEIGHT = 515;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Console console = new Console();
        console.getStylesheets().add(getClass().getResource("/css/gitbash.css").toExternalForm());
        console.setCallback(s -> executor.submit(() -> {
            if (s.equals("clear")) {
                console.clear();
            }
            else if (s.equals("exit")) {
                System.exit(0);
            }
            else {
                console.out.println("read: " + s + "\n");
            }
            console.ready();
        }));
        console.setPrefix(createPrefix());
        console.println("com.github.wnebyte.consolefx\n");
        console.ready();
        stage.setScene(new Scene(console, WIDTH, HEIGHT));
        stage.setTitle("Console");
        stage.show();
    }

    private StyleText createPrefix() {
        return new StyleTextBuilder()
                .append("wne@MSI", "green")
                .whitespace()
                .append("MINGW64", "purple")
                .whitespace()
                .append("~", "green")
                .ln()
                .append("$", "text")
                .build();
    }

    private StyleText createPrefix00() {
        return new StyleTextBuilder()
                .build();
    }

    private StyleText createPrefix01() {
        return new StyleTextBuilder()
                .append("usr$", "purple")
                .build();
    }

    private StyleText createPrefix02() {
        return new StyleTextBuilder()
                .append("wne@MSI ", "green")
                .append("MINGW64 ", "purple")
                .append("~\n", "green")
                .append("$", "green")
                .whitespace()
                .build();
    }

    private StyleText createPrefix03() {
        return new StyleTextBuilder()
                .build();
    }

    private StyleText createPrefix04() {
        return new StyleTextBuilder()
                .append("wne@MSI ", "green")
                .append("MINGW64 ", "purple")
                .append("~\n", "green")
                .append("$ ", "green")
                .build();
    }

    private StyleText createPrefix05() {
        return new StyleTextBuilder()
                .append("C:\\users\\user> ")
                .build();
    }

}
