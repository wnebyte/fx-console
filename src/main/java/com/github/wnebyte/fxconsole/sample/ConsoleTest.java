package com.github.wnebyte.fxconsole.sample;

import com.github.wnebyte.fxconsole.Console;
import com.github.wnebyte.fxconsole.StyledText;
import com.github.wnebyte.fxconsole.util.StyledTextBuilder;
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
            if (s.equals("clear")) {
                console.clear();
            } else if (s.equals("exit")) {
                System.exit(0);
            }
            System.out.println(s);
            console.ln();
        });
        console.println("com.github.wnebyte.console [Version 1.0.0]\n");
        StyledText styledText = new StyledTextBuilder()
                .append("wne@MSI", "green")
                .whitespace()
                .append("MINGW64", "purple")
                .whitespace()
                .append("~", "green")
                .ln()
                .append("$", "text")
                .build();
        console.setPrefix(styledText);
        console.ready();

        stage.setScene(new Scene(console));
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setTitle("Kommandotolken");
        stage.show();
    }

}
