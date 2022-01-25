package com.github.wnebyte.console.sample;

import com.github.wnebyte.console.Console;
import com.github.wnebyte.console.StyleText;
import com.github.wnebyte.console.StyleTextBuilder;
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
        console.getStylesheets().add(getClass().getResource("/css/win.css").toExternalForm());
        console.setCallback(s -> {
            new Thread(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (s.equals("clear")) {
                    console.clear();
                }
                else {
                    console.println("read: " + s);
                }
                console.ln();
                console.ready();
            }).start();
        });
        console.println("com.github.wnebyte.console [Version 1.0.0]\n");
        console.setPrefix(createPrefix());
        console.ready();
        stage.setScene(new Scene(console, WIDTH, HEIGHT));
        stage.setTitle("Kommandotolken");
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
}