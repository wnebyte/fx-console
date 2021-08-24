package com.github.wnebyte.fxconsole.sample;

import com.github.wnebyte.fxconsole.Console;
import com.github.wnebyte.fxconsole.StylisedText;
import com.github.wnebyte.fxconsole.util.StylisedTextBuilder;
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
        console.setCallback(s -> {
            new Thread(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (s.equals("clear")) {
                    console.clear();
                } else if (s.equals("clear -h")) {
                    console.clearHistory();
                    console.println("history cleared");
                }
                else {
                    console.println("read: " + s);
                }
                console.ln();
                console.ready();
            }).start();
        });
        console.println("com.github.wnebyte.console [Version 1.0.0]\n");
        StylisedText stylisedText = new StylisedTextBuilder()
                .append("wne@MSI", "green")
                .whitespace()
                .append("MINGW64", "purple")
                .whitespace()
                .append("~", "green")
                .ln()
                .append("$", "text")
                .build();
        console.setPrefix(stylisedText);
        console.ready();

        stage.setScene(new Scene(console, WIDTH, HEIGHT));
        stage.setTitle("Kommandotolken");
        stage.show();
    }
}
