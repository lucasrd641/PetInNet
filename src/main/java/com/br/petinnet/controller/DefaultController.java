package com.br.petinnet.controller;

import com.br.petinnet.model.Post;
import com.br.petinnet.model.Role;
import com.br.petinnet.model.User;
import com.br.petinnet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DefaultController {

    public static String uploadDirectory = System.getProperty("user.dir")+"/uploads";

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

    @RequestMapping(value = "/user/createpost")
    public ModelAndView createPost(@RequestParam("message") String message,@RequestParam(value="customFile", required = false) MultipartFile file) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        Post post = new Post();
        byte barr[]=file.getBytes();
        post.setUser(user);
        post.setPost_content(message);
        LocalDateTime lt = LocalDateTime.now();
        post.setPost_datetime(lt);
        post.setImg(barr);
        post.setLikes(0);
        userService.savePost(post);
        ModelAndView modelAndView = getAllHome();
        modelAndView.setViewName("/user/home");
        return modelAndView;
    }
    @PostMapping(value = "/registration")
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) throws IOException {
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
            InputStream is = getClass().getResourceAsStream("/static/images/paw-icon.png");
            byte[] bytes = is.readAllBytes();
            user.setImg(bytes);
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
        if(users.contains(user)){users.remove(user);}
        ModelAndView modelAndView = getAllHome();
        modelAndView.addObject("users", users);
        modelAndView.addObject("searched", name_search);
        modelAndView.setViewName("user/search");
        return modelAndView;
    }


    @GetMapping(value = "/user/follow{id}")
    public ModelAndView followById(@RequestParam(value="id",required = true) Integer id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        userService.followById(user.getId(),id);
        ModelAndView modelAndView = getAllHome();
        modelAndView.setViewName("user/home");
        return modelAndView;
    }
    @GetMapping(value = "/user/unfollow{id}")
    public ModelAndView unfollowById(@RequestParam(value="id",required = true) Integer id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        userService.unFollowById(user.getId(),id);
        ModelAndView modelAndView = getAllHome();
        modelAndView.setViewName("user/home");
        return modelAndView;
    }
    @GetMapping(value = "/user/like{id}")
    public ModelAndView likeById(@RequestParam(value="id",required = true) Integer id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        userService.likePostById(user.getId(),id);
        ModelAndView modelAndView = getAllHome();
        modelAndView.setViewName("user/home");
        return modelAndView;
    }
    @GetMapping(value = "/user/dislike{id}")
    public ModelAndView dislikeById(@RequestParam(value="id",required = true) Integer id){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        userService.unlikePostById(user.getId(),id);
        ModelAndView modelAndView = getAllHome();
        modelAndView.setViewName("user/home");
        return modelAndView;
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
        ModelAndView modelAndView = getAllHome();
        modelAndView.setViewName("user/home");
        return modelAndView;
    }

    private ModelAndView getAllHome() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        List<Post> posts = userService.findPostsById(user.getId());

        LocalDateTime lt = LocalDateTime.now();
        for (Post p:posts) {
            p.setPostUserRelations(userService.getRelationsById(user.getId(),p.getId()));
            p.setLikes(userService.getLikes(p.getId()));
            Duration duration = Duration.between(p.getPost_datetime(),lt);
            if (duration.toSeconds()<=60){
                p.setAgoTime(" "+(duration.toSeconds()<=1? " Just now" : duration.toSeconds()+" seconds ago" ));
            }else if(duration.toSeconds()>60 && duration.toMinutes()<60){
                p.setAgoTime(" "+duration.toMinutes()+(duration.toMinutes()>1? " Minutes ago" : " Minute ago" ));
            }else if(duration.toMinutes()>60 && duration.toHours()<24){
                p.setAgoTime(" "+duration.toHours()+(duration.toHours()>1? " Hours ago" : " Hour ago" ));
            }else if(duration.toHours()>24){
                p.setAgoTime(" "+duration.toDays()+(duration.toDays()>1? " Days ago" : " Day ago" ));
            }

        }
        modelAndView.addObject("user", user);
        modelAndView.addObject("posts", posts);
        modelAndView.addObject("userName", user.getUserName());
        modelAndView.addObject("upload", "Upload Photo");
        modelAndView.addObject("userPetName", user.getUserPetName());
        modelAndView.addObject("name", user.getName() + " " +user.getLastName());
        modelAndView.addObject("followers", "Followers: "+user.getFollowers().size());
        modelAndView.addObject("following", "Following: "+user.getFollowing().size());
        modelAndView.addObject("userDescription", user.getUserDescription());
        modelAndView.addObject("usersFollowing", user.getFollowing());
        return modelAndView;
    }

    @RequestMapping(value="/user/profile")
    public ModelAndView profileUser(){
        ModelAndView modelAndView = getAllHome();
        modelAndView.setViewName("user/profile");
        return modelAndView;
    }
    @RequestMapping(value = "/user/editUser")
    @PostAuthorize("hasRole('USER')")
    public ModelAndView editUser(@RequestParam("name") String name,@RequestParam("lastName") String lastName,
                                 @RequestParam("petName") String petName,@RequestParam(value="customFile", required = false) MultipartFile file,
                                 @RequestParam("description") String description,@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,@RequestParam("confirmNewPassword") String confirmPassword) throws IOException {
        ModelAndView modelAndView = getAllHome();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserName(auth.getName());
        if(!userService.checkOldPassword(oldPassword,user.getPassword())){
            modelAndView.addObject("successMessage","Invalid Current Password");
            modelAndView.setViewName("user/profile");

            return modelAndView;
        }else{
            if(!newPassword.equals(confirmPassword)){
                modelAndView.addObject("successMessage","Invalid New Password");
            }else{
                user.setName(name);
                user.setLastName(lastName);
                user.setUserPetName(petName);
                user.setImg(file.getBytes());
                user.setUserDescription(description);
                user.setPassword(newPassword);
                userService.editUser(user);
                modelAndView.addObject("successMessage","The User has been edited");
            }
            modelAndView.setViewName("user/profile");
            return modelAndView;
        }
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
