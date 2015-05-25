package com.coderobot.portallite.model.data;

/**
 * Created by the great Tony on 2015/5/21.
 */
public class ClassTime {
    public int day_of_week;
    public int time_of_day;

    public ClassTime(String raw) {
        String[] tmp = raw.split("-");
        day_of_week = Integer.parseInt(tmp[0]);
        time_of_day = Integer.parseInt(tmp[1]);
    }

    @Override
    public String toString() {
        return day_of_week + "-" + time_of_day;
    }
}
