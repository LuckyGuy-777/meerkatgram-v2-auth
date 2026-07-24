package com.meerkatgramv2auth.global.response;


import com.meerkatgramv2auth.global.response.constant.CustomResponseCode;

public record GlobalResponseDTO<T>(
        String code
        ,String message,
        T data
) {

    public static<T> GlobalResponseDTO<T> from(CustomResponseCode customResponseCode, T data) {
        return new GlobalResponseDTO<T>(customResponseCode.getCode(), customResponseCode.name(),data);
    }

    // 오버로딩(파라미터가 다르다)
    public static GlobalResponseDTO<Void> from(CustomResponseCode customResponseCode) {
        return new GlobalResponseDTO<Void>(customResponseCode.getCode(), customResponseCode.name(),null);
    }

    public static <T> GlobalResponseDTO<T> success(T data){
        return GlobalResponseDTO.<T>from(CustomResponseCode.SUCCESS, data);
    }


    public static GlobalResponseDTO<Void> success() {
        return GlobalResponseDTO.<Void>from(CustomResponseCode.SUCCESS);
    }

}


// 외부에서 타입을 받는, 제네릭스를 지정하려면..
/*
* GlobalRes 처럼, 메소드 선언부에,  를 넣어줘야하고,
* 프로퍼티에 T 를 주어야한다.
*
* 같은 클래스 내, 이름은 같고, 파라미터나 반환값이 다른것 : 오버로딩
*
* 강타입 언어 는 다 오버로딩이 있다고함.
*
*
*
*  return new GlobalRes<T>(customErrorCode.getCode(), customErrorCode.name(),data);
*  -> argument 의 순서가 의미가 있나? 의미가 있다. 순서는 바뀌면 안된다.
*
* 왜냐하면, GlobalRes<T> 에서, code,message, data 순으로 값을 받는 생성자가
* 눈에 보이지 않지만, 존재하고 있다.
* */