package db;

import model.Qna;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QnaDatabase {
    private static final Map<String, Qna> qnaDB = new ConcurrentHashMap<>();
    private static int index = 0;

    public static synchronized int getIndex(){
        return index++;
    }

    public static void addQna(Qna qna) {
        qnaDB.put(qna.getId(), qna);
    }

    public static Qna findQnaById(String qnaId) {
        return qnaDB.get(qnaId);
    }

    public static Collection<Qna> findAll() {
        return qnaDB.values();
    }

    public static boolean existsQnaByQnaId(String qnaId){
        return qnaDB.containsKey(qnaId);
    }
}
