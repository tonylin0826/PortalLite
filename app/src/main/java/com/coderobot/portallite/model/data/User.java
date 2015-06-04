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

    public User(String unformat) {
        String[] tmp = unformat.split("__,__");
        account = tmp[0];
        password = tmp[1];
    }

    @Override
    public String toString() {
        return account + "__,__" + password;
    }
}
