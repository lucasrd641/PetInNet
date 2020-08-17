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

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUserName(String userName);

    @Query(value="SELECT * FROM users WHERE name LIKE %:name_search% OR user_pet_name LIKE %:name_search%", nativeQuery = true)
    List<User> findByString(@Param("name_search") String name_search);

    @Query(value="SELECT * FROM users WHERE user_id=:id", nativeQuery = true)
    User findByIdQ(int id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_follower VALUES (:user_id,:follower_id)", nativeQuery = true)
    void followById(@Param("user_id") int userId, @Param("follower_id") int id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_follower WHERE user_id = :user_id AND follower_id = :follower_id", nativeQuery = true)
    void unFollowById(@Param("user_id") int userId, @Param("follower_id") int id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO posts VALUES (:post_id,:post_img,:post_content,:post_datetime,:user_id)", nativeQuery = true)
    void savePost(@Param("post_id") int id, @Param("post_img") byte[] img, @Param("post_content") String post_content, @Param("post_datetime") LocalDateTime post_datetime, @Param("user_id") int user_id);
}