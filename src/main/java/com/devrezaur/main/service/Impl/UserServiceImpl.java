package com.devrezaur.main.service.Impl;

import com.devrezaur.main.model.Result;
import com.devrezaur.main.model.Roles;
import com.devrezaur.main.model.Users;
import com.devrezaur.main.repository.RoleRepository;
import com.devrezaur.main.repository.UserRepository;
import com.devrezaur.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Users myUser =userRepository.findByEmail(s);

        if(myUser!=null){

            User secUser=new User(myUser.getEmail(), myUser.getPassword(), myUser.getRoles());
            return secUser;
        }


        throw new UsernameNotFoundException("User Not Found");
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Users createUser(Users user) {
        Users checkUser=userRepository.findByEmail(user.getEmail());
        if(checkUser==null){
            Roles role=roleRepository.findByRole("ROLE_USER");
            if(role!=null){
                ArrayList<Roles> roles=new ArrayList<>();
                roles.add(role);
                user.setRoles(roles);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                return userRepository.save(user);
            }
        }
        return null;
    }

    @Override
    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    public List<Users> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<Integer> findScoreByUsers(List<Users> usersList) {
        return usersList.stream()
                .map(users -> {
                    Result result = users.getResult();
                    return result != null ? result.getTotalCorrect() : 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findNameByUsers(List<Users> usersList) {
        return usersList.stream()
                .map(Users::getFullName)
                .collect(Collectors.toList());
    }

    @Override
    public Users findById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public void deleteUser(Users user) {
        userRepository.delete(user);
    }
}
