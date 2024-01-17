package handler;

import annotation.Controller;
import annotation.GetMapping;
import annotation.RequestParam;
import controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.request.GetRequest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class GetRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(GetRequestHandler.class);

    public static Object run(GetRequest getRequest) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        List<Class<?>> classes = getControllerClasses();
        Method method = findMethod(classes, getRequest.getPath());

        if(method == null){
            throw new NoSuchMethodException(String.format("GET %s 를 찾을 수 없습니다.", getRequest.getPath()));
        }

        Object[] params = createParams(method, getRequest.getParamsMap());

       return executeMethod(method, params);
    }

    private static Object executeMethod(Method method, Object[] params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> c = method.getDeclaringClass();
        Object instance = c.getDeclaredConstructor().newInstance();

        return method.invoke(instance, params);
    }

    private static Object[] createParams(Method method, Map<String, String> originParams){
        Class<? extends Annotation> requestParam = RequestParam.class;
        Parameter[] parameters = method.getParameters();

        Object[] params = new Object[parameters.length];
        int index = 0;

        for(Parameter parameter: parameters){
            if(parameter.isAnnotationPresent(requestParam)){
                RequestParam annotation = (RequestParam) parameter.getAnnotation(requestParam);

                params[index++]= originParams.get(annotation.name());
            }
        }

        return params;
    }

    private static Method findMethod(List<Class<?>> classes, String path){
        Class<? extends Annotation> getMapping = GetMapping.class;

        for(Class<?> c: classes){
            Method[] methods = c.getDeclaredMethods();

            for(Method method: methods){
                if(method.isAnnotationPresent(getMapping)){
                    GetMapping annotation = (GetMapping) method.getAnnotation(getMapping);

                    if(path.equals(annotation.path())){
                        return method;
                    }
                }
            }
        }

        return null;
    }

    private static List<Class<?>> getControllerClasses(){
        List<Class<?>> classes = new ArrayList<>();

        classes.add(UserController.class);

        return classes;
    }
}
