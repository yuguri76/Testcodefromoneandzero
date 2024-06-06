package com.sparta.oneandzerobest.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sparta.oneandzerobest.auth.entity.User;
import com.sparta.oneandzerobest.auth.repository.UserRepository;
import com.sparta.oneandzerobest.exception.InvalidFileException;
import com.sparta.oneandzerobest.exception.NotFoundImageException;
import com.sparta.oneandzerobest.exception.NotFoundNewsfeedException;
import com.sparta.oneandzerobest.exception.NotFoundUserException;
import com.sparta.oneandzerobest.newsfeed.entity.Newsfeed;
import com.sparta.oneandzerobest.newsfeed.repository.NewsfeedRepository;
import com.sparta.oneandzerobest.s3.entity.FileContentType;
import com.sparta.oneandzerobest.s3.entity.Image;
import com.sparta.oneandzerobest.s3.repository.ImageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final NewsfeedRepository newsfeedRepository;
    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 200 * 1024 * 1024;


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

    @Transactional
    public ResponseEntity<String> uploadImageToNewsfeed(Long id, MultipartFile file) {

        validFile(file);

        Newsfeed newsfeed = newsfeedRepository.findById(id).orElseThrow(
                () -> new NotFoundNewsfeedException()
        );

        if(newsfeed.getImageList().size()>=5)
            throw new InvalidFileException("한 게시글에 올릴 수 있는 미디어는 최대 5개입니다.");

        Image image = fileUploadAndSave(file); // file을 AWS S3에 업로드 후 DB에 image 저장
        newsfeed.setImage(image);

        return ResponseEntity.ok().body("성공적으로 업로드되었습니다."); // 성공 메시지 반환
    }

    /**
     * 게시글 수정 시 파일 수정
     * @param file
     * @param id
     * @param changeFileid
     * @return
     */
    @Transactional
    public ResponseEntity<String> updateImageToNewsfeed(MultipartFile file, Long id, Long changeFileid) {
        // 파일 유효성 검사
        validFile(file);

        Newsfeed newsfeed = newsfeedRepository.findById(id).orElseThrow(
            () -> new NotFoundNewsfeedException()
        );

        fileUpdateAndSave(file, changeFileid);

        return ResponseEntity.ok("성공적으로 수정되었습니다.");
    }


    /**
     * 파일 유효성 검사
     * @param file
     */
    private void validFile(MultipartFile file) {
        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
            throw new InvalidFileException("업로드 하려는 파일이 없습니다.");
        }
        String fileType = file.getContentType();
        long fileSize = file.getSize();
        FileContentType type = FileContentType.getContentType(fileType);
        if(type == null)
            throw new InvalidFileException("지원하지 않는 타입의 파일입니다.");

        switch (type){
            case JPG :
            case PNG:
            case JPEG:
                if(fileSize >MAX_IMAGE_SIZE)
                    throw new InvalidFileException("image파일은 최대 10MB까지 업로드 가능합니다.");
                break;
            case MP4:
            case AVI:
            case GIF:
                if(fileSize > MAX_VIDEO_SIZE)
                    throw new InvalidFileException("vidoe파일은 최대 200MB까지 업로드 가능합니다.");
                break;
            default:
                throw new InvalidFileException("");
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
        String url = getUUIDFileUrl(filename);

        // s3에 업로드할 파일의 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        // s3에 파일 업로드
        try {
            amazonS3Client.putObject(bucketName, url, file.getInputStream(), metadata);
        } catch (Exception e) {
            throw new RuntimeException("S3 error: " + e.getMessage());
        }

        // s3에 저장된 url 주소 반환
        return amazonS3Client.getUrl(bucketName,url).toString();
    }

    /**
     * AWS S3에 올릴 이미지의 이름을 고유 이름으로 만들어주는 메서드
     * @param filename : 원래 파일의 이름
     * @return : 고유한 파일 이름을 반환
     */
    private String getUUIDFileUrl(String filename) {
        return UUID.randomUUID().toString();
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

        Image image = new Image(file.getOriginalFilename(), url); // AWS S3 url에 따른 Image Entity 생성
        imageRepository.save(image); // image 객체 DB에 저장

        return image;
    }


    @Transactional
    public void fileUpdateAndSave(MultipartFile file, Long changeFileid) {

        // 새로운 이미지 저장
        String url = uploadImage(file);
        if(url == null) {
            throw new RuntimeException("S3 error: AWS S3로부터 url 주소를 받지 못했습니다.");
        }

        // 저장되어 있는 이미지 가져오기
        Image image = imageRepository.findById(changeFileid)
            .orElseThrow(NotFoundImageException::new);

        // s3에서 이전 이미지 삭제
        amazonS3Client.deleteObject(bucketName, image.getUrl());

        // 새 이미지 정보 수정
        image.setName(file.getOriginalFilename());
        image.setUrl(url);
    }


}
