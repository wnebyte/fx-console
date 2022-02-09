package com.github.wnebyte.consolefx.util;

import java.util.Objects;
import javafx.application.Platform;

/**
 * This class declares utility-methods for working with <code>JavaFX</code> UI components.
 */
public final class GUIUtils {

    private GUIUtils() {
        throw new UnsupportedOperationException();
    }

    public static void runSafe(final Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        }
        else {
            Platform.runLater(runnable);
        }
    }

}