package com.example.aucison_service.jpa.product;


import com.example.Aucsion_Product_Service.time.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class CommentsEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comments_id")
    private Long comments_id;       //PK


    @Column(name = "content", nullable = false)
    private String content;             //답변

    @Column(name = "createdTime", nullable = false)
    private LocalDateTime createdTime;            //댓글 등록시간

    @Column(name = "email", nullable = false)
    private String email;        //답변 작성자 식별 코드



    //생각해 보니 코멘트를 여러개 달 수도 있다고 생각됨 -> 1대 1(or zero 에서) 말고 1대 다(or zero or 1)
    @ManyToOne
    @JoinColumn(name = "posts_id")
    private PostsEntity postsEntity;


    //setter대신 이런 수고를 하자
    public void update(String content) {
        this.content = content;
    }


    @Builder
    public CommentsEntity(String content, LocalDateTime createdTime, String email, PostsEntity postsEntity){
        this.content = content;
        this.createdTime = createdTime;
        this.email = email;
        this.postsEntity = postsEntity;
    }

}

