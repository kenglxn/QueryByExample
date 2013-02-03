package net.glxn.qbe.model;


@SuppressWarnings({"UnusedDeclaration"})
public class User {

    private String id;

    private String userName;

    private String displayName;

    private String firstName;

    private String lastName;

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