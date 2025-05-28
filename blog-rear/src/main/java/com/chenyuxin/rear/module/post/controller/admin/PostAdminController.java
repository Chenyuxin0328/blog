package com.chenyuxin.rear.module.post.controller.admin;

import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.common.validation.ImageFile;
import com.chenyuxin.rear.module.post.model.dto.CreatePostDto;
import com.chenyuxin.rear.module.post.model.vo.UploadImageVo;
import com.chenyuxin.rear.module.post.service.PostImageService;
import com.chenyuxin.rear.module.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/post/admin")
public class PostAdminController {
    @Autowired
    private PostImageService postImageService;
    @Autowired
    private PostService postService;
    // 上传朋友圈图片
    @PostMapping("/img")
    public Result<UploadImageVo> uploadImage(@ImageFile @RequestParam("file") MultipartFile multipartFile){
        UploadImageVo uploadImageVo = postImageService.uploadImage(multipartFile);
        return Result.success(uploadImageVo);
    }
    // 发布朋友圈
    @PostMapping
    public Result<Void> createPost(@RequestBody CreatePostDto createPostDto){
        postService.createPost(createPostDto);
        return Result.success();
    }
}
