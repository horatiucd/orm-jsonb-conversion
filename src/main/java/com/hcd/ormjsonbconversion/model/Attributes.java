package com.hcd.ormjsonbconversion.model;

public class Attributes {

    private String title;
    private String author;
    private int words;

    public Attributes() {}

    public Attributes(String title, String author, int words) {
        this.title = title;
        this.author = author;
        this.words = words;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }
}
