package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.enums.Roles;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional
    public void memberJoin(User user) {
        String rawPassword = user.getPassword();
        String encPassword = encoder.encode(rawPassword);
        user.setPassword(encPassword);
        user.setRole(Roles.USER);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        User persistance = userRepository.findById(user.getId())
                .orElseThrow(()->{
                    return new IllegalArgumentException("회원 찾기 실패: 해당 회원을 찾을 수 없습니다.");
                });

        //validate 확인
        if(persistance.getOauth()==null || persistance.getOauth().equals("")) {
            String rawPassword = user.getPassword();
            String encPassword = encoder.encode(rawPassword);
            persistance.setPassword(encPassword);
            persistance.setEmail(user.getEmail());
        }
    }

    @Transactional(readOnly = true)
    public User getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseGet(()->{
                    return new User();
                });
        return user;
    }
}
