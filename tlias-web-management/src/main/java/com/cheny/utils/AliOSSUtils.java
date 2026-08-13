package com.cheny.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Data
@ConfigurationProperties(prefix = "alioss")
//批量读取配置文件里的自定义配置，不用单独写@value注入
public class AliOSSUtils {

    private String endpoint;
    private String region;
    private String bucketName;

    public String upload(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM")) + "_" + originalFilename;
        InputStream inputStream = multipartFile.getInputStream();


        OSSClient client = OSSClient.newBuilder()
                .credentialsProvider(new EnvironmentVariableCredentialsProvider())
                .region(region)
                .endpoint(endpoint)
                .build();

        PutObjectRequest request = PutObjectRequest.newBuilder()
                .bucket(bucketName)
                .key(fileName)
                .body(BinaryData.fromStream(inputStream))
                .build();
        client.putObject(request);

        // 拼接访问地址
        return String.format("https://%s.%s/%s", bucketName, endpoint.replace("https://", ""), fileName);
    }


}