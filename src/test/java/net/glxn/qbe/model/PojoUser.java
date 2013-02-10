package net.glxn.qbe.model;

@SuppressWarnings("UnusedDeclaration")
public class PojoUser {

    private String id;

    private String nick;

    private String email;

    private Gender gender;

    public PojoUser(String nick) {
        this.nick = nick;
    }

    public PojoUser() {}

    public PojoUser(String nick, String email) {
        this.nick = nick;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}