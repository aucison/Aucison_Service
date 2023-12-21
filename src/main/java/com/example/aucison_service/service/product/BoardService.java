package com.example.aucison_service.service.product;



import com.example.aucison_service.dto.board.*;
import com.example.aucison_service.service.member.MemberDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.util.List;

public interface BoardService {
    //List<PostListResponseDto> getAllBoard();

    List<PostListResponseDto> getBoardByProductId(Long productId, @AuthenticationPrincipal MemberDetails principal);
    PostCRUDResponseDto registPost(Long productId, PostRegistRequestDto dto, @AuthenticationPrincipal MemberDetails principal);
    CommentCRUDResponseDto registComment(Long postId, CommentRegistRequestDto dto, @AuthenticationPrincipal MemberDetails principal);
    PostCRUDResponseDto updatePost(Long postId, PostUpdateRequestDto postRequestDto, @AuthenticationPrincipal MemberDetails principal);
    PostCRUDResponseDto deletePost(Long postId, @AuthenticationPrincipal MemberDetails principal);
    CommentCRUDResponseDto updateComment(Long commentId, CommentUpdateRequestDto commentRequestDto, @AuthenticationPrincipal MemberDetails principal);
    CommentCRUDResponseDto deleteComment(Long commentId, @AuthenticationPrincipal MemberDetails principal);




}
