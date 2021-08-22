package com.github.wnebyte.fxconsole.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public final class Io {

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static List<String> read(final String fileName) {
        try {
            return Files.readAllLines(Path.of(fileName), CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(
                "file: " + fileName + " could not be read"
        );
    }

    public static void write(final String fileName, final List<String> lines) {
        try {
            Files.write(Path.of(fileName), lines, CHARSET,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
