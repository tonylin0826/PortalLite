package com.coderobot.portallite.model.data;

/**
 * Created by the great Tony on 2015/5/30.
 */
public class Homework {
    public String id;
    public String attachment;
    public String course_schedule;
    public String deadline;
    public String detail;
    public String grade;
    public String subject;
    public String uploaded_file;

    public Homework(String id, String attachment, String course_schedule, String deadline, String detail, String grade, String subject, String uploaded_file) {
        this.id = id;
        this.attachment = attachment;
        this.course_schedule = course_schedule;
        this.deadline = deadline;
        this.detail = detail;
        this.grade = grade;
        this.subject = subject;
        this.uploaded_file = uploaded_file;

    }
}
