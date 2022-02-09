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
This project contains a javafx class which  is made to look and behave like a standard console.<br> 
Both the console itself, and any text appended to it can be styled using css.


## Sample
### #1
This sample demonstrates the initialization of a console that has no styles applied, no prefix,<br> 
and echoes all input.

    class Sample extends Application {
        public void start(Stage stage) {
            Console console = new Console();
            console.setCallback(s -> {
                console.println("read: " + s + "\n");
                console.ready();
            });
            stage.setScene(new Scene(console, 895, 515));
            console.println("com.github.wnebyte.consolefx\n");
            console.ready();
            stage.show();
        }
    }
    
 
## Images
### /css/gitbash.css
![image1](images/image1.png)

### /css/win.css
![image2](images/image2.png)

## Build

## Documentation

## Licence