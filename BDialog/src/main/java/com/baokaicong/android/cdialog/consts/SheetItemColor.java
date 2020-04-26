package com.baokaicong.android.cdialog.consts;

public enum SheetItemColor {
    Blue("#037BFF"),
    Black("#000000"),
    Gray("#808080"),
    ActionColor("#ff8800"),
    Red("#FD4A2E");

    private String name;

    private SheetItemColor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
