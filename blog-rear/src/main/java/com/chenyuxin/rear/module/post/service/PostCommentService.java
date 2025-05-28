package com.chenyuxin.rear.module.post.service;

import com.chenyuxin.rear.module.post.model.dto.MakeCommentDto;

public interface PostCommentService {
    /**
     * 用户评论
     * @param userId 用户id
     * @param makeCommentDto MakeCommentDto
     */
    void makeComment(Long userId, MakeCommentDto makeCommentDto);

}
