package com.chenyuxin.rear.module.doc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenyuxin.rear.module.doc.model.entity.Doc;
import com.chenyuxin.rear.module.doc.model.vo.PageDocVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface DocMapper extends BaseMapper<Doc> {
    IPage<PageDocVo> selectPageDocVo(Page<?> page, @Param("search")String search,@Param("categoryId") Long categoryId,@Param("viewScope") Integer viewScope);

    IPage<PageDocVo> selectPageDocVoDesc(Page<?> page, @Param("search")String search,@Param("categoryId") Long categoryId,@Param("viewScope") Integer viewScope);
}
