package com.chenyuxin.rear.module.post.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chenyuxin.rear.common.util.BeanCopyUtil;
import com.chenyuxin.rear.module.post.mapper.PostCommentMapper;
import com.chenyuxin.rear.module.post.mapper.PostMapper;
import com.chenyuxin.rear.module.post.model.dto.MakeCommentDto;
import com.chenyuxin.rear.module.post.model.entity.Post;
import com.chenyuxin.rear.module.post.model.entity.PostComment;
import com.chenyuxin.rear.module.post.service.PostCommentService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostCommentServiceImpl implements PostCommentService {
    @Autowired
    private PostCommentMapper postCommentMapper;
    @Autowired
    private PostMapper postMapper;
    @Override
    public void makeComment(Long userId, MakeCommentDto makeCommentDto) {
        // 修改评论表
        userId = userId!=null?userId:0;
        PostComment postComment = BeanCopyUtil.copy(makeCommentDto, PostComment.class);
        postComment.setUserId(userId);
        postComment.setCreateTime(LocalDateTime.now());
        postComment.setIsDelete(0);
        postCommentMapper.insert(postComment);
        // 修改帖子表
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId,makeCommentDto.getPostId())
                .setSql("comment_count = comment_count + 1");
        postMapper.update(wrapper);
    }
}
