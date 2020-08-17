package com.br.petinnet.service;

import com.br.petinnet.model.Post;
import com.br.petinnet.model.User;
import com.br.petinnet.repository.RoleRepository;
import com.br.petinnet.model.Role;
import com.br.petinnet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    public User editUser(User user) {

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void savePost(Post post) {

        userRepository.savePost(post.getId(),post.getImg(),post.getPost_content(),post.getPost_datetime(),post.getUser().getId());
    }
}