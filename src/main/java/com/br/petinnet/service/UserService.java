package com.br.petinnet.service;

import com.br.petinnet.model.Post;
import com.br.petinnet.model.User;
import com.br.petinnet.repository.PostRepository;
import com.br.petinnet.repository.RoleRepository;
import com.br.petinnet.model.Role;
import com.br.petinnet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PostRepository postRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PostRepository postRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.postRepository = postRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<User> findUserByString(String name_search) { return userRepository.findByString(name_search); }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
    public void followById(int userId, int id) {userRepository.followById(userId,id);}
    public void unFollowById(int userId, int id) {userRepository.unFollowById(userId,id);}
    public List<Post> findPostsById(int id) {
        return postRepository.findPostsById(id);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    public void editUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    public void savePost(Post post){
        postRepository.savePost(post.getImg(),post.getPost_content(),post.getPost_datetime(),post.getUser().getId(),post.getLikes());
    }


    public boolean checkOldPassword(String oldPassword,String userPassword) {
        return bCryptPasswordEncoder.matches(oldPassword,userPassword);
    }

    public void likePostById(Integer user_id, Integer post_id) {
        postRepository.likePostById(user_id,post_id);
    }

    public void unlikePostById(Integer user_id, Integer post_id) {
        postRepository.unlikePostById(user_id,post_id);
    }

    public int getLikes(Integer id) {
        return postRepository.getLikes(id);
    }

    public Set<User> getRelationsById(Integer user_id, Integer post_id) {
        return userRepository.getRelationsById(user_id,post_id);
    }
}