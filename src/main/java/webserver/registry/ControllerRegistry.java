package webserver.registry;

import controller.HomeController;
import controller.TestController;
import controller.UserController;

import java.util.ArrayList;
import java.util.List;

public class ControllerRegistry {
    private final static List<Class<?>> controllers = new ArrayList<>();

    static {
        controllers.add(HomeController.class);
        controllers.add(UserController.class);
        controllers.add(TestController.class);
    }

    public static List<Class<?>> getControllers(){
        return controllers;
    }
}
