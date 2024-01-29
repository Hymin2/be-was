package controller;

import dto.UserRequest;
import dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import webserver.Model;
import webserver.annotation.GetMapping;
import webserver.annotation.PostMapping;
import webserver.annotation.RequestBody;
import webserver.annotation.RequestParam;
import webserver.response.Response;
import webserver.session.Session;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService = new UserService();

    @GetMapping(path = "/user/create")
    public Response createUser(@RequestParam(name = "userId") String userId,
                               @RequestParam(name = "password") String password,
                               @RequestParam(name = "name") String name,
                               @RequestParam(name = "email") String email) {
        logger.debug("createUser() 실행");

        userService.createUser(userId, password, name, email);

        return Response.redirect("/index.html");
    }

    @PostMapping(path = "/user/create")
    public Response createUserByPost(@RequestBody UserRequest.Register register){
        userService.createUser(register);

        return Response.redirect("/index.html");
    }

    @PostMapping(path = "/user/login")
    public Response login(@RequestBody UserRequest.Login loginInfo){
        Session session = userService.login(loginInfo);

        logger.debug(session.toString());
        return Response.redirect("/index.html", session);
    }

    @GetMapping(path = "/user/list.html")
    public String userList(Model model){
        List<UserResponse.UserInfo> userInfos = userService.getAllUser();

        model.setAttributes("users", userInfos);

        return "/user/list.html";
    }

    @GetMapping(path = "/user/login.html")
    public String loginPage(){
        return "/user/login.html";
    }

    @GetMapping(path = "/user/login_failed.html")
    public String loginFailedPage(){
        return "/user/login_failed.html";
    }

    @GetMapping(path = "/user/form.html")
    public String registerPage(){
        return "/user/form.html";
    }

    @GetMapping(path = "/user/form_failed.html")
    public String registerFailedPage(){
        return "/user/form_failed.html";
    }
}
