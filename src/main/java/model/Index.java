package model;

public class Index {

    private final int paragraph;

    private final int start;

    private final int end;

    public Index(int paragraph, int start, int end) {
        this.paragraph = paragraph;
        this.start = start;
        this.end = end;
    }

    public int getParagraph() {
        return paragraph;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format("model.Index[paragraph=%s,start=%s,end=%s]",
                getParagraph(), getStart(), getEnd());
    }
}
