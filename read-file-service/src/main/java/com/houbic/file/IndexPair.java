package com.houbic.file;

public class IndexPair {

    private long startIndex;

    private long endIndex;

    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    public long getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(long endIndex) {
        this.endIndex = endIndex;
    }

    public static IndexPair build(long startIndex, long endIndex) {
        IndexPair indexPair = new IndexPair();
        indexPair.setStartIndex(startIndex);
        indexPair.setEndIndex(endIndex);
        return indexPair;
    }

    @Override
    public String toString() {
        return "IndexPair{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
