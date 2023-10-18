package com.example.aucison_service.service.product;

import com.example.Aucsion_Product_Service.dto.board.*;

import java.util.List;

public interface BoardService {
    List<PostListResponseDto> getAllBoard();

    List<PostListResponseDto> getBoardByProductId(Long productId);
    PostCRUDResponseDto updatePost(Long postId, PostUpdateRequestDto postRequestDto);
    PostCRUDResponseDto deletePost(Long postId);
    CommentCRUDResponseDto updateComment(Long commentId, CommentUpdateRequestDto commentRequestDto);
    CommentCRUDResponseDto deleteComment(Long commentId);

    PostCRUDResponseDto registPost(Long productId,PostRegistRequestDto dto);

    CommentCRUDResponseDto registComment(Long postId, CommentRegistRequestDto dto);


}
