package com.devrezaur.main.service;


import com.devrezaur.main.model.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    Users getUserByEmail(String email);
    Users createUser(Users user);

    Users saveUser(Users users);

    List<Users> findAll();

    List<Integer> findScoreByUsers(List<Users> usersList);

    List<String> findNameByUsers(List<Users> usersList);

    Users findById(Long id);

    void deleteUser(Users user);

}
