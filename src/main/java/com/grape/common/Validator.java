package com.grape.common;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Validator {

    /* 글자 수 검증 (최소/최대)
    *  허용 글자 범위 지정
    *  제한 글자 지정
    * */
    public boolean isTextValid(String text, int min, int max, boolean allowedSymbol, boolean hasText, boolean hasNumber, boolean hasSymbol){
        if(text == null) return false;

        int length = text.length();
        String pattern = null;
        if(hasSymbol){
            pattern = "^[0-9a-zA-Z가-힣!@#$%^&*()_]+$";
        }else{
            pattern = "^[0-9a-zA-Z가-힣]+$";
        }

        if(length < min || length > max) return false; // 글자 수 검증
        if(!text.matches(pattern)) return false; // 허용 글자 검증
        if(text.contains("바보")) return false; // 제한 글자 검증

        /* 최소 1회 이상 들어가야 하는 조건
        *  1.문자/ 2.숫자/ 3.특수문자
        *  매개변수 hasText, hasNumber, hasSymbol의 true 여부로 조건 실행
        *  */
        if(hasText){
            if(!text.matches(".*[a-zA-Z가-힣].*")) return false;
        }
        if(hasNumber){
            if(!text.matches(".*[0-9].*")) return false;
        }
        if(hasSymbol){
            if(!text.matches(".*[!@#$%^&*()_].*")) return false;
        }

        return true;
    }

}
