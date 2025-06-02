package com.chenyuxin.rear.module.doc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenyuxin.rear.common.exception.BusinessException;
import com.chenyuxin.rear.common.exception.ErrorCode;
import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.common.util.BeanCopyUtil;
import com.chenyuxin.rear.module.doc.mapper.DocCategoryMapper;
import com.chenyuxin.rear.module.doc.mapper.DocMapper;
import com.chenyuxin.rear.module.doc.model.dto.CreateCategoryDto;
import com.chenyuxin.rear.module.doc.model.dto.CreateDocDto;
import com.chenyuxin.rear.module.doc.model.dto.UpdateCategoryDto;
import com.chenyuxin.rear.module.doc.model.dto.UpdateDocDto;
import com.chenyuxin.rear.module.doc.model.entity.Doc;
import com.chenyuxin.rear.module.doc.model.entity.DocCategory;
import com.chenyuxin.rear.module.doc.model.vo.*;
import com.chenyuxin.rear.module.doc.service.DocService;
import com.chenyuxin.rear.module.file.constant.MinioBucket;
import com.chenyuxin.rear.module.file.mapper.FileStoreMapper;
import com.chenyuxin.rear.module.file.model.entity.FileStorage;
import com.chenyuxin.rear.module.file.model.vo.FileStoreVo;
import com.chenyuxin.rear.module.file.service.FileStoreService;
import com.chenyuxin.rear.module.file.service.Impl.MinioService;
import com.chenyuxin.rear.module.stats.service.WeeklyStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocServiceImpl implements DocService {
    @Autowired
    private DocMapper docMapper;
    @Autowired
    private DocCategoryMapper docCategoryMapper;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private WeeklyStatsService weeklyStatsService;
    @Autowired
    private FileStoreMapper fileStoreMapper;

    // 创建新的文档
    @Transactional
    @Override
    public void createBlog(CreateDocDto createDocDto) {
        //1 插入文档表
        Doc doc = BeanCopyUtil.copy(createDocDto,Doc.class);
        doc.setCreateTime(LocalDateTime.now());
        doc.setIsDelete(0);
        doc.setLike(0);
        doc.setComment(0);
        doc.setUpdateTime(LocalDateTime.now());
        if(doc.getWriteTime()==null){
            doc.setWriteTime(LocalDate.now());
        }
        docMapper.insert(doc);
        //2 更新背景图片为已绑定
        fileStoreService.updateFileStatus(createDocDto.getBackgroundId());
        //3 分类使用次数
        LambdaUpdateWrapper<DocCategory> categoryWrapper = new LambdaUpdateWrapper<>();
        categoryWrapper.eq(DocCategory::getId,createDocDto.getCategoryId())
                .setSql("count = count + 1");
        docCategoryMapper.update(categoryWrapper);
        //4 增加文档数量
        weeklyStatsService.incrementDocCount();
    }
    // 上传文档背景图片
    @Transactional
    @Override
    public UploadBackgroundVo uploadBlogBackground(MultipartFile multipartFile) {
        FileStoreVo fileStoreVo = fileStoreService.uploadFile(MinioBucket.BLOG_DOC.getBucketName(), multipartFile);
        return BeanCopyUtil.copy(fileStoreVo, UploadBackgroundVo.class);
    }
    // 根据id删除文档
    @Override
    public void deleteDocById(Long docId) {
        LambdaUpdateWrapper<Doc> wrapper = new LambdaUpdateWrapper<>();
           wrapper.eq(Doc::getId,docId)
                   .set(Doc::getIsDelete,1);
        docMapper.update(null,wrapper);
    }
    // 创建分类
    @Override
    public void createCategory(CreateCategoryDto createCategoryDto) {
        DocCategory docCategory = new DocCategory();
        docCategory.setCategoryName(createCategoryDto.getCategoryName());
        docCategory.setCount(0);
        docCategory.setIsDelete(0);
        docCategory.setCreateTime(LocalDateTime.now());
        docCategoryMapper.insert(docCategory);
    }
    // 删除分类
    @Override
    public void deleteCategoryById(Long categoryId) {
        DocCategory docCategory = docCategoryMapper.selectById(categoryId);
        if(docCategory.getCount()>0){
            throw new BusinessException(ErrorCode.DELETE_CATEGORY_FAIL);
        }
        LambdaUpdateWrapper<DocCategory> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(DocCategory::getId,categoryId)
                .le(DocCategory::getCount,0)
                .set(DocCategory::getIsDelete,1);
        docCategoryMapper.update(wrapper);
    }
    // 分页查询帖子
    @Override
    public PageResult<PageDocVo> pageDoc(Integer pageNum, Integer pageSize, String search, Long categoryId, Integer viewScope) {
        Page<PageDocVo> page = new Page<>(pageNum, pageSize);
        IPage<PageDocVo> pageDocVoIPage;
        if(categoryId!=null){
            pageDocVoIPage = docMapper.selectPageDocVo(page, search, categoryId, viewScope);
        }else {
            pageDocVoIPage = docMapper.selectPageDocVoDesc(page, search, categoryId, viewScope);
        }
        return new PageResult<>(pageNum,pageSize,pageDocVoIPage.getTotal(),pageDocVoIPage.getRecords());
    }

    @Override
    public List<SelectDocCategoryVo> selectDocCategory() {
        LambdaQueryWrapper<DocCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocCategory::getIsDelete,0);
        List<SelectDocCategoryVo> docCategories = docCategoryMapper.selectList(wrapper)
                .stream().map(docCategory ->
                    BeanCopyUtil.copy(docCategory,SelectDocCategoryVo.class)
        ).toList();
        return docCategories;
    }

    @Override
    public List<GetRecentDocVo> getRecentDoc(Integer viewScope) {
        LambdaUpdateWrapper<Doc> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Doc::getIsDelete,0)
                .le(Doc::getViewScope,viewScope)
                .orderByDesc(Doc::getWriteTime);
        Page<Doc> page = new Page<>(1, 5);
        List<Doc> records = docMapper.selectPage(page, wrapper).getRecords();
        List<GetRecentDocVo> getRecentDocVos = records.stream()
                .map(doc -> BeanCopyUtil.copy(doc,GetRecentDocVo.class) )
                .toList();
        return getRecentDocVos;
    }
    // 更新博客记录
    @Override
    public void updateDoc(UpdateDocDto updateDocDto) {
        // 更新技术文档
        Doc doc = docMapper.selectById(updateDocDto.getId());
        Long oldBackgroundId = doc.getBackgroundId();
        LambdaUpdateWrapper<Doc> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Doc::getId,updateDocDto.getId())
                .set(Doc::getBackgroundId,updateDocDto.getBackgroundId())
                .set(Doc::getTitle,updateDocDto.getTitle())
                .set(Doc::getGeneral,updateDocDto.getGeneral())
                .set(Doc::getCategoryId,updateDocDto.getCategoryId())
                .set(Doc::getUrl,updateDocDto.getUrl())
                .set(Doc::getWriteTime,updateDocDto.getWriteTime())
                .set(Doc::getViewScope,updateDocDto.getViewScope())
                .set(Doc::getUpdateTime,LocalDateTime.now());
        docMapper.update(wrapper);
        // 修改文件状态
        if(!Objects.equals(oldBackgroundId, updateDocDto.getBackgroundId())){
            LambdaUpdateWrapper<FileStorage> fileWrapper = new LambdaUpdateWrapper<>();
            fileWrapper.eq(FileStorage::getId,oldBackgroundId)
                    .set(FileStorage::getStatus,0);
            fileStoreMapper.update(fileWrapper);
        }
    }
    // 更新分类名称
    @Override
    public void updateCategory(UpdateCategoryDto updateCategoryDto) {
        LambdaUpdateWrapper<DocCategory> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(DocCategory::getId,updateCategoryDto.getId())
                .set(DocCategory::getCategoryName,updateCategoryDto.getCategoryName());
        docCategoryMapper.update(wrapper);
    }
    // 管理端分页查询技术文档
    @Override
    public PageResult<AdminPageDocVo> adminPageDoc(Long categoryId, String search, Integer pageNum, Integer pageSize) {
        Page<AdminPageDocVo> page = new Page<>(pageNum, pageSize);
        IPage<AdminPageDocVo> pageDocVoIPage = docMapper.adminPageDoc(page,categoryId,search,pageNum,pageSize);
        return new PageResult<AdminPageDocVo>(pageNum,pageSize,pageDocVoIPage.getTotal(),pageDocVoIPage.getRecords());
    }
}
