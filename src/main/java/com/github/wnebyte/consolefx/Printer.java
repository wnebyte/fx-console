package com.github.wnebyte.consolefx;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

public abstract class Printer extends PrintStream {

    private static final OutputStream STUB_OUTPUT_STREAM = new OutputStream() {
        /**
         * @throws UnsupportedOperationException if called.
         */
        @Override
        public void write(int b) {
            throw new UnsupportedOperationException(
                    "This OutputStream cannot be written to."
            );
        }
    };

    public Printer() {
        super(STUB_OUTPUT_STREAM);
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public PrintStream append(char c) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public PrintStream append(CharSequence csq) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public boolean checkError() {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public void clearError() {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public void setError() {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public void close() {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public void flush() {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public void write(int i) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public void write(byte[] buf, int off, int len) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public void write(byte[] b) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public PrintStream printf(String format, Object... args) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public PrintStream format(String format, Object... args) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }

    /**
     * @throws UnsupportedOperationException if called.
     */
    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        throw new UnsupportedOperationException(
                "This method is unsupported."
        );
    }
}
