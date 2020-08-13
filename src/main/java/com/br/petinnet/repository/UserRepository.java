package com.br.petinnet.repository;

import com.br.petinnet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUserName(String userName);

    @Query(value="SELECT * FROM users WHERE name LIKE %:name_search% OR user_pet_name LIKE %:name_search%", nativeQuery = true)
    List<User> findByString(@Param("name_search") String name_search);

    @Query(value="SELECT * FROM users WHERE user_id=:id", nativeQuery = true)
    User findByIdQ(Integer id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_follower VALUES (:user_id,:follower_id)", nativeQuery = true)
    void followById(@Param("user_id") Integer userId, @Param("follower_id") Integer id);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_follower WHERE user_id = :user_id AND follower_id = :follower_id", nativeQuery = true)
    void unFollowById(@Param("user_id") Integer userId, @Param("follower_id") Integer id);

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE user_follower SET user_id = :user_id, follower_id = :follower_id",nativeQuery = true)
//    void followById(@Param("user_id") Integer userId, @Param("follower_id") Integer id);
}