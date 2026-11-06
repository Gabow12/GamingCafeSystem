/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author GABRIEL DY
 */
public class User {

    private String username;
    private String password;
    private String role;
    private boolean blocked;
    private int loginAttempts;
    private String securityQuestion;
    private String securityAnswer;

    public User(
            String username,
            String password,
            String role,
            String securityQuestion,
            String securityAnswer,
            boolean blocked,
            int loginAttempts) {

        this.username = username;
        this.password = password;
        this.role = role;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.blocked = blocked;
        this.loginAttempts = loginAttempts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public boolean login(String username, String password) {

        if (blocked && !role.equalsIgnoreCase("Admin")) {
            return false;
        }

        if (this.username.equals(username)
                && this.password.equals(password)) {

            loginAttempts = 0;
            return true;
        }

        if (!role.equalsIgnoreCase("Admin")) {

            loginAttempts++;

            if (loginAttempts >= 3) {
                blocked = true;
            }
        }

        return false;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void changePassword(String newPassword) {

        this.password = newPassword;
    }

    @Override
    public String toString() {

        return username + " (" + role + ")";
    }

}
