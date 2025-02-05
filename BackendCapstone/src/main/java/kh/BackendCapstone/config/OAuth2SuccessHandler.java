package kh.BackendCapstone.config;


import kh.BackendCapstone.dto.TokenDto;
import kh.BackendCapstone.entity.CustomOAuth2User;
import kh.BackendCapstone.entity.Member;
import kh.BackendCapstone.entity.RefreshToken;
import kh.BackendCapstone.jwt.TokenProvider;
import kh.BackendCapstone.repository.MemberRepository;
import kh.BackendCapstone.repository.RefreshTokenRepository;
import kh.BackendCapstone.service.AuthService;
import kh.BackendCapstone.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.warn("authentication 확인 : {}",authentication);
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        
        Member member = memberRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        
        log.info("Exists by member: {}", refreshTokenRepository.existsByMember(member));
        if(refreshTokenRepository.existsByMember(member)) {
            refreshTokenRepository.deleteByMember(member);
        }
        
        RefreshToken refreshToken = new RefreshToken();
        String encodedToken = tokenDto.getRefreshToken();
        refreshToken.setRefreshToken(encodedToken);
        refreshToken.setRefreshTokenExpiresIn(tokenDto.getRefreshTokenExpiresIn());
        refreshToken.setMember(member);
        
        refreshTokenRepository.save(refreshToken);

        // 액세스 토큰을 가져와서 리다이렉트 URL에 포함시킴
        String token = tokenDto.getAccessToken();

        // 만료 시간을 설정 (예: 3600초 = 1시간)
        long expirationTime = tokenDto.getAccessTokenExpiresIn() / 1000; // 밀리초를 초로 변환

        // 프론트엔드로 리다이렉트하면서 토큰과 만료 시간을 전달
        response.sendRedirect("http://localhost:8111/auth/oauth-response/" + token + "/" + expirationTime);
    }
}