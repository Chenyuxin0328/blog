package com.chenyuxin.rear.module.doc.controller.admin;

import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.common.validation.ImageFile;
import com.chenyuxin.rear.module.doc.model.dto.CreateCategoryDto;
import com.chenyuxin.rear.module.doc.model.dto.CreateDocDto;
import com.chenyuxin.rear.module.doc.model.vo.UploadBackgroundVo;
import com.chenyuxin.rear.module.doc.service.DocService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/doc/admin")
public class DocAdminController {
    @Autowired
    private DocService docService;
    // 创建文档
    @PostMapping("/create")
    public Result<Void> createDoc(@Valid @RequestBody CreateDocDto createDocDto){
        docService.createBlog(createDocDto);
        return Result.success();
    }
    // 上传文档背景图片
    @PostMapping("/background")
    public Result<UploadBackgroundVo> uploadDocBackground(@ImageFile @RequestParam("file")MultipartFile multipartFile){
        UploadBackgroundVo backGroundUrl = docService.uploadBlogBackground(multipartFile);
        return Result.success(backGroundUrl);
    }
    // 删除文档
    @DeleteMapping("/{docId}")
    public Result<UploadBackgroundVo> deleteDoc(@PathVariable("docId") Long docId){
        docService.deleteDocById(docId);
        return Result.success();
    }
    // 创建分类
    @PostMapping("/category")
    public Result<Void> createCategory(@RequestBody CreateCategoryDto createCategoryDto){
        docService.createCategory(createCategoryDto);
        return Result.success();
    }
    // 删除分类
    @DeleteMapping("/category/{categoryId}")
    public Result<Void> deleteCategoryById(@PathVariable("categoryId") Long categoryId){
        docService.deleteCategoryById(categoryId);
        return Result.success();
    }
}
