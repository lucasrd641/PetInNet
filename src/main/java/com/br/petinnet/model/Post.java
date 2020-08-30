package com.br.petinnet.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer id;

    @Column(name = "post_content")
    private String post_content;

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(name = "post_img",length=100000)
    private byte[] img;

    @Column(name = "post_datetime")
    private LocalDateTime post_datetime;

    private String agoTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
