package com.meerkatgramv2auth.global.errors.custom;

public class DuplicatedRecordException extends RuntimeException {
    public DuplicatedRecordException(String message) {

        // 부모 클래스의 생성자를 호출해줌
        super(message);
    }
}
