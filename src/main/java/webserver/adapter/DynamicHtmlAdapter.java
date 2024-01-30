package webserver.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.Model;
import webserver.annotation.GetMapping;
import webserver.exception.GeneralException;
import webserver.request.Request;
import webserver.response.Response;
import webserver.status.ErrorCode;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicHtmlAdapter extends MethodRequestAdapter{
    private static final Logger logger = LoggerFactory.getLogger(DynamicHtmlAdapter.class);
    private static final URL BASE_URL = ResourceAdapter.class.getClassLoader().getResource("./templates");
    private static final String pattenStr = "\\'\\$\\{([^}]+)\\}\\'"; // '${}'
    private static final Pattern pattern = Pattern.compile(pattenStr);
    private static final DynamicHtmlAdapter dynamicHtmlAdapter = new DynamicHtmlAdapter();

    private DynamicHtmlAdapter(){}

    public static DynamicHtmlAdapter getInstance(){
        return dynamicHtmlAdapter;
    }

    @Override
    public Response run(Request request) throws Throwable {
        Method method = findMethod(GetMapping.class, request.getPath());

        if(method == null){
            throw new GeneralException(ErrorCode.RESOURCE_NOT_FOUND_ERROR);
        }

        Object[] params = createParams(method, request);

        try {
            Object o = executeMethod(method, params);

            Model model = (Model) Arrays.stream(params)
                    .filter((param) -> param instanceof Model)
                    .findFirst()
                    .orElse(null);

            if(o instanceof String){
                if(model != null){
                    return Response.onSuccess(getDynamicHtml(model, (String) o));
                } else{
                    return Response.onSuccess(toByteArray(getFile((String) o)));
                }
            }

            return null;
        } catch (InvocationTargetException e){
            logger.error(e.getMessage());
            throw e.getTargetException();
        } catch (Exception e){
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean canRun(Request request) {
        return request.getMethod().equals("GET") && request.getPath().contains(".html");
    }

    private byte[] getDynamicHtml(Model model, String path) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new FileReader(BASE_URL.getPath() + path))) {
            while(reader.ready()){
                String line = reader.readLine();

                Matcher matcher = pattern.matcher(line);

                if(matcher.find()){
                    String key = matcher.group(1);
                    String openTag = getOpenTag(line);
                    String closeTag = getCloseTag(openTag);

                    StringBuilder body = new StringBuilder();

                    while(!line.contains(closeTag)){
                        logger.debug(line);
                        body.append(line).append("\n");

                        line = reader.readLine();
                    }
                    body.append(line).append("\n");

                    sb.append(makeHtml(body.toString(), model.getAttribute(key)));

                    continue;
                }
                sb.append(line).append("\n");
            }


            return sb.toString().getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String makeHtml(String str, Object value) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder result = new StringBuilder();

        logger.debug(str);

        if(str.contains("ty.each")){
            StringBuilder each = new StringBuilder();

            String[] strSplit = str.split("\n");
            result.append(strSplit[0]).append("\n");

            Collection<Object> collection = (Collection<Object>) value;

            if(collection.isEmpty()){
                return "";
            }

            int index = 1;
            for(Object object: collection){
                Class<?> clazz = object.getClass();

                for(int k = 1; k < strSplit.length - 1; k++){
                    Matcher matcher = pattern.matcher(strSplit[k]);

                    if(matcher.find()){
                        String key = matcher.group(1);

                        if(key.equals("index")){
                            each.append(makeHtml(strSplit[k], index++)).append("\n");

                            continue;
                        }
                        Field field = clazz.getDeclaredField(key);
                        field.setAccessible(true);

                        Object fieldValue = field.get(object);
                        each.append(makeHtml(strSplit[k], fieldValue)).append("\n");
                    } else{
                        each.append(strSplit[k]).append("\n");
                    }
                }
            }

            logger.debug(each.toString());
            result.append(each).append("\n");
            result.append(strSplit[strSplit.length - 1]).append("\n");

            return result.toString();
        }

        if(str.contains("ty.text")){
            return makeTextLine(str, String.valueOf(value));
        }

        return result.toString();
    }

    private String getOpenTag(String line){
        return line.trim().substring(0, line.trim().indexOf(">")).split(" ")[0];
    }

    private String getCloseTag(String openTag){
        return "</" + openTag.substring(1) + ">";
    }

    private String makeTextLine(String line, String newText){
        int pos = 0;
        boolean hasAttribute = false;

        StringBuilder sb = new StringBuilder();

        while (pos < line.length()) {
            char c = line.charAt(pos);
            if (c == '<') {
                // 열린 태그인 경우 닫힌 태그를 찾음
                int endTagStart = line.indexOf('>', pos);
                if (endTagStart != -1) {
                    String tagContent = line.substring(pos, endTagStart + 1);

                    if(tagContent.contains("ty.text")){
                        hasAttribute = true;
                    }

                    sb.append(tagContent);
                    pos = endTagStart + 1;
                } else {
                    break;
                }
            } else {
                // 태그가 아닌 경우 일반 텍스트를 찾음
                int nextTagStart = line.indexOf('<', pos);
                if (nextTagStart != -1) {
                    String textContent = line.substring(pos, nextTagStart);

                    if(hasAttribute){
                        sb.append(newText);
                    } else {
                        sb.append(textContent);
                    }
                    pos = nextTagStart;
                } else {
                    break;
                }
            }
        }

        return sb.append("\n").toString();
    }

    private File getFile(String path) {
        return new File(BASE_URL.getPath() + path);
    }

    private byte[] toByteArray(File file) throws IOException {
        if(!file.isDirectory() && file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            byte[] body = fis.readAllBytes();

            return body;
        }

        return null;
    }
}
