package com.berkaykomur.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MessagesType {

    TOKEN_IS_INVALID("1001", "Token bulunamadı", HttpStatus.UNAUTHORIZED),
    TOKEN_IS_EXPIRED("1008", "Tokenin süresi dolmuş", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("1010", "Böyle bir refresh token bulunamadı", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_IS_EXPIRED("1009", "Refresh tokenin süresi dolmuş", HttpStatus.UNAUTHORIZED),
    USERNAME_NOT_FOUND("1006", "Username bulunamadı", HttpStatus.NOT_FOUND),
    MEMBER_NOT_FOUND("1015","Member bulunamadı", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("1016","User bulunamadı",HttpStatus.NOT_FOUND),
    USERNAME_OR_PASSWORD_INVALID("1007", "Kullanıcı adı veya şifre geçersiz", HttpStatus.UNAUTHORIZED),
    USERNAME_ALREADY_TAKEN("1025", "Kullanıcı adı alınmış", HttpStatus.CONFLICT),
    MAX_BOOK_LIMIT_EXCEEDED("1012","Maksimum kitap alımına ulaştınız ",HttpStatus.BAD_REQUEST),
    OVERDUE_LIMIT_EXCEEDED("1014","Teslim tarihi geçmiş kitabınız bulunmaktatır kitap alamazsınız",HttpStatus.BAD_REQUEST),
    NO_RECORD_EXIST("1004", "Kayıt bulunamadı", HttpStatus.NOT_FOUND),
    ALREADY_LOANED("1013", "Bu kitap başkası tarafından zaten ödünç alınmış", HttpStatus.CONFLICT),
    UNAUTHORIZED_ACTIO("1012", "Bunu yapmaya yetkin yok!", HttpStatus.UNAUTHORIZED),
    GENERAL_EXCEPTION("1011", "Genel bir hata oluştu", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String errorMessage;
    private final HttpStatus httpStatus;

}
