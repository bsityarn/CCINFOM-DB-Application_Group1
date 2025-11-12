/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author marcquizon
 */
public class Technician {

    private String technicianID;
    private String firstName;
    private String lastName;
    private String email;
    private String position;
    private String currentPassword;
    private String newPassword;

    public static String add(String firstName, String lastName, String email, String position, String password) {
        //TODO - Check for email duplicates
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            return "Empty";
        }
        return "Valid";
    }

    public static String delete(String technicianID) {

        if (technicianID.isBlank()) {
            return "Empty";
        }
        return "Valid";
    }

    public static Technician getInfo(String technicianID) {
        Technician technician = new Technician();
        //TODO: Code logic for retrieving a record
        //TODO: Code setting of attributes for technician to be returned
        return technician;
    }

    // --- Getters ---
    public String getTechnicianID() {
        return technicianID;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    // --- Setters ---
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
