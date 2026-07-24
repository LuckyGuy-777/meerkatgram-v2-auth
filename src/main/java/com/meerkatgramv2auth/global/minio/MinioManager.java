package com.meerkatgramv2auth.global.minio;


import com.meerkatgramv2auth.global.errors.custom.FileManagedException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioManager {

    private final MinioConfig minioConfig;
    private final MinioClient minioClient;


    /*
    * 파일 확장자 추출 및 파일검증
    * @param file
    * @return fileExtension
    * */

    public String extractExtension(MultipartFile file) {

        //파일 존재 체크
        if(file == null || file.isEmpty()) {
            throw new FileManagedException("파일 업로드 실패: 파일 없음");
        }

        // 파일 확장자 검증. ( .확장자 ) 가 없는 경우를 판단함.
        String fileName = file.getOriginalFilename();
        if(fileName == null || !fileName.contains(".")){
            throw new FileManagedException("파일 업로드 실패: 파일명 이상");
        }

        // 파일의 마지막 . 에 + 1 한것을 가져와서, 소문자로 바꾸겠다
        // -> 유저가, 확장자로 대문자를 보낼수도 있는데, 그것을 받아서, 소문자로 바꾼다 라는 구문
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();

        // 허용확장자 검증
        if(!minioConfig.allowImageExtensions().contains("image/"+ fileExtension)) {
            throw new FileManagedException("파일 업로드 실패: 허용하지 않는 확장자");
        }


        return fileExtension;
    }


    /*
    * 랜덤한 파일명 생성(확장자 없이)
    * @return 'yyyyMMdd_파일명' 형식
    *
    * */
    public String generateFileName() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now();

        return now.format(dateFormatter) + "_" + UUID.randomUUID();
    }

    public String generateObjectKey(MultipartFile file) {
        //파일 Path 생성
        Path path = Path.of(minioConfig.minioProfilePath(), this.generateFileName() + "." + this.extractExtension(file));
        return path.toString().replace(File.separator, "/");
    }

    public void uploadFile(String objectKey, MultipartFile file) {
        // inputStream 파일작성 준비.
        try(InputStream inputStream = file.getInputStream()) {
            minioClient
                    // 파일이, 파일서버로, 업로드 요청이 들어가는 로직. 실패하면 catch 로 감
                    .putObject(
                            PutObjectArgs.builder()
                                    // 실제로 담고싶은 정보 추가.
                                    .bucket(minioConfig.minioBucket()) // 파일이 저장될 MinIO의 버킷명
                                    .object(objectKey)  // 버킷 내부에서 관리될 전체 저장 경로
                                    .stream(inputStream, file.getSize(), -1)
                                    .contentType(file.getContentType())
                                    .build()
                    );
        } catch (Exception e) {
            throw new FileManagedException("파일 업로드 실패: MinIO 업로드 실패, "+objectKey);
        }
    }

    public String createMinioObjectUri(String objectKey) {

        // 도메인 경로를 만드는게 아닌, 컴퓨터영역의 os 영역의 path 를 만드는거라고 함.
        // 컴퓨터의, /storage/imge/file.jpg 와 같은 경로를 만드는 path 객체
        Path path = Path.of(minioConfig.minioBucket(), objectKey);

        // 도메인 하고, 해당 path 를 합쳐주는것.
        // 도메인에 path를 .env 파일에 잘 적어두었다는 가정하에,
        // .env에 있는 경로와 + path 의 경로를 합쳐줌
        return String.format(
           "%s/%s",
           minioConfig.minioEndpoint(),
           path.toString().replace(File.separator, "/")
        );
    }

}
