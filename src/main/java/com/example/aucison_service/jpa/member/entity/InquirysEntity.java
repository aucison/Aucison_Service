package com.example.aucison_service.jpa.member.entity;



import com.example.aucison_service.enums.QStatusEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "inquirys")
public class InquirysEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquirys_id")
    private Long inauirysId;       //PK

    @Column(name = "q_Email", nullable = false)
    private String qEmail;           //문의자 이메일

    @Column(name = "q_Post", nullable = false)
    private String qPost;           //문의자 이메일

    @Column(name = "q_Comment", nullable = false)
    private String qComment;           //문의자 이메일

    @Column(name = "q_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private QStatusEnum qStatus;         //상품 상태

    @Builder
    public InquirysEntity(String qEmail, String qPost, String qComment, QStatusEnum qStatus){
        this.qEmail=qEmail;
        this.qPost=qPost;
        this.qComment=qComment;
        this.qStatus=qStatus;
    }

}
