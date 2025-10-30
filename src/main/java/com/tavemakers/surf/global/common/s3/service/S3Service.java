package com.tavemakers.surf.global.common.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.tavemakers.surf.global.common.s3.dto.PreSignedUrlResponse;
import com.tavemakers.surf.global.common.s3.exception.FileNameIsEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    public static final String ORIGINAL_PATH = "original/";
    public static final Integer PRESIGNED_EXPIRATION = 2;

    private final AmazonS3 s3Client;
    @Value("${cloud.aws.bucket-name}")
    private String bucketName;

    public List<PreSignedUrlResponse> generatePreSignedUrlList(List<String> fileNames) {
        validateFileName(fileNames);
        return fileNames.stream()
                .map(this::generateSinglePutPreSignedUrl)
                .toList();
    }

    private void validateFileName(List<String> fileNames) {
        if(fileNames == null || fileNames.isEmpty()) {
            throw new FileNameIsEmptyException();
        }
    }

    public PreSignedUrlResponse generateSinglePutPreSignedUrl(String filename) {
        String key = ORIGINAL_PATH + UUID.randomUUID() + "/" + filename;
        Date expiration = getExpiration();

        GeneratePresignedUrlRequest request =
                getPostGeneratePresignedUrlRequest(key, expiration);
        URL url = s3Client.generatePresignedUrl(request);

        return PreSignedUrlResponse.from(key, url.toString());
    }

    private GeneratePresignedUrlRequest getPostGeneratePresignedUrlRequest(String fileName, Date expiration) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest
                = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.POST)
                .withKey(fileName)
                .withExpiration(expiration);
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());
        return generatePresignedUrlRequest;
    }

    private Date getExpiration() {
        Instant expirationInstant = Instant.now().plus(PRESIGNED_EXPIRATION, ChronoUnit.MINUTES);
        return Date.from(expirationInstant);
    }

}
