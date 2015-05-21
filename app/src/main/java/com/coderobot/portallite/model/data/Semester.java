package com.coderobot.portallite.model.data;

/**
 * Created by Tony on 2015/3/7.
 */
public class Semester {
    public int year;
    public int semester;

    public Semester(String sem) {
        String[] s = sem.split("/");
        year = Integer.parseInt(s[0]);
        semester = Integer.parseInt(s[1]);
    }

    @Override
    public String toString() {
        return year + "/" + semester;
    }
}
