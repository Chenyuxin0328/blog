package com.chenyuxin.rear;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import jakarta.annotation.Resource;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@SpringBootTest
public class MinioTest {
    @Autowired
    private MinioClient minioClient;

    // 配置你的桶名
    private static final String BUCKET_NAME = "blog-doc";

    @Test
    public void testUploadFile() throws Exception {
        // 指定本地文件路径（你可以换成任意本地测试图片）
        String filePath = "/Users/chenyuxin/Downloads/github.png";
        System.out.println(Files.probeContentType(Paths.get(filePath)));

        // 生成唯一文件名，避免重复
        String fileName = UUID.randomUUID() + ".png";

        try (InputStream inputStream = new FileInputStream(filePath)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType("image/jpeg")
                            .build()
            );
            System.out.println("✅ 上传成功！URL: http://localhost:9000/" + BUCKET_NAME + "/" + fileName);
        }
    }
}
