package com.br.petinnet.repository;

import com.br.petinnet.model.Post;
import com.br.petinnet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUserName(String userName);

    @Query(value="SELECT * FROM users WHERE name LIKE %:name_search% OR user_pet_name LIKE %:name_search%", nativeQuery = true)
    List<User> findByString(@Param("name_search") String name_search);

    //@Query(value="SELECT * FROM posts WHERE user_id=:id OR user_id IN (SELECT user_id FROM user_follower WHERE follower_id=:id )
    // ORDER BY post_datetime DESC", nativeQuery = true)


   // List<Post> findPostsById(@Param("id")Integer id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_follower VALUES (:user_id,:follower_id)", nativeQuery = true)
    void followById(@Param("user_id") Integer userId, @Param("follower_id") Integer id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_follower WHERE user_id = :user_id AND follower_id = :follower_id", nativeQuery = true)
    void unFollowById(@Param("user_id") Integer userId, @Param("follower_id") Integer id);

    @Query(value="SELECT u.* FROM users u JOIN post_like pl ON pl.user_id=u.user_id WHERE pl.user_id=:user_id AND pl.post_id=:post_id", nativeQuery = true)
    Set<User> getRelationsById(Integer user_id, Integer post_id);
}