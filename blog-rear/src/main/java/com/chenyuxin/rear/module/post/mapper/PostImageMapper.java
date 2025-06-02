package com.chenyuxin.rear.module.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenyuxin.rear.module.post.model.entity.PostImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostImageMapper extends BaseMapper<PostImage> {
    List<Long> selectAllByPostId(Long postId);
}
