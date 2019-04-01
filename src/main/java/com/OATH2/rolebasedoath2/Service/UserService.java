package com.OATH2.rolebasedoath2.Service;

import com.OATH2.rolebasedoath2.Model.User;
import com.OATH2.rolebasedoath2.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto user);
    List<UserDto> findAll();
    User findOne(String userName);
    String delete(String id);
}