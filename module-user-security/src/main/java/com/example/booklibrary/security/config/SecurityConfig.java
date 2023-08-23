package com.example.booklibrary.security.config;

import com.example.booklibrary.security.entrypoint.RestAuthenticationEntryPoint;
import com.example.booklibrary.security.handler.CustomAccessDeniedHandler;
import com.example.booklibrary.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.example.booklibrary.security.constant.Role.*;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${security.permitAll:false}")
    private boolean permitAll;

    @Value("${cors.allowedOrigins}")
    private String[] allowedOrigins;

    @Value("${cors.allowedMethods}")
    private String[] allowedMethods;

    @Value("${cors.allowedHeaders}")
    private String[] allowedHeaders;

    @Value("${cors.exposedHeaders}")
    private String[] exposedHeaders;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (permitAll) {
            http.authorizeHttpRequests(SecurityConfig::customizeAnyRequestPermitAll);
        } else {
            http
                    .authorizeHttpRequests(authorizeHttpRequests ->
                            authorizeHttpRequests
                                    .requestMatchers(
                                            antMatcher("/"),
                                            antMatcher("/index.html"),
                                            antMatcher("/login.html"),
                                            antMatcher("/auth/login"),
                                            antMatcher("/h2-console/**"),
                                            antMatcher("/actuator/**"),
                                            antMatcher("/api-docs/**"),
                                            antMatcher("/swagger-ui/**")
                                    ).permitAll()
                                    .requestMatchers(
                                            antMatcher("/api/**/books/**"),
                                            antMatcher("/api/**/purchases/**"),
                                            antMatcher("/api/**/recommendations/**")
                                    ).hasAnyRole(SUPER_ADMIN.name(), ADMIN.name(), USER.name())
                                    .requestMatchers(
                                            antMatcher("/manage/api/**/books/**"),
                                            antMatcher("/manage/api/**/purchases/**"),
                                            antMatcher("/api/**/users/**")
                                    ).hasAnyRole(SUPER_ADMIN.name(), ADMIN.name())
                                    .requestMatchers(
                                            antMatcher("/api/**/reports/**")
                                    ).hasAnyRole(SUPER_ADMIN.name())
                                    .anyRequest().authenticated()
                    )
                    .csrf(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .cors(this::customizeCors)
                    .headers(SecurityConfig::customizeFrameOption)
                    .sessionManagement(SecurityConfig::customizeSessionPolicy)
                    .exceptionHandling(exceptionHandling ->
                            exceptionHandling
                                    .authenticationEntryPoint(restAuthenticationEntryPoint)
                                    .accessDeniedHandler(customAccessDeniedHandler)

                    )
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }


        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
        return new ProviderManager(authenticationProviders);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private void customizeCors(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
        httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
    }

    private static void customizeSessionPolicy(SessionManagementConfigurer<HttpSecurity> httpSecuritySessionManagementConfigurer) {
        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private static void customizeFrameOption(HeadersConfigurer<HttpSecurity> httpSecurityHeadersConfigurer) {
        httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
    }

    private static void customizeAnyRequestPermitAll(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizationManagerRequestMatcherRegistry) {
        authorizationManagerRequestMatcherRegistry.anyRequest().permitAll();
    }
}

