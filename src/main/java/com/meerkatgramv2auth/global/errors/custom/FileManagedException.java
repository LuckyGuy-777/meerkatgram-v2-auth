package com.meerkatgramv2auth.global.errors.custom;

public class FileManagedException extends RuntimeException {
    public FileManagedException(String message) {

        // 부모 클래스의 생성자를 호출해줌
        super(message);
    }
}


// 커스텀 에러는 보통 RuntimeException을 상속받는다고 함
