package data;

class Index {

    private final int start;
    private final int end;

    Index(int start, int end) {
        this.start = start;
        this.end = end;
    }

    int getStart() {
        return start;
    }

    int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format("start=%s, end=%s", getStart(), getEnd());
    }
}
