package com.OATH2.rolebasedoath2.Controllers;


import com.OATH2.rolebasedoath2.Service.AuthenticationFacadeService;
import com.OATH2.rolebasedoath2.Service.UserService;
import com.OATH2.rolebasedoath2.dto.ApiResponse;
import com.OATH2.rolebasedoath2.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public static final String SUCCESS = "success";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_OWNER = "ROLE_OWNER";

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationFacadeService authenticationFacadeService;

    @CrossOrigin
    @Secured({ROLE_ADMIN})
    @RequestMapping(value="/listAll",method = RequestMethod.GET)
    public ApiResponse listUser(){
        log.info("received request to list user: ", authenticationFacadeService.getAuthentication().getPrincipal());
        return new ApiResponse(HttpStatus.OK, SUCCESS, userService.findAll());
    }

    @CrossOrigin
    @Secured({ROLE_ADMIN})
    @RequestMapping(value="/saveUser", method= RequestMethod.POST)
    public ApiResponse create(@RequestBody UserDto user){
        log.info("received request to create user: ", authenticationFacadeService.getAuthentication().getPrincipal());
        return new ApiResponse(HttpStatus.OK, SUCCESS, userService.save(user));
    }

    @CrossOrigin
    @Secured({ROLE_ADMIN, ROLE_USER})
    @RequestMapping(value="/getUser/{userName}",method = RequestMethod.GET)
    public ApiResponse update(@PathVariable String userName){
        log.info("received request to update user: ", authenticationFacadeService.getAuthentication().getPrincipal());
        return new ApiResponse(HttpStatus.OK, SUCCESS, userService.findOne(userName));
    }

    @CrossOrigin
    @Secured({ROLE_OWNER})
    @RequestMapping(value = "/deleteUser/{userName}",method = RequestMethod.GET)
    public ApiResponse delete(@PathVariable(value = "userName") String userName){
        log.info("received request to delete user: ", authenticationFacadeService.getAuthentication().getPrincipal());
        String status = userService.delete(userName);
        return new ApiResponse(HttpStatus.OK, SUCCESS, status);
    }



}