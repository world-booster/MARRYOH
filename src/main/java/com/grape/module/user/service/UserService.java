package com.grape.module.user.service;

import com.fasterxml.uuid.Generators;
import com.grape.common.DateFormat;
import com.grape.common.Validator;
import com.grape.module.auth.service.IAuthService;
import com.grape.module.user.model.UserDto;
import com.grape.module.user.model.UserFindingInfoDto;
import com.grape.module.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/*
* int 타입 반환 1: 성공/ 0: 실패
* */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final IUserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DateFormat dateFormat;
    private final Validator validator;
    private final IAuthService authService;

    @Override
    public int signup(UserDto user) throws Exception{
        try {
            /* 유저키 생성 */
            user.setUserKey(generateUuidV7());
            /* 유저키 생성이후 토큰 생성 및 저장 */
            if(authService.createRefreshToken(user.getUserKey()) == 0) return 0;
            /* 회원정보 미입력 검증 및 초기화 */
            initializeInfo(user);
            /* 입력정보 유효성 최종 검증 */
            if (checkUserInfo(user.getUserId(), "id") == 0) {
                return 0;
            }
            if (checkUserInfo(user.getUserPw(), "pw") == 0) {
                return 0;
            }
            if (checkUserInfo(user.getUserNickname(), "nickname") == 0) {
                return 0;
            }
            if (checkUserInfo(user.getUserEmail(), "email") == 0) {
                return 0;
            }
            /* 비밀번호 bcrypt 암호화 */
            String encodedPw = passwordEncoder.encode(user.getUserPw());
            user.setUserPw(encodedPw);
            return userRepository.insertUser(user);
        }catch (NullPointerException npe){
            logger.info("UserService/signup(UserDto user)/nullpointerException");
            return 0;
        }catch (DataAccessException dae) {
            logger.info("UserService/signup(UserDto user)/DataAccessException");
            throw dae;
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public int signin(UserDto user){
        try{
            if(checkUserInfo(user.getUserId(), "id") == 0){
                logger.info("로그인 실패: 사용자 없음 - id : "+user.getUserId());
                return 0;
            }

            String encodedPassword = userRepository.selectUserPw(user.getUserId());
            if(encodedPassword == null){
                logger.info("로그인 실패: 암호화된 비밀번호 없음 - id : "+user.getUserId());
                return 0;
            }

            boolean login = passwordEncoder.matches(user.getUserPw(), encodedPassword);
            if(login){
                logger.info("로그인 성공");
                return 1;
            } else{
                logger.info("로그인 실패: 비밀번호 불일치 - id : "+user.getUserId());
                return 0;
            }

        } catch(Exception e){
            throw e;
        }
    }

    /*
    *  유효성 검증 메서드
    *  중복 체크, 글자 수 제한, 특수문자 허용 체크, 최소 포함 조건
    *  */
    @Override
    public int checkUserInfo(String userInfo, String userInfoType){
        try{
            if(userInfo == null) return 0;
            if(userInfoType.equals("id")){
                if(!validator.isTextValid(userInfo,5,20,false, false, false, false)) return 1;
                return userRepository.selectUserId(userInfo);
            } else if (userInfoType.equals("nickname")) {
                if(!validator.isTextValid(userInfo, 2, 10, false, false, false, false)) return 1;
                return userRepository.selectUserNickname(userInfo);
            } else if (userInfoType.equals("email")) {
                return userRepository.selectUserEmail(userInfo);
            } else if (userInfoType.equals("pw")) {
                if(!validator.isTextValid(userInfo, 7, 10, true, true, true, true)) return 1;
                return 1;
            } else if (userInfoType.equals("userKey")) {
                return userRepository.selectUserKey(userInfo);
            } else {
                return 0;
            }
        }catch (NullPointerException npe){
            logger.info("UserService/checkUserInfo/nullpointerException");
            return 0;
        }catch (Exception e){
            throw e;
        }
    }


    /* uuid-v7(유저키) 등록 메서드 */
    public String generateUuidV7(){
        String uuidV7 = Generators.timeBasedEpochGenerator().generate().toString();
        while (checkUserInfo(uuidV7,"userKey") == 1) {
            uuidV7 = Generators.timeBasedEpochGenerator().generate().toString();
        }
        return uuidV7;
    }

    /* 회원정보 초기화 함수 */
    public UserDto initializeInfo(UserDto user){
        if (user.getSocialLoginYn() == null || !user.getSocialLoginYn().equals("Y")) {
            user.setSocialLoginYn("N");
        } else if (user.getSignupDt() == null) {
            user.setSignupDt(dateFormat.getNow());
        } else if (user.getLoginAuth() == null) {
            user.setLoginAuth("L");
        } else if (!user.getAdminYn().equals("Y")) {
            user.setAdminYn("N");
        }
        return user;
    }

    /* 회원정보 찾기 및 재설정 함수 */
    @Override
    public String findUserInfo(UserFindingInfoDto userFindingInfo, String userFindingInfoType) {
        if(userFindingInfoType.equals("id")){
            //이메일 인증 토큰 필요
        }
        return "";
    }
}
