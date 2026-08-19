package com.yukilala.games;

public class Game {
    private String name;
    private String packageName;
    private String notes;

    public Game() {}

    public Game(String name, String packageName, String notes) {
        this.name = name;
        this.packageName = packageName;
        this.notes = notes;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Game{name='" + name + "', packageName='" + packageName + "', notes='" + notes + "'}";
    }
}
