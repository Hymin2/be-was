package controller;

import annotation.GetMapping;
import annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;

public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService = new UserService();

    @GetMapping(path = "/user/create")
    public void createUser(@RequestParam(name = "userId") String userId,
                           @RequestParam(name = "password") String password,
                           @RequestParam(name = "name") String name,
                           @RequestParam(name = "email") String email){
        logger.debug("createUser() 실행");

        userService.createUser(userId, password, name, email);
    }
}
