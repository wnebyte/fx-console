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
    import com.github.wnebyte.fxconsole.Console;
    class Sample extends Application {
        public void start(Stage stage) {
            Console console = new Console();
            console.ready();
            console.setCallback(s -> {
                new Thread(() -> {
                    console.println("read: " + s);
                    console.ready();
                }).start();
            });
            stage.setScene(new Scene(console, 895, 515));
            stage.show();
        }
    }
The console becomes locked after accepting input, so 
call console.ready() to print any specified prefix to the console, 
and unlock the console.<br/>

    import com.github.wnebyte.fxconsole.Console;
    import com.github.wnebyte.fxconsole.StyledText;
    ...
    foo() {
        console.println("text", "css-cls0", "css-cls1");
        StyledText styledText = new StyledTextBuilder()
                .append("foo", "css-cls0")
                .whitespace()
                .append("bar", "css-cls1")
                .ln()
                .append("boo", "css-cls2")
                .build();
        console.println(styledText); 
    }
Prints "text" to the console and applies the two specified css-classes 
to the appended text.<br/>
Then creates and prints a styledText object to the console.
 
## Build

## Documentation

## Licence
tbd