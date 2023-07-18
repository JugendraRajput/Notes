package com.jdgames.notes;

public class NoteParse {

    int id;
    String title;
    String subtitle;

    public NoteParse(int id, String title, String subtitle) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
