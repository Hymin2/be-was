package webserver.adapter;

import org.checkerframework.checker.nullness.Opt;
import webserver.Model;
import webserver.annotation.GetMapping;
import webserver.annotation.PostMapping;
import webserver.annotation.RequestBody;
import webserver.annotation.RequestParam;
import webserver.registry.ControllerRegistry;
import webserver.request.Request;
import webserver.session.Session;
import webserver.session.SessionManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

public abstract class MethodRequestAdapter implements Adapter{
    protected Object executeMethod(Method method, Object[] params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> c = method.getDeclaringClass();
        Object instance = c.getDeclaredConstructor().newInstance();

        return method.invoke(instance, params);
    }

    protected Method findMethod(Class<? extends Annotation> annotation, String path){
        List<Class<?>> classes = ControllerRegistry.getControllers();

        for(Class<?> c: classes){
            Method[] methods = c.getDeclaredMethods();

            for(Method method: methods){
                if(method.isAnnotationPresent(annotation) && isSamePath(annotation, path, method)){
                    return method;
                }
            }
        }

        return null;
    }

    protected Object[] createParams(Method method, Request request) throws InstantiationException, IllegalAccessException {
        Parameter[] parameters = method.getParameters();

        Object[] params = new Object[parameters.length];
        int index = 0;

        for(Parameter parameter: parameters){
            if(parameter.isAnnotationPresent(RequestParam.class)){
                RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                params[index++] = request.getParam(annotation.name());
            } else if(parameter.isAnnotationPresent(RequestBody.class)){
                params[index++] = createResponseBody(parameter.getType(), request);
            } else if(parameter.getType().equals(Model.class)){
                params[index++] = new Model();
            } else if(parameter.getType().equals(Session.class)){
                Optional<String> sessionId = request.getSessionId();
                params[index++] = sessionId.map(SessionManager::loadSession).orElse(null);
            }
        }

        return params;
    }

    private Object createResponseBody(Class<?> type, Request request) throws InstantiationException, IllegalAccessException {
        Object o = type.newInstance();

        Field[] fields = o.getClass().getDeclaredFields();

        for(Field field: fields){
            field.setAccessible(true);
            field.set(o, request.getBody(field.getName()));
        }

        return o;
    }

    private boolean isSamePath(Class<? extends Annotation> annotation, String path, Method method) {
        boolean samePath = false;

        if(annotation == GetMapping.class){
            GetMapping getMapping = (GetMapping) method.getAnnotation(annotation);
            samePath = path.equals(getMapping.path());
        } else if(annotation == PostMapping.class){
            PostMapping postMapping = (PostMapping) method.getAnnotation(annotation);
            samePath = path.equals(postMapping.path());
        }
        return samePath;
    }
}
