package com.coderobot.portallite.model.data;

/**
 * Created by the great Tony on 2015/5/28.
 */
public class CourseInfo {

    public String id;
    public String attachment;
    public String author;
    public String date;
    public String detail;
    public String subject;

    public CourseInfo(String id, String attachment, String author, String date, String detail, String subject) {
        this.id = id;
        this.attachment = attachment;
        this.author = author;
        this.date = date;
        this.detail = detail;
        this.subject = subject;
    }
}
