package com.esc.wmg.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.esc.wmg.config.S3Config;

import jakarta.transaction.Transactional;

@Service
public class ImageService {

    private S3Config s3Config;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${file.upload.dir}")
    private String localLocation;

    public ImageService(S3Config s3Config) {

        this.s3Config = s3Config;
    }

    @Transactional
    public String imageUpload(MultipartRequest request) throws IOException {

        MultipartFile file = request.getFile("upload");

        if (file == null || file.isEmpty()) {
            return null;
        }

        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf("."));
        String uuidFileName = UUID.randomUUID() + ext;

        // 디렉토리 생성 (없으면 자동 생성)
        File dir = new File(localLocation);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File localFile = new File(dir, uuidFileName);
        file.transferTo(localFile);

        s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, uuidFileName, localFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        String s3Url = s3Config.amazonS3Client().getUrl(bucket, uuidFileName).toString();

        localFile.delete();

        return s3Url;
    }

}
