package com.example.aucison_service.service.product;



import com.example.aucison_service.dto.board.*;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.util.List;

public interface BoardService {
    //List<PostListResponseDto> getAllBoard();

    List<PostListResponseDto> getBoardByProductId(Long productId, OAuth2User principal);
    PostCRUDResponseDto registPost(Long productId, PostRegistRequestDto dto, OAuth2User principal);
    CommentCRUDResponseDto registComment(Long postId, CommentRegistRequestDto dto, OAuth2User principal);
    PostCRUDResponseDto updatePost(Long postId, PostUpdateRequestDto postRequestDto);
    PostCRUDResponseDto deletePost(Long postId);
    CommentCRUDResponseDto updateComment(Long commentId, CommentUpdateRequestDto commentRequestDto);
    CommentCRUDResponseDto deleteComment(Long commentId);




}
