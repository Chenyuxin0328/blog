package com.chenyuxin.rear.module.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chenyuxin.rear.module.post.model.entity.Post;
import com.chenyuxin.rear.module.post.model.vo.PagePostVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    // 分页查询朋友圈
    List<PagePostVo> selectPagePost(Integer viewScope,Integer offset,Integer pageSize);
    // 获取分页可见范围内朋友圈总数
    int countByViewScope(Integer viewScope);
    // 获取总点赞和评论数
    Map<String, Object> getLikeAndCommentStats();
}
