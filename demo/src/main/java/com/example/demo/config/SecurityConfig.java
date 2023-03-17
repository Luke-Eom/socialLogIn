package com.example.demo.config;

import com.example.demo.config.auth.PrincipalDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalDetailService principalDetailService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder encodePWD() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(principalDetailService).passwordEncoder(encodePWD());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/","/auth/**","/js/**","/css/**","/image/**","/dummy/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/auth/loginForm")
                .loginProcessingUrl("/auth/loginProc")
                .defaultSuccessUrl("/");
    }

}

//@Autowired
//    private UserDetailsService jwtUserDetailsService;
//
//    @Autowired
//    private JwtRequestFilter jwtRequestFilter;
//
//    @Autowired
//    private UserOAuth2Service userOAuth2Service;
//
//    @Autowired
//    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//
//        return new BCryptPasswordEncoder();
//    }
//
//    @Override
//    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.cors().configurationSource(request -> {
//            var cors = new CorsConfiguration();
//            cors.setAllowedOrigins(List.of("http://localhost:3000"));
//            cors.setAllowedMethods(List.of("GET","POST", "PUT", "DELETE"));
//            cors.setAllowedHeaders(List.of("*"));
//            return cors;
//            });
//        httpSecurity.csrf().disable()
//
//                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS).permitAll()
//                .antMatchers("/account/**", "/member/authenticate", "/auth/**", "/api/**").permitAll()
//        .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .oauth2Login().defaultSuccessUrl("/login-success").successHandler(oAuth2AuthenticationSuccessHandler).userInfoEndpoint().userService(userOAuth2Service);
//
//        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//    }