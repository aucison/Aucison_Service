package com.example.aucison_service.service.product;


import com.example.aucison_service.controller.AuthController;
import com.example.aucison_service.dto.board.*;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.product.*;
import com.example.aucison_service.service.member.MemberDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService{

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    PostsRepository postsRepository;
    CommentsRepository commentsRepository;

    ProductsRepository productsRepository;


    @Autowired
    public BoardServiceImpl(PostsRepository postsRepository, CommentsRepository commentsRepository,
                            ProductsRepository productsRepository) {
        this.productsRepository=productsRepository;
        this.commentsRepository=commentsRepository;
        this.postsRepository=postsRepository;
    }

    private void validatePrincipal(MemberDetails principal) {
        if (principal == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public List<PostListResponseDto> getBoardByProductId(Long productId,@AuthenticationPrincipal MemberDetails principal){
        validatePrincipal(principal);

        //조회를 할 떄 인증된 정보를 가진 녀석이기는 해야하지만 등록이나 다른 행위를 하는건 아니므로 필요가 없음
        //String userEmail = principal.getAttribute("email");
        //String email = principal.getMember().getEmail();

        List<PostsEntity> postsEntities = postsRepository.findByProductsEntity_ProductsId(productId);

        return postsEntities.stream()
                .map(postEntity -> {
                    PostListResponseDto postListResponseDto = PostListResponseDto.builder()
                            .posts_id(postEntity.getPostsId())
                            .title(postEntity.getTitle())
                            .content(postEntity.getContent())
                            .email(postEntity.getEmail())
                            .build();

                    List<CommentsEntity> commentsEntities = commentsRepository.findByPostsEntity_PostsId(postEntity.getPostsId());

                    List<CommentListResponseDto> commentListResponseDtos = commentsEntities.stream()
                            .map(commentEntity -> CommentListResponseDto.builder()
                                    .comments_id(commentEntity.getCommentsId())
                                    .content(commentEntity.getContent())
                                    .email(commentEntity.getEmail())
                                    .build())
                            .collect(Collectors.toList());

                    postListResponseDto.setComments(commentListResponseDtos);

                    return postListResponseDto;
                })
                .collect(Collectors.toList());
    }

    //게시물과 댓글에 대한 CRUD 서비스 코드
    //최대한 간결하고 직관성있고 통일성있게...
    @Transactional
    @Override
    public PostCRUDResponseDto registPost(Long productId, PostRegistRequestDto dto, @AuthenticationPrincipal MemberDetails principal){
        validatePrincipal(principal);

        ProductsEntity product = productsRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));


        // 입력값 검증 (예: 제목이나 내용이 비어 있지 않은지 확인)
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
                dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        //String email = principal.getAttribute("email");
        String email = principal.getMember().getEmail();

        PostsEntity post = PostsEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .email(email)
                .productsEntity(product)
                .build();

        // 'createdTime'이 자동으로 설정될 것이므로 필요 x


        PostsEntity savedPost = postsRepository.save(post);

        // savedPost에는 이제 데이터베이스에서 자동 생성된 ID가 포함되어 있음
        PostCRUDResponseDto responseDto = PostCRUDResponseDto.builder()
                .posts_id(savedPost.getPostsId())
                .build();

        return responseDto;
    }


    @Transactional
    @Override
    public CommentCRUDResponseDto registComment(Long postId, CommentRegistRequestDto dto, @AuthenticationPrincipal MemberDetails principal){
        validatePrincipal(principal);

        PostsEntity postEntity = postsRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // 입력값 검증 (예: 제목이나 내용이 비어 있지 않은지 확인)
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        //String email = principal.getAttribute("email");
        String email = principal.getMember().getEmail();

        CommentsEntity comment = CommentsEntity.builder()
                .content(dto.getContent())
                .email(email)
                .postsEntity(postEntity)
                .build();

        // 'createdTime'이 자동으로 설정될 것이므로 필요 x

        CommentsEntity savedComment = commentsRepository.save(comment);

        //이제 생성된 ID 있으므로
        CommentCRUDResponseDto responseDto = CommentCRUDResponseDto.builder()
                .comment_id(savedComment.getCommentsId())
                .build();

        return responseDto;
    }

    @Transactional
    @Override
    public PostCRUDResponseDto updatePost(Long postId, PostUpdateRequestDto postRequestDto, @AuthenticationPrincipal MemberDetails principal) {
        validatePrincipal(principal);

        PostsEntity postEntity = postsRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));


        postEntity.update(postRequestDto.getTitle(), postRequestDto.getContent());    //실제 수정 로직
        PostsEntity updatedPost = postsRepository.save(postEntity);


        return PostCRUDResponseDto.builder()
                .posts_id(updatedPost.getPostsId())
                .build();
    }

    @Transactional
    @Override
    public PostCRUDResponseDto deletePost(Long postId, @AuthenticationPrincipal MemberDetails principal) {
        validatePrincipal(principal);

        postsRepository.deleteById(postId);
        return PostCRUDResponseDto.builder().posts_id(postId).build();
    }

    @Transactional
    @Override
    public CommentCRUDResponseDto updateComment(Long commentId, CommentUpdateRequestDto commentRequestDto, @AuthenticationPrincipal MemberDetails principal) {
        validatePrincipal(principal);

        CommentsEntity comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        comment.update(commentRequestDto.getContent()); //실제 수정 로직

        CommentsEntity updatedComment = commentsRepository.save(comment);

        return CommentCRUDResponseDto.builder()
                .comment_id(updatedComment.getCommentsId())
                .build();
    }

    @Transactional
    @Override
    public CommentCRUDResponseDto deleteComment(Long commentId, @AuthenticationPrincipal MemberDetails principal) {
        validatePrincipal(principal);

        commentsRepository.deleteById(commentId);
        return CommentCRUDResponseDto.builder().comment_id(commentId).build();
    }
}