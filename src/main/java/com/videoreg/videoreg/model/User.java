package com.videoreg.videoreg.model;

public class User {
    private String firstName;
    private String lastName;
    private int age;
    private String password;

    public User(String firstName, String lastName, int age, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.password = password;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getAge() { return age; }
    public String getPassword() { return password; }
}
