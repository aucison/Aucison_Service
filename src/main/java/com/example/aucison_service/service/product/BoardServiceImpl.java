package com.example.aucison_service.service.product;


import com.example.aucison_service.controller.AuthController;
import com.example.aucison_service.dto.board.*;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.jpa.product.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

//    MemberServiceClient memberServiceClient;

    @Autowired
    public BoardServiceImpl(PostsRepository postsRepository, CommentsRepository commentsRepository,
                            ProductsRepository productsRepository) {
        this.productsRepository=productsRepository;
        this.commentsRepository=commentsRepository;
        this.postsRepository=postsRepository;
    }

//    @Override
//    public List<PostListResponseDto> getAllBoard() {
//        //게시글, 댓글 전부 보여주는 서비스
//
//        //모든 게시글 다 가져오고
//        List<PostsEntity> postsEntities = postsRepository.findAll();
//
//        return postsEntities.stream()
//                .map(postEntity -> {
//                    PostListResponseDto postListResponseDto = PostListResponseDto.builder()
//                            .posts_id(postEntity.getPostsId())
//                            .title(postEntity.getTitle())
//                            .content(postEntity.getContent())
//                            .createdTime(postEntity.getCreatedTime())
//                            .email(postEntity.getEmail())
//                            .build();
//
//                    List<CommentsEntity> commentsEntities = commentsRepository.findByPostsEntity_PostsId(postEntity.getPostsId());
//
//                    List<CommentListResponseDto> commentListResponseDtos = postEntity.getCommentsEntities()
//                            .stream()
//                            .map(commentEntity -> CommentListResponseDto.builder()
//                                    .comments_id(commentEntity.getCommentsId())
//                                    .content(commentEntity.getContent())
//                                    .createdTime(commentEntity.getCreatedTime())
//                                    .email(commentEntity.getEmail())
//                                    .build())
//                            .collect(Collectors.toList());
//
//                    postListResponseDto.setComments(commentListResponseDtos);
//
//                    return postListResponseDto;     //여기에 글-댓글 모두 모이게 됨
//                })
//                .collect(Collectors.toList());
//    }


    @Override
    public List<PostListResponseDto> getBoardByProductId(Long productId,@AuthenticationPrincipal OAuth2User principal){

        String userEmail = principal.getAttribute("email");

        if (principal == null) {
            logger.info("인증되지 않은 사용자입니다!");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

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
    public PostCRUDResponseDto registPost(Long productId, PostRegistRequestDto dto,@AuthenticationPrincipal OAuth2User principal){
        ProductsEntity product = productsRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));


        // 입력값 검증 (예: 제목이나 내용이 비어 있지 않은지 확인)
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
                dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        String email = principal.getAttribute("email");
        if (principal == null) {
            logger.info("인증되지 않은 사용자입니다!");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }


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
    public CommentCRUDResponseDto registComment(Long postId, CommentRegistRequestDto dto, @AuthenticationPrincipal OAuth2User principal){

        PostsEntity postEntity = postsRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // 입력값 검증 (예: 제목이나 내용이 비어 있지 않은지 확인)
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        String email = principal.getAttribute("email");

        if (principal == null) {
            logger.info("인증되지 않은 사용자입니다!");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

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

    @Override
    public PostCRUDResponseDto updatePost(Long postId, PostUpdateRequestDto postRequestDto) {
        PostsEntity postEntity = postsRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        postEntity.update(postRequestDto.getTitle(), postRequestDto.getContent());    //실제 수정 로직

        /*
        postsRepository.save(post);

        return PostCRUDResponseDto.builder().posts_id(postId).build();

         */

        PostsEntity updatedPost = postsRepository.save(postEntity);

        PostCRUDResponseDto responseDto = PostCRUDResponseDto.builder()
                .posts_id(updatedPost.getPostsId())
                .build();

        return responseDto;

        //위 식과 아래식은 큰 차이는 없지만 아래식이 좀 더 명시적이고 동기화 상태 보장함
        //내 생각에 간편한 식은 delete에 어울릴 듯
    }

    @Override
    public PostCRUDResponseDto deletePost(Long postId) {
        postsRepository.deleteById(postId);
        return PostCRUDResponseDto.builder().posts_id(postId).build();
    }

    @Override
    public CommentCRUDResponseDto updateComment(Long commentId, CommentUpdateRequestDto commentRequestDto) {
        CommentsEntity comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        comment.update(commentRequestDto.getContent()); //실제 수정 로직

        CommentsEntity updatedComment = commentsRepository.save(comment);

        CommentCRUDResponseDto responseDto = CommentCRUDResponseDto.builder()
                .comment_id(updatedComment.getCommentsId())
                .build();

        return responseDto;
    }

    @Override
    public CommentCRUDResponseDto deleteComment(Long commentId) {
        commentsRepository.deleteById(commentId);
        return CommentCRUDResponseDto.builder().comment_id(commentId).build();
    }


}