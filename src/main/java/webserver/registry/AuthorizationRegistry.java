package webserver.registry;


import java.util.HashMap;
import java.util.Map;

public class AuthorizationRegistry {
    private final static Map<String, String> authorizations = new HashMap<>();

    static {
        authorizations.put("/user/list.html", "user");
    }

    public static Map<String, String> getAuthorizations(){
        return authorizations;
    }
}
