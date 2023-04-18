package com.devrezaur.main.config;

import com.devrezaur.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.exceptionHandling().accessDeniedPage("/403");

        http.authorizeRequests().antMatchers("/","/css/**", "/js/**").permitAll();


        http.formLogin()
                .loginPage("/").permitAll() // login, Login.html
                .usernameParameter("user_email") // <input type="email" name="user_email">
                .passwordParameter("user_password")// <input type="password" name="user_password">
                .loginProcessingUrl("/auth").permitAll() // <form th:action ="@{'/auth'}"> method="post"
                .failureUrl("/?error")
                .defaultSuccessUrl("/quiz");

        http.logout()
                .logoutUrl("/logout").permitAll() //<form th:action ="@{'/logout'}"> method="post"
                .logoutSuccessUrl("/logout");

//        http.csrf().disable();


    }




}