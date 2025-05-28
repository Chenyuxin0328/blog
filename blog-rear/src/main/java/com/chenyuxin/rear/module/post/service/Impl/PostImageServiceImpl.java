package com.chenyuxin.rear.module.post.service.Impl;

import com.chenyuxin.rear.common.util.BeanCopyUtil;
import com.chenyuxin.rear.module.file.constant.MinioBucket;
import com.chenyuxin.rear.module.file.model.vo.FileStoreVo;
import com.chenyuxin.rear.module.file.service.FileStoreService;
import com.chenyuxin.rear.module.post.model.vo.UploadImageVo;
import com.chenyuxin.rear.module.post.service.PostImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostImageServiceImpl implements PostImageService {
    @Autowired
    private FileStoreService fileStoreService;

    @Override
    public UploadImageVo uploadImage(MultipartFile multipartFile) {
        FileStoreVo fileStoreVo = fileStoreService.uploadFile(MinioBucket.FRIEND_CIRCLE.getBucketName(), multipartFile);
        return BeanCopyUtil.copy(fileStoreVo, UploadImageVo.class);
    }
}
