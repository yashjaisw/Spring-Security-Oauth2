package com.OATH2.rolebasedoath2.Service.Impl;

import com.OATH2.rolebasedoath2.Model.Role;
import com.OATH2.rolebasedoath2.Model.RoleType;
import com.OATH2.rolebasedoath2.Model.User;
import com.OATH2.rolebasedoath2.Service.UserService;
import com.OATH2.rolebasedoath2.dao.RoleDao;
import com.OATH2.rolebasedoath2.dao.UserDao;
import com.OATH2.rolebasedoath2.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userDao.findByUsername(userId);
        if(user == null){
            log.error("Invalid username or password.");
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        Set<GrantedAuthority> grantedAuthorities = getAuthorities(user);


        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    private Set<GrantedAuthority> getAuthorities(User user) {
        Set<Role> roleByUserId = user.getRoles();
        final Set<GrantedAuthority> authorities = roleByUserId.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toString().toUpperCase())).collect(Collectors.toSet());
        return authorities;
    }

    public List<UserDto> findAll() {
        List<UserDto> users = new ArrayList<>();
        userDao.findAll().iterator().forEachRemaining(user -> users.add(user.toUserDto()));
        return users;
    }

    @Override
    public User findOne(String userName) {
        return userDao.findByUsername(userName);
    }

    @Override
    public String delete(String userName) {
        User user = userDao.findByUsername(userName);
        if(user!=null) {
            userDao.deleteById(user.getId());
            return "Ok";
        }
        return "Failed";
    }

    @Override
    public UserDto save(UserDto userDto) {
        User userWithDuplicateUsername = userDao.findByUsername(userDto.getUsername());
        if(userWithDuplicateUsername != null && userDto.getId() != userWithDuplicateUsername.getId()) {
            log.error("Duplicate username: ", userDto.getUsername());
            throw new RuntimeException("Duplicate username.");
        }
        User userWithDuplicateEmail = userDao.findByEmail(userDto.getEmail());
        if(userWithDuplicateEmail != null && userDto.getId() != userWithDuplicateEmail.getId()) {
            log.error("Duplicate email: ", userDto.getEmail());
            throw new RuntimeException("Duplicate email.");
        }
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        List<RoleType> roleTypes = new ArrayList<>();
        userDto.getRole().stream().map(role -> roleTypes.add(RoleType.valueOf(role)));
        user.setRoles(roleDao.find(userDto.getRole()));
        userDao.save(user);
        return userDto;
    }
}