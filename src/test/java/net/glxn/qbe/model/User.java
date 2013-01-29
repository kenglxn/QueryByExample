package net.glxn.qbe.model;

import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings({"UnusedDeclaration"})
public class User {

    public static final String ID = "id";
    public static final String USER_NAME = "user_name";
    public static final String DISPLAY_NAME = "display_name";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String GENDER = "gender";

    @XmlElement(name = ID)
    private String id;

    @XmlElement(name = USER_NAME)
    private String userName;

    @XmlElement(name = DISPLAY_NAME)
    private String displayName;

    @XmlElement(name = FIRST_NAME)
    private String firstName;

    @XmlElement(name = LAST_NAME)
    private String lastName;

    @XmlElement(name = GENDER)
    private Gender gender;

    public User() {
    }

    public User(String displayName) {
        this.displayName = displayName;
    }

    public User(String userName, String displayName) {
        this.userName = userName;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}