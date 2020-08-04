package com.br.petinnet.repository;

import com.br.petinnet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUserName(String userName);

    @Query(value="SELECT * FROM users WHERE name LIKE %:name_search% OR user_pet_name LIKE %:name_search%", nativeQuery = true)
    List<User> findByString(@Param("name_search") String name_search);
}