package com.chenyuxin.rear.module.post.service;

import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.module.post.model.dto.CreatePostDto;
import com.chenyuxin.rear.module.post.model.dto.LikePostDto;
import com.chenyuxin.rear.module.post.model.vo.PagePostVo;

public interface PostService {
    /**
     * 创建帖子
     * @param createPostDto CreatePostDto
     */
    void createPost(CreatePostDto createPostDto);

    /**
     * 分页查询帖子
     * @param viewScope 访问权限
     * @param pageNum 第几页
     * @param pageSize 每页展示记录数
     * @return PageResult
     */
    PageResult<PagePostVo> pagePost(Integer viewScope, Integer pageNum, Integer pageSize);

    /**
     * 点赞帖子
     * @param likePostDto LikePostDto
     */
    void likePost(LikePostDto likePostDto);
}
