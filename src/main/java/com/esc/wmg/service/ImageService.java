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

import ch.qos.logback.core.model.Model;

@Service
public class ImageService {

    private S3Config s3Config;

    public ImageService(S3Config s3Config) {

        this.s3Config = s3Config;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String localLocation = "C:\\Users\\smhrd\\Desktop\\실전 코드\\wmg\\src\\main\\resources\\static\\s3\\";
    // private String localLocation = "C:\\Users\\smhrd\\Desktop\\실전프로젝트_스프링부트\\ESC\\src\\main\\resources\\static\\s3\\";
    // private final String localLocation =
    // new File(System.getProperty("user.dir"), "src/main/resources/static/s3/").getAbsolutePath();

    public String imageUpload(MultipartRequest request) throws IOException {

        MultipartFile file = request.getFile("upload");

        // 이미지가 업로드되지 않은 경우 (null 또는 비어있음)
        if (file == null || file.isEmpty()) {
            return null; // 이미지 없이도 게시글 등록 가능
        }

        String fileName = file.getOriginalFilename();

        String ext = fileName.substring(fileName.indexOf("."));

        String uuidFileName = UUID.randomUUID() + ext;
        String localPath = localLocation + uuidFileName;

        File localFile = new File(localPath);
        file.transferTo(localFile);

        s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, uuidFileName, localFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        String s3Url = s3Config.amazonS3Client().getUrl(bucket, uuidFileName).toString();

        // 로컬 임시 파일 삭제
        localFile.delete();

        return s3Url;

    }
}