package org.nexus.controller;


import org.nexus.Exporter.*;
import org.nexus.aws.AmazonS3Util;
import org.nexus.entity.*;
import org.nexus.exception.UserNotFoundException;
import org.nexus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import jakarta.servlet.http.HttpServletResponse;


@Controller
public class UserController {

    @Autowired
    private UserService service ;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String ListFirstPage(Model model) {
        return listPage(1,model,null);
    }

    @GetMapping("/users/page/{pageNum}")
    public String listPage(@PathVariable (name = "pageNum") int pageNum, Model model , @Param("keyword") String keyword) {

        Page<User> page = service.listByPage(pageNum,keyword);

        List<User> listUsers = page.getContent();

        long startcount = (pageNum - 1) * userService.USERS_PER_PAGE  + 1;

        long endcount = startcount + userService.USERS_PER_PAGE - 1;

        if(endcount > page.getTotalElements()) {
            endcount = page.getTotalElements();
        }

        model.addAttribute("startcount", startcount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentpage", pageNum);
        model.addAttribute("endcount", endcount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers" , listUsers);
        model.addAttribute("keyword", keyword);

        return "Users" ;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {

        List<Role> listRoles = service.listRoles();

        User user = service.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Edit Profile: " + user.getFirstName() + " " + user.getLastName());
        model.addAttribute("currentDateTime", formattedDateTime);
        model.addAttribute("currentUserLogin", user.getEmail());
        model.addAttribute("listRoles", listRoles);


        return "profile";
    }


//    @PostMapping("/users/save")
//    public String saveUserProfile(User user,
//                           RedirectAttributes redirectAttributes,
//                           @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
//
//        if (!multipartFile.isEmpty()) {
//            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//            user.setPhotos(filename);
//            User savedUser = service.save(user);
//            String uploadDir = "user-photos/" + savedUser.getId();
//
//            AmazonS3Util.removeFolder(uploadDir);
//            AmazonS3Util.uploadFile(uploadDir, filename, multipartFile.getInputStream());
//        } else {
//            if (user.getPhotos().isEmpty()) {
//                user.setPhotos(null);
//            }
//            service.save(user);
//        }
//
//        redirectAttributes.addFlashAttribute("message", "User saved successfully");
//
//
//
//        return "redirect:/users";
//    }


    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> listRoles = service.listRoles();
        User user = new User();

        user.setEnabled(true);

        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle","Create New User");
        return "user_form";

    }


    @PostMapping("/users/save")
    public String saveUser(User user , RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile ) throws IOException  {

        if(!multipartFile.isEmpty()) {

            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(filename);
            User savedUser = service.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, filename, multipartFile.getInputStream());
//            String uploadDir = "user-photos/" + savedUser.getId();
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir,filename,multipartFile);

        }
        else
        {
            if(user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            service.save(user);
        }
        //        service.save(user);

        User currentUser = service.getCurrentUser();
        if (currentUser != null && user.getId() != null && user.getId().equals(currentUser.getId())) {
            return "redirect:/profile";
        }

        redirectAttributes.addFlashAttribute("message" , "User added successfully ");
        return "redirect:/users";
    }




    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id ,
                           Model model ,
                           RedirectAttributes redirectAttributes ){
        try {
            List<Role> listRoles = service.listRoles();

            User user = service.get(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle","Edit User (ID:" + id + ")");
            model.addAttribute("listRoles", listRoles);

            return "user_form" ;
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message" , ex.getMessage());

        }
        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            String userPhotosDir = "user-photos/" + id;
            AmazonS3Util.removeFolder(userPhotosDir);

            redirectAttributes.addFlashAttribute("message",
                    "The user ID " + id + " has been deleted successfully");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }


    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List <User> listUsers =	service.listAll();
        UserCsvExporter exporter = new UserCsvExporter ();
        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List <User> listUsers =	service.listAll();
        UserExcelExporter exporter = new UserExcelExporter ();
        exporter.export(listUsers, response);

    }
    @GetMapping("users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAll();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(listUsers, response);
    }


}