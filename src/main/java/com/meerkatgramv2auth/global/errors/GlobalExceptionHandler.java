package com.meerkatgramv2auth.global.errors;

import com.meerkatgramv2auth.global.response.constant.CustomResponseCode;
import com.meerkatgramv2auth.global.errors.custom.*;
import com.meerkatgramv2auth.global.response.GlobalRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;


// 이 클래스에, 내가만든 예외파일을 만든것을 넣어줘야, 예외시에, 내가 작성한 예외처리가 작동함
// 내가 만든 예외사항을 띄우기위해..

//  customErrorCode.name() : 상수명을 tostring() 을 통해 그대로 문자열로 받아옴.


// 내가 필요한 상수 하나만, 인수로 받아오면 됨.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<GlobalRes<Void>> generateErrorResponse(CustomResponseCode customResponseCode){
        return ResponseEntity.status(customResponseCode.getHttpStatus())
                .body(GlobalRes.<Void>from(customResponseCode));
    }



    @ExceptionHandler(NotRegisteredException.class)
    public ResponseEntity<GlobalRes<Void>> notRegisteredHandle(NotRegisteredException e){
        log.debug(CustomResponseCode.NOT_REGISTERED_ERROR.name(),e);
        return this.generateErrorResponse(CustomResponseCode.NOT_REGISTERED_ERROR);
    }



    // 인증에러
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GlobalRes<Void>> authenticationHandle(AuthenticationException e){

        log.debug(CustomResponseCode.UNAUTHENTICATED_ERROR.name(),e);
        return this.generateErrorResponse(CustomResponseCode.UNAUTHENTICATED_ERROR);
    }


    // 권한에러
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalRes<Void>> accessDeniedHandle(AccessDeniedException e){
        log.debug(CustomResponseCode.UNAUTHORIZED_ERROR.name(),e);
        return this.generateErrorResponse(CustomResponseCode.UNAUTHORIZED_ERROR);
    }




    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<GlobalRes<Void>> invalidTokenHandle(InvalidTokenException e){

        log.debug(CustomResponseCode.INVALID_TOKEN_ERROR.name(),e);
        return this.generateErrorResponse(CustomResponseCode.INVALID_TOKEN_ERROR);
    }

    @ExceptionHandler(DeletedRecordException.class)
    public ResponseEntity<GlobalRes<Void>> deletedRecordHandle(DeletedRecordException e){

        log.debug(CustomResponseCode.NOT_FOUND_DATA_ERROR.name(),e);
        return this.generateErrorResponse(CustomResponseCode.NOT_FOUND_DATA_ERROR);
    }


    @ExceptionHandler(DuplicatedRecordException.class)
    public ResponseEntity<GlobalRes<Void>> duplicatedRecordHandle(DuplicatedRecordException e){

        log.debug(CustomResponseCode.DUPLICATED_RECORD_ERROR.name(),e);
        return this.generateErrorResponse(CustomResponseCode.DUPLICATED_RECORD_ERROR);
    }





    // 매개변수 타입 불일치 예외를 처리함
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalRes<Void>> MethodArgumentTypeMismatchHandle(MethodArgumentTypeMismatchException e){

        log.debug(CustomResponseCode.INVALID_PARAMETER_ERROR.name(),String.format("%s : 필드를 확인해 주세요", e.getName()));
        return this.generateErrorResponse(CustomResponseCode.INVALID_PARAMETER_ERROR);


        // 특정필드 하나에 대한 에러가 발생했을때, validation exception 이 반환된다.
        // 프로퍼티 하나에 대해서만 반환됨.

    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalRes<Void>> methodArgumentNotValidHandle(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField, // 필드명
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "유효하지 않은 값입니다.",
                        (existing, replacement) -> existing // 중복 필드가 있을 경우 기존 값 유지
                ));

        log.debug(CustomResponseCode.INVALID_PARAMETER_ERROR.name(),errors);
        return this.generateErrorResponse(CustomResponseCode.INVALID_PARAMETER_ERROR);
    }



    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<GlobalRes<List<String>>> MethodArgumentNotValidHandle(MethodArgumentNotValidException e) {
    //     // 400 번 에러가 나왔을때, 내용을 적어주는거 같음.
    //     return ResponseEntity.status(400).body(
    //             // GlobalRes의 builder() 를 하는데, 데이터타입을 String 으로 해준다
    //             // 라는 내용
    //             // 데이터 여러개를 가져 오기 위해서, List 배열에 String 을 씀
    //             GlobalRes.<List<String>> builder()
    //                     .code("E21")
    //                     .message("요청 파라미터에 이상이 있습니다.")
    //                     .data(
    //                             e.getBindingResult()
    //                                     .getAllErrors()
    //                                     .stream()
    //                                     .map(item -> String.format("%s : 잘못된 값입니다. ", item.getObjectName()))
    //                                     .toList()
    //                     )
    //                     .build()
    //     );
    //
    // }


    @ExceptionHandler(FileManagedException.class)
    public ResponseEntity<GlobalRes<Void>> fileManagedHandle(FileManagedException e) {
        log.debug(CustomResponseCode.FILE_MANAGED_ERROR.name(),e);
        return this.generateErrorResponse(CustomResponseCode.FILE_MANAGED_ERROR);
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalRes<Void>> othersHandle(Exception e) {
        log.error(
                "시스템 에러:" , e
        );

        log.debug(CustomResponseCode.SYSTEM_ERROR.name(),e);
        return this.generateErrorResponse(CustomResponseCode.SYSTEM_ERROR);

    }


    // SQL 관련 에러들은, 이 예외를 통한다.
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<GlobalRes<Void>> sqlHandle(SQLException e) {
        log.error("DB 에러:" , e);

        log.debug(CustomResponseCode.DB_ERROR.name(),e);
        return this.generateErrorResponse(CustomResponseCode.DB_ERROR);

    }








}

// 400 번에, E21
// E21은, BAD REQUEST ERROR 라고함.

/*
* 에러 처리를 하는 클래스라는 걸 알리려면,
* @RestControllerAdvice 를 사용한다.
*
*
* 에러 로그를 남기기 위한
* @Slf4j 어노테이션을 사용함.
*
*
* String.format 는,
* %s 나 %d 같은 포멧 문자를 사용하기위해서 사용
*
* e.getStackTrace()
* 상세정보. 에러가 어떤 파일에 어떤라인에서 발생했는걸
* 상세하게 표현해주는 역할을함.
*
* */