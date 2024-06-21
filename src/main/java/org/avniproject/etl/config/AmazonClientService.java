package org.avniproject.etl.config;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.util.Date;

@Service
@ConditionalOnProperty(value = "aws.s3.enable", havingValue = "true", matchIfMissing = true)
public class AmazonClientService {

    @Value("${avni.bucket.name}")
    String bucketName;

    protected AmazonS3 s3Client;

    protected final long DOWNLOAD_EXPIRY_DURATION = Duration.ofHours(1).toMillis();

    @Autowired
    public AmazonClientService(@Value("${aws.access.key}") String accessKeyId,
                               @Value("${aws.secret.access.key}") String secretAccessKey){
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_SOUTH_1)
                .withPathStyleAccessEnabled(true)
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(accessKeyId, secretAccessKey)
                        )
                )
                .build();
    }


    public URL generateMediaDownloadUrl(String url) throws S3FileDoesNotExist {
        AmazonS3URI amazonS3URI = new AmazonS3URI(url);
        String objectKey = amazonS3URI.getKey();

        boolean exists = s3Client.doesObjectExist(bucketName, objectKey);
        if (!exists) {
            throw new S3FileDoesNotExist("File does not exist in S3 bucket: " + url);
        }

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(getExpireDate(DOWNLOAD_EXPIRY_DURATION));

        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }

    private Date getExpireDate(long expireDuration) {
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + expireDuration);
        return expiration;
    }
}
