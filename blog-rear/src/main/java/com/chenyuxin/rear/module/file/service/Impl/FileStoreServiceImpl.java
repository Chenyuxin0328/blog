package com.chenyuxin.rear.module.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chenyuxin.rear.common.exception.BusinessException;
import com.chenyuxin.rear.common.exception.ErrorCode;
import com.chenyuxin.rear.module.file.constant.MinioBucket;
import com.chenyuxin.rear.module.file.mapper.FileStoreMapper;
import com.chenyuxin.rear.module.file.model.entity.FileStorage;
import com.chenyuxin.rear.module.file.model.vo.FileStoreVo;
import com.chenyuxin.rear.module.file.service.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FileStoreServiceImpl implements FileStoreService {
    @Autowired
    private FileStoreMapper fileStoreMapper;
    @Autowired
    private MinioService minioService;

    @Override
    public FileStoreVo uploadFile(String bucketName,MultipartFile multipartFile) {
        String imagePath;
        try{
            imagePath = minioService.uploadFile(bucketName,multipartFile.getInputStream(), multipartFile.getSize(),multipartFile.getOriginalFilename(),multipartFile.getContentType());
        }catch (Exception e){
            log.error("上传文件失败,bucket:{},filename:{},exception:",bucketName,multipartFile.getOriginalFilename(),e);
            throw new BusinessException(ErrorCode.UPLOAD_IMAGE_FAIL);
        }
        FileStorage fileStorage;
        try{
            fileStorage = FileStorage.builder()
                    .url(imagePath)
                    .status(0)
                    .createTime(LocalDateTime.now())
                    .build();
            fileStoreMapper.insert(fileStorage);
        }catch (Exception e){
            log.error("插入文件数据至mysql异常,imagePath:{},exception:",imagePath,e);
            try{
                minioService.deleteFile(imagePath);
            }catch (Exception e2){
                log.error("minio中有错误数据请手动删除,imagePath:{},Exception:",imagePath,e2);
            }
            throw new BusinessException(ErrorCode.UPLOAD_IMAGE_FAIL);
        }
        return new FileStoreVo(fileStorage.getId(),imagePath);
    }

    @Override
    public void updateFileStatus(Long fileId) {
        LambdaUpdateWrapper<FileStorage> fileWrapper = new LambdaUpdateWrapper<>();
        fileWrapper.eq(FileStorage::getId,fileId)
                .set(FileStorage::getStatus,1);
        fileStoreMapper.update(fileWrapper);
    }
}
