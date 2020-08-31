package com.br.petinnet.repository;

import com.br.petinnet.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {


    @Query(value="SELECT * FROM posts WHERE user_id=:id OR user_id IN (SELECT follower_id FROM user_follower WHERE user_id=:id ) " +
            "ORDER BY post_datetime DESC LIMIT 10", nativeQuery = true)
    List<Post> findPostsById(@Param("id")Integer id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO posts (post_img,post_content,post_datetime,user_id,likes) VALUES (:post_img,:post_content,:post_datetime,:user_id,:likes)", nativeQuery = true)
    void savePost(@Param("post_img") byte[] img, @Param("post_content") String post_content, @Param("post_datetime") LocalDateTime post_datetime, @Param("user_id") Integer user_id, @Param("likes") int likes);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO post_like (post_id,user_id) VALUES (:post_id,:user_id)", nativeQuery = true)
    void likePostById(Integer user_id, Integer post_id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM post_like WHERE user_id=:user_id AND post_id=:post_id", nativeQuery = true)
    void unlikePostById(Integer user_id, Integer post_id);

    @Query(value="SELECT COUNT(user_id) FROM post_like WHERE post_id=:id", nativeQuery = true)
    int getLikes(Integer id);

}
