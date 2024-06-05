package com.sparta.oneandzerobest.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import com.sparta.oneandzerobest.s3.entity.Image;
import com.sparta.oneandzerobest.s3.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final NewsfeedRepository newsfeedRepository;
    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Transactional
    public ResponseEntity<String> uploadImageToProfile(Long id, MultipartFile file) {
        if(!validFile(file)){
            return ResponseEntity.badRequest().body("잘못된 응답입니다.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        String url = uploadImage(file);
        if (url == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Image entity 생성 후 저장
        Image image = new Image(url);
        imageRepository.save(image);

        user.setProfileImage(image);

        return ResponseEntity.ok().body("성공적으로 업로드되었습니다.");
    }


    public ResponseEntity<String> uploadImageToNewsfeed(Long id, MultipartFile file) {

        if(!validFile(file)){
            return ResponseEntity.badRequest().body("잘못된 응답입니다.");
        }
        newsfeedRepository.findById(id).orElseThrow(() -> new RuntimeException("뉴스피드가 존재하지 않습니다."));

        String url = uploadImage(file);
        if (url == null) {
            return ResponseEntity.badRequest().body(("성공적으로 업로드되었습니다."));
        }

        // Image entity 생성 후 저장
        Image image = new Image(url);
        imageRepository.save(image);

        return ResponseEntity.badRequest().body("잘못된 응답입니다.");
    }

    private Boolean validFile(MultipartFile file) {
        // 파일 유효성 체크
        return !file.isEmpty() && !Objects.isNull(file.getOriginalFilename());
    }

    private String uploadImage(MultipartFile file){

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
        } catch (Exception e) {
            // 예외처리
            System.out.println("uploadImage err: " + e.getMessage());
            return null;
        }

        // url 주소 반환
        return amazonS3Client.getUrl(bucketName,uniqueName).toString();
    }

    private String getUniqeFileName(String filename) {
        Optional<Long> maxId = imageRepository.findMaxid();
        if (maxId.isPresent()) {
            filename += Long.toString(maxId.get());
        } else {
            filename += Long.toString(1);
        }
        return filename;
    }
}
