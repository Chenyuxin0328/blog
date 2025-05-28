package com.chenyuxin.rear.module.doc.service;

import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.module.doc.model.dto.CreateCategoryDto;
import com.chenyuxin.rear.module.doc.model.dto.CreateDocDto;
import com.chenyuxin.rear.module.doc.model.vo.GetRecentDocVo;
import com.chenyuxin.rear.module.doc.model.vo.PageDocVo;
import com.chenyuxin.rear.module.doc.model.vo.SelectDocCategoryVo;
import com.chenyuxin.rear.module.doc.model.vo.UploadBackgroundVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocService {
    void createBlog( CreateDocDto createDocDto);

    UploadBackgroundVo uploadBlogBackground(MultipartFile multipartFile);

    void deleteDocById(Long docId);

    void createCategory(CreateCategoryDto createCategoryDto);

    void deleteCategoryById(Long categoryId);

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
}
