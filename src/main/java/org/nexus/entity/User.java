package org.nexus.entity;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "college_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", length = 45, nullable = false)

    private String firstName;

    @Column(name = "last_name", length = 45, nullable = false)

    private String lastName;

    @Column(length = 64, nullable = false)

    private String password;

    @Column(length = 128, nullable = false, unique = true)

    private String email;

    private String photos;

    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "college_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

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

    public String getPassword() {

        return password;

    }

    public void setPassword(String password) {

        this.password = password;

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

    public Set<Role> getRoles() {

        return roles;

    }

    public void setRoles(Set<Role> roles) {

        this.roles = roles;

    }

    public User() {

    }

    public User(String email, String password, String firstName, String lastName) {

        this.firstName = firstName;

        this.lastName = lastName;

        this.password = password;

        this.email = email;

    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName
                + ", roles=" + roles + "]";
    }

    @Transient
    public String getPhotoImagePath() {
        if(photos == null || id == null) {
            return "/images/user.png";
        }
//        return "/user-photos/" +this.id + "/" + this.photos;

        return   Constants.S3_BASE_URI + "/user-photos/" +this.id + "/" + this.photos;

    }

    public boolean hasRole(String roleName) {
        Iterator<Role> iterator = roles.iterator();

        while (iterator.hasNext()) {
            Role role = iterator.next();
            if (role.getName().equals(roleName)) {
                return true;
            }
        }

        return false;
    }

}