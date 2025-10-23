package com.proyectoweb.Juledtoys.modelos_old;

public class UserAccount {
  private String firstName;
  private String lastName;
  private String gender; // "M" o "F"
  private String email;
  private String role;   // "ADMIN" o "CLIENTE"

  public UserAccount() {}
  public UserAccount(String firstName, String lastName, String gender, String email, String role) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.email = email;
    this.role = role;
  }

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public String getGender() { return gender; }
  public void setGender(String gender) { this.gender = gender; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
}
