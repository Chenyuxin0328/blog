package com.chenyuxin.rear.module.post.service;

import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.module.post.model.dto.CreatePostDto;
import com.chenyuxin.rear.module.post.model.dto.LikePostDto;
import com.chenyuxin.rear.module.post.model.dto.UpdatePostDto;
import com.chenyuxin.rear.module.post.model.vo.AdminPagePostVo;
import com.chenyuxin.rear.module.post.model.vo.PagePostVo;

import java.util.List;

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

    /**
     * 根据id更新朋友圈
     * @param updatePostDto UpdatePostDto
     */
    void updatePost(UpdatePostDto updatePostDto);

    /**
     * 管理员分页查询朋友圈
     * @param search 搜索
     * @param pageNum 当前页数
     * @param pageSize 每页展示记录数
     * @return List<AdminPagePostVo>
     */
    PageResult<AdminPagePostVo> selectPageAdmin(String search, Integer pageNum, Integer pageSize);

    /**
     * 根据id删除帖子
     * @param postId 帖子id
     */
    void deletePostById(Long postId);
}
