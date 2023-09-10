package accountserviceapp.config;

import accountserviceapp.business.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private LoggingService loggingService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions().disable())
                .httpBasic()
                .authenticationEntryPoint(customAuthenticationEntryPoint())
                .and()
                .authorizeRequests(auth -> { auth
                    .requestMatchers("/h2-console/**", "/error/**", "/actuator/shutdown").permitAll()
                    .requestMatchers("/api/auth/signup").permitAll()
                    .requestMatchers("/api/empl/payment").hasAnyAuthority("ROLE_ACCOUNTANT", "ROLE_USER")
                    .requestMatchers("/api/acct/*").hasAuthority("ROLE_ACCOUNTANT")
                    .requestMatchers("/api/admin/user/**").hasAuthority("ROLE_ADMINISTRATOR")
                    .requestMatchers("/api/security/events/").hasAuthority("ROLE_AUDITOR")
                    .anyRequest().permitAll();
                })
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler(loggingService);
    }

    @Bean
    public RestAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint(loggingService);
    }
}