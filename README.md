# fx-console
javafx-library

## Table of Contents
- [About](#about)
- [Sample](#sample)
- [Build](#build)
- [Documentation](#documentation)
- [Licence](#licence)

## About
This project contains a css-styleable javafx console class.

## Sample
    class Sample extends Application {
        public void start(Stage stage) {
            Console console = new Console();
            console.ready(); // prints the console prefix if one has been set, and unlocks the console
            console.setCallback(s -> {
                new Thread(() -> {
                    console.lock();
                    console.println("read: " + s);
                    console.ready();
                }).start();
            });
            stage.setScene(new Scene(console, 895, 515));
            stage.show();
        }
    }
 
## Build

## Documentation

## Licence
tbd