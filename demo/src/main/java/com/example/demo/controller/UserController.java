package com.example.demo.controller;

import com.cos.blog.model.KakaoProfile;
import com.cos.blog.model.OAuthToken;
import com.cos.blog.model.User;
import com.example.demo.entity.KakaoProfile;
import com.example.demo.entity.OAuthToken;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

// 인증이 안된 사용자들이 출입할 수 있는 경로를 /auth/** 허용 (시큐리티가 필요없는 곳)
// 그냥 주소가 /이면 index.jsp 허용
// static이하에 있는 /js/**, /css/**, /image/**


@Controller
public class UserController {

    @Value("${cos.key}")
    private String cosKey;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/auth/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/auth/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/user/updateForm")
    public String updateForm() {
        return "user/updateForm";
    }

    @GetMapping("/auth/kakao/callback")
    public String kakaoCallback(String code) {

        // Header
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id", "65fcfe922d0fa539a1bf32c45062e422");
        params.add("redirect_ur", "http://localhost:8000/auth/kakao/callback");
        params.add("code", code);

        // header와 body를 entity로 매핑
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // POST 방식으로 Http 요청 -> response
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        //Gson, Json Simple, ObjectMapper();
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 카카오 프로필 반환받기
        // Header
        RestTemplate rt2 = new RestTemplate();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer "+oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // Header에 해당하는 entity
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        // POST 방식으로 Http 요청 -> response
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        System.out.println("response2: " + response2.getBody());

        //Gson, Json Simple, ObjectMapper();
        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        User kakaoUser = User.builder()
                .username(Integer.toString(kakaoProfile.getId()))
                .password(cosKey)
                .email(kakaoProfile.getKakao_account().getEmail())
                .oauth("kakao")
                .build();

        System.out.println("카카오 사용자 회원가입 정보"+kakaoUser);

        // 중복 확인
        User originUser = userService.getUser(kakaoUser.getUsername());

        if(originUser.getUsername() == null) {
            System.out.println("기존 회원이 아닙니다. 카카오톡 회원 가입");

            userService.memberJoin(kakaoUser);
        }

        System.out.println("자동 회원가입 및 로그인을 진행합니다.");

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(),cosKey));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/";

    }

}
