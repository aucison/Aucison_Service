package com.example.aucison_service.controller;



import com.example.aucison_service.dto.ApiResponse;
import com.example.aucison_service.dto.board.*;
import com.example.aucison_service.service.member.MemberDetails;
import com.example.aucison_service.service.product.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class BoardController {

    private BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService){
        this.boardService=boardService;
    }

    /*
    @GetMapping("/detail/board/{products_id}")
    public ResponseEntity<Map<String, Object>> getBoardByProductId(@PathVariable Long products_id) {
        List<PostListResponseDto> posts = boardService.getBoardByProductId(products_id);

        Map<String, Object> response = new HashMap<>();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

     */

    //해당 상품의 게시물 및 댓글 조회
    @GetMapping("/detail/{products_id}/board")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<PostListResponseDto>> getBoardByProductId(@PathVariable Long products_id,
                                                                      @AuthenticationPrincipal MemberDetails principal){

        return ApiResponse.createSuccess(boardService.getBoardByProductId(products_id, principal));
    }


    // 상품 개별 조회에서 게시물 작성
    // post도 마치 get처럼 짠다 이방식은
    @PostMapping("/detail/{products_id}/board")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PostCRUDResponseDto> registPost(@PathVariable("products_id") Long productId,
                                                       @RequestBody PostRegistRequestDto postRegistRequestDto,
                                                       @AuthenticationPrincipal MemberDetails principal) {

        return ApiResponse.createSuccess(boardService.registPost(productId, postRegistRequestDto, principal));
    }

    // 상품 개별 조회에서 게시물에 댓글 작성
    @PostMapping("/detail/{products_id}/board/{posts_id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<?> registComment(@PathVariable("products_id") Long productId,
                                        @PathVariable("posts_id") Long postId,
                                        @RequestBody CommentRegistRequestDto commentRegistRequestDto,
                                        @AuthenticationPrincipal MemberDetails principal) {

        return ApiResponse.createSuccess(boardService.registComment(postId, commentRegistRequestDto, principal));
    }

    @PutMapping("/detail/{products_id}/board/{posts_id}")
    public ApiResponse<PostCRUDResponseDto> updatePost(@PathVariable("products_id") Long productId,
                                                       @PathVariable("posts_id") Long postId,
                                                       @RequestBody PostUpdateRequestDto requestDto,
                                                       @AuthenticationPrincipal MemberDetails principal) {
        return ApiResponse.createSuccess(boardService.updatePost(postId, requestDto, principal));
    }

    @DeleteMapping("/detail/{products_id}/board/{posts_id}")
    public ApiResponse<PostCRUDResponseDto> deletePost(@PathVariable("products_id") Long productId,
                                                       @PathVariable("posts_id") Long postId,
                                                       @AuthenticationPrincipal MemberDetails principal) {
        return ApiResponse.createSuccess(boardService.deletePost(postId, principal));
    }


    @PutMapping("/detail/{products_id}/board/{posts_id}/comment/{comments_id}")
    public ApiResponse<CommentCRUDResponseDto> updateComment(@PathVariable("products_id") Long productId,
                                                             @PathVariable("posts_id") Long postId,
                                                             @PathVariable("comments_id") Long commentId,
                                                             @RequestBody CommentUpdateRequestDto requestDto) {
        return ApiResponse.createSuccess(boardService.updateComment(commentId, requestDto));
    }

    @DeleteMapping("/detail/{products_id}/board/{posts_id}/comment/{comments_id}")
    public ApiResponse<CommentCRUDResponseDto> deleteComment(@PathVariable("products_id") Long productId,
                                                             @PathVariable("posts_id") Long postId,
                                                             @PathVariable("comments_id") Long commentId) {
        return ApiResponse.createSuccess(boardService.deleteComment(commentId));
    }

}
