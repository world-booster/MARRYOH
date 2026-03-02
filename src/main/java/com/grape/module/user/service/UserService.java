package com.grape.module.user.service;

import com.grape.utils.DateFormatter;
import com.grape.utils.Validator;
import com.grape.module.auth.model.TokenDto;
import com.grape.module.auth.service.IAuthService;
import com.grape.module.user.model.*;
import com.grape.module.user.repository.IUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;

import java.util.Optional;
import java.util.UUID;


/*
* int 타입 반환 1: 성공/ 0: 실패
* */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final IUserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DateFormatter dateFormatter;
    private final Validator validator;
    private final IAuthService authService;

    @Override
    public int signup(UserDto user) throws Exception{
        try {
            user.setUserKey(generateUuidV7());
            initializeInfo(user);

            if(!checkUserInfo(user.getUserId(), "id") ||
                    !checkUserInfo(user.getUserPw(), "pw") ||
                    !checkUserInfo(user.getUserNickname(), "nickname") ||
                    !checkUserInfo(user.getUserEmail(), "email")){
                return 0;
            }
            /* 비밀번호 bcrypt 암호화 */
            String encodedPw = passwordEncoder.encode(user.getUserPw());
            user.setUserPw(encodedPw);

            int result = userRepository.insertUser(user);
            if(result == 0){
                return 0;
            }

            /* 유저 생성이후 토큰 생성 및 저장 */
            if(authService.createRefreshToken(user.getUserKey()) == null){
                return 0;
            }

            return result;
        }catch (DataAccessException dae) {
            logger.debug("회원가입 에러: "+dae.getMessage());
            throw dae;
        }catch (Exception e){
            logger.debug("회원가입 에러: "+e.getMessage());
            throw e;
        }
    }

    @Override
    public TokenDto signin(LoginDto loginDto){
        try{
            if(loginDto.getUserId() == null || loginDto.getPassword() == null ||
                    loginDto.getUserId().trim().isEmpty() || loginDto.getPassword().trim().isEmpty()){
                throw new RuntimeException("회원정보가 존재하지 않습니다");
            }
            UserDto user = userRepository.selectUser(loginDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

            String encodedPassword = userRepository.selectUserPw(user.getUserId());
            if(encodedPassword == null){
                logger.info("로그인 실패: 암호화된 비밀번호 없음 - id : "+user.getUserId());
                throw new IllegalArgumentException("저장된 비밀번호가 존재하지 않습니다.");
            }

            if(!passwordEncoder.matches(loginDto.getPassword(), encodedPassword)){
                throw new IllegalArgumentException("비밀번호 불일치");
            }

            logger.info("로그인 성공: "+ loginDto.getUserId());

            Optional<UserDto> userInfo = userRepository.selectUser(loginDto.getUserId());
            String userKey = userInfo.get().getUserKey();

            String refreshToken = authService.createRefreshToken(userKey);

            //access 토큰 발급
            String accessToken = authService.jwtTokenGenerator(user.getUserKey());

            return new TokenDto(accessToken, refreshToken);
        } catch(Exception e){
            logger.error("로그인 에러: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void signout(String refreshToken) {
        try{

            if(refreshToken == null || refreshToken.isBlank()) {
                return;
            }
            authService.deleteTokenByToken(refreshToken);

        } catch (ExpiredJwtException e){
            return;
        }
    }

    @Override
    public String findId(FindIdDto findIdDto) {
        if(!checkUserInfo(findIdDto.getEmail(), "email")){
            throw new RuntimeException("존재하지 않는 이메일 입니다.");
        }
        String userId = userRepository.selectUserId(findIdDto.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 유저아이디 정보가 존재하지 않습니다"));
        return userId;
    }

    @Override
    public int resetPassword(ResetPwDto resetPwDto) {
        try{
            if(!resetPwDto.isVerified()){
                return 0;
            }

            UserDto user =  userRepository.selectUser(resetPwDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

            String encodedPw = passwordEncoder.encode(resetPwDto.getNewPassword());
            user.setUserPw(encodedPw);
            return userRepository.updateUserPassword(user);
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public UserProfileDto getProfile(String accessToken){
        try{
            if(accessToken == null || accessToken.trim().isEmpty()){
                throw new IllegalArgumentException("Access 토큰이 존재하지 않습니다.");
            }

            Claims claims = authService.parseJwtToken(accessToken);
            String userKey = claims.getSubject();
            if(userKey == null || userKey.trim().isEmpty()){
                throw new RuntimeException("토큰에서 사용자 정보를 찾을 수 없습니다.");
            }

            UserDto user = userRepository.selectUserByKey(userKey)
                    .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

            return new UserProfileDto(
                    user.getUserId(),
                    user.getUserNickname(),
                    user.getUserEmail(),
                    user.getSignupDt().toString(),
                    user.getLoginAuth(),
                    user.getAdminYn()
            );

        }catch (IllegalArgumentException iae){
            logger.info("UserService/getProfile()/IllegalArgumentException" + iae.getMessage());
            throw iae;
        }catch (DataAccessException dae){
            logger.error("UserService/getProfile()/DataAccessException" + dae.getMessage());
            throw dae;
        }catch (RuntimeException re){
            logger.info("UserService/getProfile()/RuntimeException" + re.getMessage());
            throw re;
        }catch (Exception e){
            logger.error("UserService/getProfile()/Excetion" + e.getMessage());
            throw e;
        }
    }

    @Override
    public void updateProfile(String accessToken, UserProfileUpdateDto updateDto){
        try{
            if(accessToken == null || accessToken.trim().isEmpty()){
                throw new IllegalArgumentException("Access 토큰이 존재하지 않습니다.");
            }

            Claims claims = authService.parseJwtToken(accessToken);
            String userKey = claims.getSubject();
            if(userKey == null || userKey.trim().isEmpty()){
                throw new RuntimeException("토큰에서 사용자 정보를 찾을 수 없습니다.");
            }

            UserDto user = userRepository.selectUserByKey(userKey)
                    .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

            if(updateDto.getUserNickname() != null) {
                if(!validator.isTextValid(updateDto.getUserNickname(), 2, 10, false, false, false, false)){
                    throw new IllegalArgumentException("닉네임 형식이 올바르지 않습니다.");
                }
                if(userRepository.existUserNickname(updateDto.getUserNickname())){
                    throw new RuntimeException("이미 존재하는 닉네임 입니다.");
                }
                user.setUserNickname(updateDto.getUserNickname());
            }

            int updated = userRepository.updateUserProfile(user);
            if(updated == 0){
                throw new RuntimeException("프로필 수정에 실패했습니다.");
            }
        }catch (IllegalArgumentException e) {
            logger.info("UserService/updateProfile()/IllegalArgumentException: " + e.getMessage());
            throw e;
        } catch (DataAccessException dae) {
            logger.error("UserService/updateProfile()/DataAccessException: " + dae.getMessage());
            throw dae;
        } catch (RuntimeException re) {
            logger.info("UserService/updateProfile()/RuntimeException: " + re.getMessage());
            throw re;
        } catch (Exception e) {
            logger.error("UserService/updateProfile()/Exception: " + e.getMessage(), e);
            throw e;
        }
    }

    /*
    *  유효성 검증 메서드
    *  중복 체크, 글자 수 제한, 특수문자 허용 체크, 최소 포함 조건
    *  */
    @Override
    public boolean checkUserInfo(String userInfo, String userInfoType){
        try{
            if(userInfo == null) return false;
            switch (userInfoType) {
                case "id" -> {
                    return validator.isTextValid(userInfo, 5, 20, false, false, false, false) && !userRepository.existUserId(userInfo);
                }
                case "nickname" -> {
                    return validator.isTextValid(userInfo, 2, 10, false, false, false, false) && !userRepository.existUserNickname(userInfo);
                }
                case "email" -> {
                    return !userRepository.existUserEmail(userInfo);
                }
                case "pw" -> {
                    return validator.isTextValid(userInfo, 7, 10, true, true, true, true);
                }
                case "userKey" -> {
                    return !userRepository.existUserKey(userInfo);
                }
                default -> {
                    return false;
                }
            }
        }catch (NullPointerException npe){
            logger.info("UserService/checkUserInfo/nullpointerException");
            return false;
        }catch (Exception e){
            throw e;
        }
    }



    /*
    * JDK UUID사용 ( uuid v7(시간순) -> jdk uuid[랜덤] 변경)
    *  */
    private String generateUuidV7(){
        // 무한 루프 방지 + JDK UUID 사용
        for(int i = 0; i < 10; i++) {
            String uuidV7 = UUID.randomUUID().toString().replace("-", "");
            logger.info("생성된 userKey 시도 {}: {}", i+1, uuidV7);

            if(checkUserInfo(uuidV7, "userKey")) {
                logger.info("사용 가능한 userKey: {}", uuidV7);
                return uuidV7;
            }
        }
        logger.error("userKey 생성 실패 - 10회 시도 모두 중복");
        throw new RuntimeException("고유한 userKey 생성 실패");
    }

    /* 회원정보 초기화 함수 */
    private UserDto initializeInfo(UserDto user){
        if (user.getSocialLoginYn() == null || !user.getSocialLoginYn().equals("Y")) {
            user.setSocialLoginYn("N");
        }
        if (user.getSignupDt() == null) {
            user.setSignupDt(dateFormatter.getMariaDbDateTime());
        }
        if (user.getLoginAuth() == null) {
            user.setLoginAuth("L");
        }
        if (user.getAdminYn() == null || !user.getAdminYn().equals("Y")) {
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
