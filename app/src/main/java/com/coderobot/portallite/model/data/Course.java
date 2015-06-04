package com.coderobot.portallite.model.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tony on 2015/3/8.
 */
public class Course{

    public String id;
    public String name;
    public String ctype;
    public List<ClassTime> ctimes;
    public String classroom;
    public Semester semester;


    public Course(String id, String name, String ctype, String ctimes, String classroom, String semester) {
        this.id = id;
        this.name = name;
        this.ctype = ctype;

        List<String> tmp = Arrays.asList(ctimes.split("__,__"));
        this.ctimes = new ArrayList<>();
        for (String str : tmp) {
            if (!str.isEmpty())
                this.ctimes.add(new ClassTime(str));
        }

        this.classroom = classroom;
        this.semester = new Semester(semester);
    }

}
