package com.ecogem.backend.auth.service;


import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.auth.domain.Status;
import com.ecogem.backend.auth.repositorty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    // ✅ 회원가입
    public User signup(String loginId, String pwd, String email, Role role) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        User user = User.builder()
                .loginId(loginId)
                .pwd(pwd)
                .email(email)
                .role(role)                        // 회사/가게 구분용
                .status(Status.INCOMPLETE)         // 등록 미완 상태
                .build();

        return userRepository.save(user);
    }

    // ✅ 로그인
    public User login(String loginId, String pwd) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!user.getPwd().equals(pwd)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}
