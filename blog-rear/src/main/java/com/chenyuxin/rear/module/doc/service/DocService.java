package com.chenyuxin.rear.module.doc.service;

import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.module.doc.model.dto.CreateCategoryDto;
import com.chenyuxin.rear.module.doc.model.dto.CreateDocDto;
import com.chenyuxin.rear.module.doc.model.dto.UpdateCategoryDto;
import com.chenyuxin.rear.module.doc.model.dto.UpdateDocDto;
import com.chenyuxin.rear.module.doc.model.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocService {
    void createBlog( CreateDocDto createDocDto);

    UploadBackgroundVo uploadBlogBackground(MultipartFile multipartFile);

    void deleteDocById(Long docId);

    void createCategory(CreateCategoryDto createCategoryDto);

    void deleteCategoryById(Long categoryId);

    /**
     * 用户分页查询文档
     * @param pageNum 第几页
     * @param pageSize 每页展示的记录数
     * @param search 搜索词
     * @param categoryId 分类id
     * @param viewScope 权限范围
     * @return PageResult<PageDocVo>
     */
    PageResult<PageDocVo> pageDoc(Integer pageNum, Integer pageSize, String search, Long categoryId, Integer viewScope);

    /**
     * 查询技术文档分类
     * @return List<SelectDocCategoryVo>
     */
    List<SelectDocCategoryVo> selectDocCategory();

    /**
     * 查询近期五篇技术文档
     * @param viewScope 查看权限范围
     * @return List<GetRecentDocVo>
     */
    List<GetRecentDocVo> getRecentDoc(Integer viewScope);

    /**
     * 更新博客
     * @param updateDocDto UpdateDocDto
     */
    void updateDoc(UpdateDocDto updateDocDto);

    /**
     * 更新技术文档分类
     * @param updateCategoryDto UpdateCategoryDto
     */
    void updateCategory(UpdateCategoryDto updateCategoryDto);

    /**
     * 管理端分页查询技术文档
     * @param categoryId 分类id
     * @param search 搜索字段
     * @param pageNum 第几页
     * @param pageSize 每页展示记录数
     */
    PageResult<AdminPageDocVo> adminPageDoc(Long categoryId, String search, Integer pageNum, Integer pageSize);

}
