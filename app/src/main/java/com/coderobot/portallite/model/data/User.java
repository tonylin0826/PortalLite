package com.coderobot.portallite.model.data;

/**
 * Created by Tony on 2/14/15.
 */
public class User {

    public String account = "";
    public String password = "";

    public User(String id, String pwd) {
        account = id;
        password = pwd;
    }
}
