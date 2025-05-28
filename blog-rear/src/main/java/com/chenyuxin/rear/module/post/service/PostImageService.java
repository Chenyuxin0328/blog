package com.chenyuxin.rear.module.post.service;

import com.chenyuxin.rear.module.post.model.vo.UploadImageVo;
import org.springframework.web.multipart.MultipartFile;

public interface PostImageService {
    /**
     * 上传朋友圈文件
     * @param multipartFile 文件
     * @return UploadImageVo
     */
    UploadImageVo uploadImage(MultipartFile multipartFile);

}
