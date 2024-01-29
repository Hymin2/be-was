package webserver.session;

import java.util.UUID;

public class SessionManager {
    private static final Integer DEFAULT_MAX_AGE = 60 * 30;

    public static Session createSession(String userId){
        String sid = UUID.randomUUID().toString();

        return Session.builder()
                .id(sid)
                .userId(userId)
                .maxAge(DEFAULT_MAX_AGE)
                .build();
    }

    public static Session createSession(String userId, int maxAge){
        String sid = UUID.randomUUID().toString();

        return Session.builder()
                .id(sid)
                .userId(userId)
                .maxAge(maxAge)
                .build();
    }

    public static Session loadSession(String sessionId){
        Session session = SessionDatabase.loadSession(sessionId);

        if(session == null) {
            return null;
        }

        if(session.isExpired()){
            delete(sessionId);

            return null;
        }

        return session;
    }

    public static void save(Session session){
        SessionDatabase.addSession(session);
    }

    public static void delete(String sessionId){
        SessionDatabase.deleteSession(sessionId);
    }
}
