package com.coderobot.portallite.model.data;

/**
 * Created by the great Tony on 2015/5/28.
 */
public class Material {

    public String id;
    public String attachment;
    public String course_schedule;
    public String date;
    public String detail;

    public Material(String id, String attachment, String course_schedule, String date, String detail) {
        this.id = id;
        this.attachment = attachment;
        this.course_schedule = course_schedule;
        this.date = date;
        this.detail = detail;
    }
}
