package com.sparta.oneandzerobest.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.exception.InvalidFileException;
import com.sparta.oneandzerobest.exception.NotFoundNewsfeedException;
import com.sparta.oneandzerobest.exception.NotFoundUserException;
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

    /**
     * 프로필 이미지 업로드 메서드
     * @param id : user id
     * @param file : file
     * @return :
     */
    @Transactional
    public ResponseEntity<String> uploadImageToProfile(Long id, MultipartFile file) {
        validFile(file); // 파일의 유효성 검사

        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundUserException()
        );

        Image image =  fileUploadAndSave(file); // file을 AWS S3에 업로드 후 DB에 image 저장

        user.setProfileImage(image); // AWS S3에 저장된 이미지 url 저장

        return ResponseEntity.ok().body("성공적으로 업로드되었습니다."); // 성공 메시지 반환
    }


    public ResponseEntity<String> uploadImageToNewsfeed(Long id, MultipartFile file) {
        validFile(file); // 파일의 유효성 검사

        newsfeedRepository.findById(id).orElseThrow(
                () -> new NotFoundNewsfeedException()
        );

        fileUploadAndSave(file); // file을 AWS S3에 업로드 후 DB에 image 저장

        return ResponseEntity.ok().body("성공적으로 업로드되었습니다."); // 성공 메시지 반환
    }

    private void validFile(MultipartFile file) {
        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) { // 파일의 유효성 검사
            throw new InvalidFileException();
        }
    }

    /**
     * AWS S3에 이미지 업로드 하는 메서드
     * @param file : 업로드 하는 파일
     * @return : AWS S3에 저장된 url 주소
     */
    private String uploadImage(MultipartFile file){

        // 파일의 이름 설정
        String filename= file.getOriginalFilename();
        String uniqueName = getUniqueFileName(filename);

        // s3에 업로드할 파일의 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        // s3에 파일 업로드
        try {
            amazonS3Client.putObject(bucketName, uniqueName, file.getInputStream(), metadata);
        } catch (Exception e) {
            throw new RuntimeException("S3 error: " + e.getMessage());
        }

        // s3에 저장된 url 주소 반환
        return amazonS3Client.getUrl(bucketName,uniqueName).toString();
    }

    /**
     * AWS S3에 올릴 이미지의 이름을 고유 이름으로 만들어주는 메서드
     * @param filename : 원래 파일의 이름
     * @return : 고유한 파일 이름을 반환
     */
    private String getUniqueFileName(String filename) {
        // 자신의 아이디 값을 넣어줌으로써 유니크 이름을 만들어준다.
        Optional<Long> maxId = imageRepository.findMaxid();
        if (maxId.isPresent()) {
            filename += Long.toString(maxId.get() + 1);
        } else {
            filename += Long.toString(1);
        }
        return filename;
    }

    /**
     * 파일을 AWS S3에 업로드하고 DB에 저장하는 메서드
     * @param file : 업로드하고자하는 파일
     * @return : 파일의 이미지 객체 반환
     */
    private Image fileUploadAndSave(MultipartFile file) {
        String url = uploadImage(file); // AWS S3로 이미지 업로드
        if (url == null) {
            throw new RuntimeException("S3 error: AWS S3로부터 url 주소를 받지 못했습니다.");
        }

        Image image = new Image(url); // AWS S3 url에 따른 Image Entity 생성
        imageRepository.save(image); // image 객체 DB에 저장

        return image;
    }
}
