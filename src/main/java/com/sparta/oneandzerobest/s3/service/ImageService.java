package com.sparta.oneandzerobest.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.sparta.oneandzerobest.s3.config.S3Config;
import com.sparta.oneandzerobest.s3.entity.Image;
import com.sparta.oneandzerobest.s3.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;
    private AmazonS3Client amazonS3Client;

    private final S3Config s3Config;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile image) {
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
//            throw new S3Exception(ErrorCode.EMPTY_FILE_EXCEPTION);
            return null;
        }
        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image) {
        this.validateImageFileExtention(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
//            throw new S3Exception(ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
            return null;
        }
    }

    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
//            throw new S3Exception(ErrorCode.NO_FILE_EXTENTION);
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif", "mp4", "avi");

        if (!allowedExtentionList.contains(extention)) {
//            throw new S3Exception(ErrorCode.INVALID_FILE_EXTENTION);
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; //변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extention);
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try{
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest); // put image to S3
        }catch (AmazonServiceException e) {
            // AWS S3에서 발생하는 서비스 관련 예외 처리
            throw new RuntimeException("S3 Service Exception: " + e.getMessage(), e);
        } catch (SdkClientException e) {
            // AWS SDK 클라이언트 관련 예외 처리
            throw new RuntimeException("S3 Client Exception: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected Exception: " + e.getMessage(), e);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    public ResponseEntity<String> uploadImageToNewsfeed(Long id, MultipartFile file) {

        // 파일의 이름 설정
        String filename= file.getOriginalFilename();
        String uniqueName = getUniqeFileName(filename);
        // s3에 업로드할 파일의 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        // s3에 파일 업로드
        try {
            amazonS3Client.putObject(bucketName, uniqueName, file.getInputStream(), metadata);
        } catch (IOException e) {
            // 예외처리
            return ResponseEntity.badRequest().body("잘못된 응답입니다.");
        }

        // 업로드한 파일의 주소
        String url = amazonS3Client.getUrl(bucketName,uniqueName).toString();

        // Image entity 생성 후 저장
        Image image = new Image(url);
        imageRepository.save(image);

        return ResponseEntity.ok("성공적으로 업로드 되었습니다 : "+url);
    }

    private String getUniqeFileName(String filename) {
        return filename;
    }
}
