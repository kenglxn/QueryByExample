package net.glxn.qbe.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "user")
public class User {

    public static final String ID_COLUMN_NAME = "id_column";
    public static final String NICK_COLUMN_NAME = "nick_column";
    public static final String EMAIL_COLUMN_NAME = "email_column";
    public static final String GENDER_COLUMN_NAME = "gender_column";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_COLUMN_NAME)
    private String id;

    @Column(name = NICK_COLUMN_NAME)
    private String nick;

    @Column(name = EMAIL_COLUMN_NAME)
    private String email;

    @Column(name = GENDER_COLUMN_NAME)
    private Gender gender;

    public User() {}

    public User(String nick, String email, Gender gender) {
        this.nick = nick;
        this.email = email;
        this.gender = gender;
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