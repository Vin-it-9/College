package org.nexus.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller("/dashboard")
@RequestMapping("/dashboard")
public class Dashboardcontroller {

    @GetMapping("/director")
    public String director() {
        return "dashboard/director";
    }

    @GetMapping("/principal")
    public String principle() {
        return "dashboard/principle";
    }

    @GetMapping("/hod")
    public String hod() {
        return "dashboard/hod";
    }

//    @GetMapping("/lab-assistant")
//    public String labassistant() {
//        return "dashboard/labassistant";
//    }
//
//    @GetMapping("/teacher")
//    public String teacher() {
//        return "dashboard/teacher";
//    }






}
