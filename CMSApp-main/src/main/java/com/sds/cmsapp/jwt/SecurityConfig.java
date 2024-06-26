package com.sds.cmsapp.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
    public JwtUtil jwtUtil;
    
    public SecurityConfig(JwtUtil jwtUtil) {
    	this.jwtUtil = jwtUtil;
	}
    
    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    
    @Bean
    public LoginFilter loginFilter() throws Exception{
    	return new LoginFilter(jwtUtil, authenticationConfiguration.getAuthenticationManager());
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
            	
    	httpSecurity
            .authorizeHttpRequests(auth -> auth
            	
            	.requestMatchers("/").permitAll()  // static
            	.requestMatchers("/admin/**").permitAll()  // static
            	.requestMatchers("/loginForm").permitAll() // 로그인 폼 
            	.requestMatchers("/emp/login").permitAll() // 로그인 과정
                      	
            	.anyRequest().permitAll()
            	
            );
    	
    	httpSecurity.csrf(csrf -> csrf.disable());
    	httpSecurity.formLogin(form -> form.disable());
    	httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    	
    	httpSecurity.addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    	
        return httpSecurity.build();
    }
}
