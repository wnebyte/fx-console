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

This project contains the <code>Console</code> class, which is a javafx class that is made to look and behave like a standard console.<br> 
Both the console itself, and any appended text can be individually styled using css.

## Sample

### #1

This sample demonstrates the creation and initialization of a <code>Console</code> that has no styles applied, 
no prefix, and which simply echoes any and all input.

    class Sample extends Application {
        public void start(Stage stage) {
            Console console = new Console();
            console.setCallback(s -> {
                console.println("read: " + s + "\n");
            });
            stage.setScene(new Scene(console, 895, 515));
            stage.show();
        }
    }
    
### #2

This sample demonstrates how to set the <code>Console</code>'s optional prefix.<br>

    ...
    console.getStylesheets().add(getClass().getResource("<filepath>.css").toExternalForm());
    console.setPrefix(new StyleTextBuilder()
            .append("wne@MSI", "green")
            .whitespace()
            .append("MINGW64", "purple")
            .whitespace()
            .append("~", "green")
            .ln()
            .append("$", "text")
            .build()
    );

Each appended <code>StyleTextSegment</code> consists of some text, and a Collection of styleClasses 
to be applied or added to the text.

    ...
    console.ready();

Unlocks (if currently locked) the <code>Console</code> and appends its optional prefix.
 
## Images

### /css/gitbash.css

![image1](images/image1.png)

### /css/win.css

![image2](images/image2.png)

## Build

## Documentation

## Licence