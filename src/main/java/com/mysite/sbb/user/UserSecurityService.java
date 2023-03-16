package com.mysite.sbb.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//스프링 시큐리티에 등록하여 사용할 UserSecurityService는 스프링 시큐리티가 제공하는 UserDetailsService 인터페이스를 구현해야 함.
//UserDetailsService는 loadUserByUsername 메서드를 구현하도록 강제하는 인터페이스
//로그인 처리의 핵심
@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    // loadUserByUsername 메서드는 사용자명으로 비밀번호를 조회하여 리턴하는 메서드
    // loadUserByUsername 메서드에 의해 리턴된 User 객체의 비밀번호가 화면으로부터 입력 받은 비밀번호와 일치하는지를 검사하는 로직을 내부적으로 가지고 있다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SiteUser> _siteUser = this.userRepository.findByusername(username);
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다");
        }
        SiteUser siteUser = _siteUser.get();
        List<GrantedAuthority> authorities = new ArrayList<>(); // 권환 부여. List인 이유는 권한을 여러개 부여할수 있기때문?
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities); // 시큐리티의 User객체 리턴.


    }
}
