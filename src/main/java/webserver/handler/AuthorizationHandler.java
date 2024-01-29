package webserver.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.exception.NotLoggedInException;
import webserver.registry.AuthorizationRegistry;
import webserver.request.Request;
import webserver.session.Session;
import webserver.session.SessionManager;
import webserver.status.ErrorCode;

import java.util.Map;

public class AuthorizationHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationHandler.class);

    public static void handle(Request request){
        Map<String, String> authorizations = AuthorizationRegistry.getAuthorizations();

        if(authorizations.containsKey(request.getPath())){
            if(authorizations.get(request.getPath()).equals("user")){
                isLogin(request);
            }
        }
    }

    private static void isLogin(Request request){
        String sid = request.getHeader("Cookie");

        if(sid == null){
            throw new NotLoggedInException(ErrorCode.NOT_LOGGED_IN_ERROR);
        }

        String sidValue = sid.substring(sid.indexOf("sid=") + "sid=".length());

        Session session = SessionManager.loadSession(sidValue);

        if(session == null){
            throw new NotLoggedInException(ErrorCode.NOT_LOGGED_IN_ERROR);
        }
    }
}
