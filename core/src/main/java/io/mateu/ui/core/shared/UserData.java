package io.mateu.ui.core.shared;

import java.util.List;

/**
 * Created by miguel on 25/1/17.
 */
public class UserData extends Data {


    public String getLogin() {
        return get("login");
    }

    public void setLogin(String login) {
        set("login", login);
    }

    public String getEmail() {
        return get("email");
    }

    public void setEmail(String email) {
        set("email", email);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public List<Integer> getPermissions() {
        return get("permissions");
    }

    public void setPermissions(List<Integer> permissions) {
        set("permissions", permissions);
    }

    public Data getData() {
        return get("data");
    }

    public void setData(Data data) {
        set("data", data);
    }
}
