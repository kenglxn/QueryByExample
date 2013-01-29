package net.glxn.qbe.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings({"UnusedDeclaration"})
@Entity
@Table(name = "user")
public class UserEntity  {


    public static final String ID = "col_id";
    public static final String USER_NAME = "col_user_name";
    public static final String DISPLAY_NAME = "col_display_name";
    public static final String FIRST_NAME = "col_first_name";
    public static final String LAST_NAME = "col_last_name";
    public static final String GENDER = "col_gender";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID)
    private String id;

    @Column(name = USER_NAME)
    private String userName;

    @Column(name = DISPLAY_NAME)
    private String displayName;

    @Column(name = FIRST_NAME)
    private String firstName;

    @Column(name = LAST_NAME)
    private String lastName;

    @Column(name = GENDER)
    private Gender gender;

    public UserEntity() {
    }

    public UserEntity(String userName, String displayName, String firstName, String lastName) {
        this.userName = userName;
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
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