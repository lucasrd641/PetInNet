package com.br.petinnet.controller;

import com.br.petinnet.model.Role;
import com.br.petinnet.model.User;
import com.br.petinnet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class DefaultController {

    @Autowired
    private UserService userService;

    @GetMapping(value={"/", "/login"})
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }


    @GetMapping(value="/registration")
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @PostMapping(value = "/registration")
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByUserName(user.getUserName());
        if (userExists != null) {
            bindingResult
                    .rejectValue("userName", "error.user",
                            "There is already a user registered with the user name provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("login");

        }
        return modelAndView;
    }

    @GetMapping(value="/user/search")
    public ModelAndView search(String name_search){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        List<User> users = userService.findUserByString(name_search);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userPetName", user.getUserPetName());
        modelAndView.addObject("name", user.getName() + " " +user.getLastName());
        modelAndView.addObject("userMain", user);
        modelAndView.addObject("users", users);
        modelAndView.addObject("searched", name_search);
        modelAndView.addObject("followers", "Followers: "+user.getFollowers().size());
        modelAndView.addObject("following", "Following: "+user.getFollowing().size());

        modelAndView.setViewName("user/search");
        return modelAndView;
    }


    @GetMapping(value = "/user/follow{id}")
    public ModelAndView followById(@RequestParam(value="id",required = true) Integer id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        userService.followById(user.getId(),id);
        ModelAndView modelAndView = new ModelAndView();
        return new ModelAndView(new RedirectView("/home"));
    }
    @GetMapping(value = "/user/unfollow{id}")
    public ModelAndView unfollowById(@RequestParam(value="id",required = true) Integer id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        userService.unFollowById(user.getId(),id);
        ModelAndView modelAndView = new ModelAndView();
        return new ModelAndView(new RedirectView("/home"));
    }
    @GetMapping(value="/home")
    public ModelAndView homeMain(){
        String redirected;
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        return new ModelAndView(new RedirectView(redirect(user)));
    }

    private String redirect(User user) {
        String[] roles = new String[user.getRoles().size()];
        String url="";
        int i=0;
        for (Role role: user.getRoles()) {
            if(role.getId()==1){
                url="admin/home";
            }else{
                url="user/home";
            }
            roles[i++] = role.getRole();
        }
        return url;
    }

    @GetMapping(value="/user/home")
    public ModelAndView homeUser(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        modelAndView.addObject("userName", user.getUserName());
        modelAndView.addObject("userPetName", user.getUserPetName());
        modelAndView.addObject("name", user.getName() + " " +user.getLastName());
        modelAndView.addObject("followers", "Followers: "+user.getFollowers().size());
        modelAndView.addObject("following", "Following: "+user.getFollowing().size());
        modelAndView.addObject("userDescription", user.getUserDescription());
        modelAndView.addObject("usersFollowing", user.getFollowing());
        modelAndView.setViewName("user/home");
        return modelAndView;
    }

    @GetMapping(value="/user/profile")
    public ModelAndView profileUser(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        modelAndView.addObject("user", user);
        modelAndView.addObject("userPetName", user.getUserPetName());
        modelAndView.addObject("name", user.getName() + " " +user.getLastName());
        modelAndView.addObject("followers", "Followers: "+user.getFollowers().size());
        modelAndView.addObject("following", "Following: "+user.getFollowing().size());
        modelAndView.addObject("userDescription", user.getUserDescription());

        modelAndView.setViewName("user/profile");
        return modelAndView;
    }
    @PostMapping(value = "/user/profile")
    public ModelAndView editUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
            userService.editUser(user);
            modelAndView.addObject("successMessage", "The Profile has been edited successfully");
            modelAndView.addObject("user", user);
            modelAndView.setViewName("user/profile");
        return modelAndView;
    }

    @GetMapping(value="/admin/home")
    public ModelAndView homeAdm(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getUserName() + "/" + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }


}
