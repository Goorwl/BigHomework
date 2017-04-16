package com.example.ll.player.lyric;

public class LyricBean {
    private int    startPoint;
    private String content;

    public LyricBean(int startPoint, String content) {
        this.startPoint = startPoint;
        this.content = content;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LyricBean{" +
                "startPoint=" + startPoint +
                ", content='" + content + '\'' +
                '}';
    }
}
