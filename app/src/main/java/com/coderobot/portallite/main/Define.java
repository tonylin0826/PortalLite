package com.coderobot.portallite.main;

/**
 * Created by Tony on 2015/3/7.
 */
public interface Define {
    interface Message {
        int MSG_API_LOGIN = 0;
        int MSG_API_GET_SCHEDULE = 1;
        int MSG_API_GET_COURSE_DETAIL = 2;
        int MSG_API_GET_SEMESTERS = 3;

        int MSG_LOGIN_FINISHED = 4;
    }
}
