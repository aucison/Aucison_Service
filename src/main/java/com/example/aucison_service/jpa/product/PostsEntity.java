package com.example.aucison_service.jpa.product;

import com.example.aucison_service.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "posts")
public class PostsEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "products_id")
    private Long posts_id;       //PK


    @Column(name = "title", nullable = false)
    private String title;               // 제목

    @Column(name = "content", nullable = false)
    private String content;             //내용

    @Column(name = "createdTime", nullable = false)
    private LocalDateTime createdTime;          //등록시간

    @Column(name = "email", nullable = false)
    private String email;        //게시글 등록자

    @OneToMany(mappedBy = "postsEntity")
    List<CommentsEntity> commentsEntities = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="products_id")
    private ProductsEntity productsEntity;


    //setter대신 이런 수고를 하자
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }


    @Builder
    public PostsEntity(String title, String content, LocalDateTime createdTime, String email, ProductsEntity productsEntity){
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.email = email;
        this.productsEntity = productsEntity;
    }

}
