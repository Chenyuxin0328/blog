package com.chenyuxin.rear.module.file.service.Impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioService {

    @Resource
    private MinioClient minioClient;
    @Value("${minio.endpoint}")
    private String minioUrl;

    /**
     * 上传文件到指定桶
     * @param bucketName 桶名
     * @param inputStream 文件输入流
     * @param originalFileName 原始文件名，用于获取扩展名
     * @param contentType 文件内容类型，如 image/png
     * @return 文件访问URL
     * @throws Exception 上传异常
     */
    public String uploadFile(String bucketName, InputStream inputStream,long size,String originalFileName, String contentType) throws Exception {
        String fileExtension = getFileExtension(originalFileName);
        String fileName = UUID.randomUUID() + fileExtension;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(inputStream,size, -1)
                        .contentType(contentType)
                        .build()
        );

        // 返回URL，这里假设minio在localhost:9000，实际根据部署环境调整
        return String.format("%s/%s/%s", this.minioUrl,bucketName, fileName);
    }

    /**
     * 删除指定桶里的文件
     * @param bucketName 桶名
     * @param objectName 文件名
     * @throws Exception 删除异常
     */
    public void deleteFile(String imagePath) throws Exception{
        // 假设minioUrl是配置的MinIO基础地址，结尾没有斜杠
        String minioUrl = this.minioUrl; // 例如 "http://localhost:9000"

        if (!imagePath.startsWith(minioUrl)) {
            throw new IllegalArgumentException("非法的imagePath，不包含MinIO基础地址");
        }

        // 去掉前缀，剩余部分是 bucketName/objectName
        String path = imagePath.substring(minioUrl.length() + 1); // +1 去掉斜杠

        // 分割桶名和对象名
        int slashIndex = path.indexOf('/');
        if (slashIndex < 0) {
            throw new IllegalArgumentException("imagePath格式错误，无法解析bucket和object");
        }

        String bucketName = path.substring(0, slashIndex);
        String objectName = path.substring(slashIndex + 1);
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );

    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot != -1 ? fileName.substring(lastDot) : "";
    }
}
