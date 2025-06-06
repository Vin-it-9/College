package org.nexus.service;


import java.io.IOException;
import java.util.*;

import org.nexus.FileUploadUtil;
import org.nexus.repository.*;
import org.nexus.entity.*;
import org.nexus.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    public static final int USERS_PER_PAGE = 10;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll() {
        return (List<User>) userRepo.findAll();
    }

    public Page<User> listByPage(int pageNum, String keyword) {

        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE);

        if (keyword != null && !keyword.isEmpty()) {
            return userRepo.findAll(keyword, pageable);
        }

        return userRepo.findAll(pageable);
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepo.findAll();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();
        return userRepo.getUserByEmail(email);
    }

    public Optional<User> getUserById(Integer id) {
        return userRepo.findById(id);
    }


    public User save(User user) {

        boolean isUpdatingUser = (user.getId() != null);

        if(isUpdatingUser) {

            User existingUser = userRepo.findById(user.getId()).get();

            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }
      return  userRepo.save(user);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id ,String email) {
        User userByEmail = 	userRepo.getUserByEmail(email);

        if(userByEmail == null) return true;
        boolean isCreatingNew = (id == null);

        if(isCreatingNew) {
            if(userByEmail != null ) return false ;
        }else {
            if(userByEmail.getId() != id) {
                return false;
            }
        }
        return true;
    }

    public User get(Integer id) throws UserNotFoundException {

        try {
            return userRepo.findById(id).get();
        }
        catch (NoSuchElementException ex) {
            throw new UserNotFoundException("Could not find user with id "+ id);

        }

    }


    public void delete(Integer id) throws UserNotFoundException {

        Long countById = userRepo.countById(id);

        if(countById == null || countById == 0) {
            throw new UserNotFoundException("could not fint any user with ID"+id);
        }

        userRepo.deleteById(id);

    }

}