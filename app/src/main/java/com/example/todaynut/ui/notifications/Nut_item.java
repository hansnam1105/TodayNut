package com.example.todaynut.ui.notifications;

public class Nut_item {
    boolean checked;
    String ItemString;
    Nut_item(boolean b, String t){
        checked = b;
        ItemString = t;
    }
    public boolean isChecked(){
        return checked;
    }
}
