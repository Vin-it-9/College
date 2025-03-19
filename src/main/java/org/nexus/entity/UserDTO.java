package org.nexus.entity;

import java.util.Set;

public class UserDTO {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String photos;
    private boolean enabled;
    private Set<String> roles; // We map roles to role names instead of the full Role entity

    // Constructors
    public UserDTO() {
    }

    public UserDTO(Integer id, String firstName, String lastName, String email, String photos, boolean enabled, Set<String> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photos = photos;
        this.enabled = enabled;
        this.roles = roles;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
