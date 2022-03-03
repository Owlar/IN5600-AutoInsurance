package no.uio.ifi.oscarlr.in5600_autoinsurance;

import androidx.annotation.NonNull;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String passClear;
    private String passHash;
    private String email;

    public User(int id, String firstName, String lastName, String passClear, String passHash, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passClear = passClear;
        this.passHash = passHash;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPassClear() {
        return passClear;
    }

    public void setPassClear(String passClear) {
        this.passClear = passClear;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    @Override
    public String toString() {
        return id + ": " + firstName + " " + lastName;
    }

}
