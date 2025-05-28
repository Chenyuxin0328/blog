package com.chenyuxin.rear.module.post.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.common.util.BeanCopyUtil;
import com.chenyuxin.rear.module.doc.model.vo.PageDocVo;
import com.chenyuxin.rear.module.file.service.FileStoreService;
import com.chenyuxin.rear.module.post.mapper.PostImageMapper;
import com.chenyuxin.rear.module.post.mapper.PostMapper;
import com.chenyuxin.rear.module.post.model.dto.CreatePostDto;
import com.chenyuxin.rear.module.post.model.dto.LikePostDto;
import com.chenyuxin.rear.module.post.model.entity.Post;
import com.chenyuxin.rear.module.post.model.entity.PostImage;
import com.chenyuxin.rear.module.post.model.vo.PagePostVo;
import com.chenyuxin.rear.module.post.service.PostService;
import com.chenyuxin.rear.module.stats.service.WeeklyStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostImageMapper postImageMapper;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private WeeklyStatsService weeklyStatsService;
    // 发布帖子
    @Transactional
    @Override
    public void createPost(CreatePostDto createPostDto) {
        // Post表
        Post post = BeanCopyUtil.copy(createPostDto, Post.class);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setIsDelete(0);
        post.setCreateTime(LocalDateTime.now());
        postMapper.insert(post);
        Long postId = post.getId();
        // 处理帖子图片相关
        int sort = 1;
        for (Long imageId : createPostDto.getImageIds()) {
            PostImage postImage = new PostImage();
            postImage.setPostId(postId);
            postImage.setFileId(imageId);
            postImage.setSortOrder(sort++);
            fileStoreService.updateFileStatus(imageId);
            postImageMapper.insert(postImage);
        }
        // 更新帖子数量
        weeklyStatsService.incrementPostCount();
    }
    // 分页查询帖子
    @Override
    public PageResult<PagePostVo> pagePost(Integer viewScope, Integer pageNum, Integer pageSize) {
        int total = postMapper.countByViewScope(viewScope);
        int offset = (pageNum - 1) * pageSize;
        List<PagePostVo> pagePostVos = postMapper.selectPagePost(viewScope,offset,pageSize);
        return new PageResult<>(pageNum,pageSize,total,pagePostVos);
    }

    @Override
    public void likePost(LikePostDto likePostDto) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId,likePostDto.getPostId())
                .setSql("like_count = like_count + "+likePostDto.getCount());
        postMapper.update(wrapper);
    }
}
