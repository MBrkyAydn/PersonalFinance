package com.berkay.personalfinance.service;
import com.berkay.personalfinance.entity.User;
import com.berkay.personalfinance.enums.Role;
import com.berkay.personalfinance.exception.DuplicateResourceException;
import com.berkay.personalfinance.mapper.UserMapper;
import com.berkay.personalfinance.repository.UserRepository;
import com.berkay.personalfinance.requestdto.LoginRequestDto;
import com.berkay.personalfinance.requestdto.RegisterRequestDto;
import com.berkay.personalfinance.responsedto.AuthResponseDto;
import com.berkay.personalfinance.responsedto.UserResponseDto;
import com.berkay.personalfinance.security.CustomUserDetails;
import com.berkay.personalfinance.security.JwtService;
import com.berkay.personalfinance.serviceimp.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthServiceImpl authService;
    private User mockUser;
    private CustomUserDetails mockUserDetails;
    private RegisterRequestDto registerRequestDto;
    private UserResponseDto userResponseDto;
    private LoginRequestDto loginRequestDto;
    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .email("ahmet@example.com")
                .password("encoded_pass")
                .role(Role.ROLE_USER)
                .build();
        mockUser.setId(1L);
        mockUserDetails = new CustomUserDetails(
                1L,
                "ahmet@example.com",
                "encoded_pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        registerRequestDto = RegisterRequestDto.builder()
                .email("ahmet@example.com")
                .password("123456")
                .build();
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .email("ahmet@example.com")
                .role(Role.ROLE_USER)
                .createdDate(LocalDateTime.now())
                .build();
        loginRequestDto = LoginRequestDto.builder()
                .email("ahmet@example.com")
                .password("123456")
                .build();
    }
    @Test
    @DisplayName("register: Email kayıtlı değilse kullanıcı başarıyla kaydedilmeli ve UserResponseDto dönmeli")
    void register_WhenEmailDoesNotExist_ShouldSaveAndReturnUserResponseDto() {
        // GIVEN
        when(userRepository.existsByEmail("ahmet@example.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded_pass");
        when(userMapper.toEntiy(registerRequestDto, "encoded_pass")).thenReturn(mockUser);
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(userMapper.toResponseDto(mockUser)).thenReturn(userResponseDto);
        // WHEN
        UserResponseDto result = authService.register(registerRequestDto);
        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("ahmet@example.com");
        verify(userRepository).existsByEmail("ahmet@example.com");
        verify(userRepository).save(mockUser);
    }
    @Test
    @DisplayName("login: Bilgiler doğruysa token üretilmeli ve AuthResponseDto dönmeli")
    void login_WhenCredentialsAreValid_ShouldAuthenticateAndReturnAuthResponseDto() {
        // GIVEN
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("mocked.jwt.token");
        // WHEN
        AuthResponseDto result = authService.login(loginRequestDto);
        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("mocked.jwt.token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(mockUserDetails);
    }
    @Test
    @DisplayName("login: Bilgiler hatalıysa BadCredentialsException fırlatılmalı ve token üretilmemeli")
    void login_WhenCredentialsAreInvalid_ShouldThrowBadCredentialsException() {
        // GIVEN
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Hatalı e-posta veya şifre"));
        // WHEN & THEN
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDto));
        // Şifre yanlışsa token üretme metoduna asla gidilmemeli
        verify(jwtService, never()).generateToken(any());
    }


}
