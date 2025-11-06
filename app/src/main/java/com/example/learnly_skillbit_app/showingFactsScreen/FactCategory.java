package com.example.learnly_skillbit_app.showingFactsScreen;

public class FactCategory {
    private final String name;
    private final int imageRes;
    private final String group; // "Interesting" or "Psychology" or "unKnown"

    public FactCategory(String name, int imageRes, String group) {
        this.name = name;
        this.imageRes = imageRes;
        this.group = group;
    }
    public String getName() { return name; }
    public int getImageRes() { return imageRes; }
    public String getGroup() { return group; }
}
