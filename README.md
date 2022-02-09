# fx-console
javafx-library

## Table of Contents
- [About](#about)
- [Sample](#sample)
- [Images](#images)
- [Build](#build)
- [Documentation](#documentation)
- [Licence](#licence)

## About
This project contains a javafx class which  is made to look and behave like a standard console. 
Both the console itself, and any text that is appended to it can be styled using css.


## Sample
### #1
This sample demonstrates the initialization of a console that has no styles applied, no prefix, 
and echoes all input.

    class Sample extends Application {
        public void start(Stage stage) {
            Console console = new Console();
            console.setCallback(s -> {
                console.println("read: " + s + "\n");
                console.ready();
            });
            stage.setScene(new Scene(console, 895, 515));
            console.ready();
            stage.show();
        }
    }
    
 
## Images
![image](images/image1.png)


## Build

## Documentation

## Licence