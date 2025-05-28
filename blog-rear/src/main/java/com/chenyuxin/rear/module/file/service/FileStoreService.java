package com.chenyuxin.rear.module.file.service;

import com.chenyuxin.rear.module.file.model.vo.FileStoreVo;
import org.springframework.web.multipart.MultipartFile;

public interface FileStoreService {
    /**
     * 上传文件
     * @param bucketName 桶名称
     * @param multipartFile 文件
     * @return FileStoreVo
     */
    FileStoreVo uploadFile(String bucketName,MultipartFile multipartFile);

    /**
     *  更新文件状态
     * @param fileId 文件id
     */
    void updateFileStatus(Long fileId);
}
