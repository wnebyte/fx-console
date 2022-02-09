package com.github.wnebyte.console;

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
        Console console = new Console();
        console.getStylesheets().add(getClass().getResource("/css/gitbash.css").toExternalForm());
        console.setCallback(s -> {
            new Thread(() -> {
                if (s.equals("clear")) {
                    console.clear();
                }
                else {
                    console.println("read: " + s);
                }
                console.println();
                console.ready();
            }).start();
        });
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
}
