package com.devrezaur.main.controller;

import com.devrezaur.main.model.Users;
import com.devrezaur.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ConfigureController {

    private UserService userService;

    @Autowired
    public ConfigureController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value="/configure")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addItem(Model model){
        List<Users> allUser=userService.findAll();
        model.addAttribute("allUser", allUser);

        return "configure";
    }

    @GetMapping(value="/details/{id}")
    public String details(Model model, @PathVariable(name="id") Long id){

        Users users=userService.findById(id);
        model.addAttribute("users", users);

        return "details";
    }


    @PostMapping(value="/saveuser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String saveItem(@RequestParam(name="id", defaultValue = "0") Long id,
                           @RequestParam(name="user_name", defaultValue = "0") String name,
                           @RequestParam(name="user_email", defaultValue = "No Item") String email,
                           @RequestParam(name="user_score",defaultValue = "0") int score,
                           @RequestParam(name="roles_id",defaultValue = "0") String  role){

        Users user=userService.findById(id);

        if(user!=null){
            user.setFullName(name);
            user.setEmail(email);
            user.getResult().setTotalCorrect(score);
            user.getRoles().get(0).setRole(role);
            userService.saveUser(user);
        }



        return "redirect:/configure";
    }

    @PostMapping(value="/deleteuser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deleteItem(@RequestParam(name="id", defaultValue = "0") Long id){

        Users user=userService.findById(id);
        System.out.println(user.getFullName());
        if(user!=null){
            userService.deleteUser(user);
        }

        return "redirect:/configure";
    }
}
