package com.example.nonmembers.board.post.service;

import com.example.nonmembers.board.post.db.PostEntity;
import com.example.nonmembers.board.post.db.PostRepository;
import com.example.nonmembers.board.post.model.PostRequest;
import com.example.nonmembers.board.post.model.PostViewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    
    public PostEntity create(PostRequest postRequest){
        var entity = PostEntity.builder().postId(1L)// 1번
                .userName(postRequest.getUserName()).password(postRequest.getPassword()).email(postRequest.getEmail()).
                status("REGISTERED").title(postRequest.getTitle()).content(postRequest.getContent())
                .postedSt(LocalDateTime.now()).build();

        return postRepository.save(entity);
    }

    public PostEntity view(PostViewRequest postViewRequest) {
        return postRepository.findFirstByIdAndStatusOrderByIdDesc(postViewRequest.getPostId(),"REGISTERED")
                .map(it->{
            if(!it.getPassword().equals(postViewRequest.getPassword())){
                var format = "패스워드가 맞지 않습니다.%s vs %s";
                throw new RuntimeException(String.format(format, it.getPassword(), postViewRequest.getPassword()));
            }
            return it;
            //Optional이 비어있는 경우 예외를 발생
        }).orElseThrow(
                ()-> {
                    return new RuntimeException("해당 게시글이 존재하지 않습니다.: "+postViewRequest.getPostId());
                }
        );
    }

    public List<PostEntity> all() {
        return postRepository.findAll();
    }
    public void delete(PostViewRequest postViewRequest){
        postRepository.findById(postViewRequest.getPostId())
                .map(it->{
                    if(!it.getPassword().equals(postViewRequest.getPassword())){
                        var format = "패스워드가 맞지 않습니다.%s vs %s";
                        throw new RuntimeException(String.format(format, it.getPassword(), postViewRequest.getPassword()));
                    }
                    it.setStatus("UNREGISTERED");
                    postRepository.save(it);
                    return it;
                }).orElseThrow(
                        ()-> {
                           return new RuntimeException("해당 게시글이 존재하지 않습니다.: "+postViewRequest.getPostId());
                        }
                );
    }
}
