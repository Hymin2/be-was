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
    private static final String pattenStr = "'\\$\\{([^}]+)}'"; // '${}'
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
            Arrays.stream(e.getStackTrace()).map(String::valueOf)
                    .forEach(logger::error);

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
                    String openTag = getOpenTag(line);
                    String closeTag = getCloseTag(openTag);

                    StringBuilder body = new StringBuilder();

                    while(!line.contains(closeTag)){
                        body.append(line).append("\n");

                        line = reader.readLine();
                    }
                    body.append(line).append("\n");

                    sb.append(makeHtml(body.toString(), model));

                    continue;
                }
                sb.append(line).append("\n");
            }


            return sb.toString().getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String makeHtml(String htmlBody, Model model) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder result = new StringBuilder();

        if(htmlBody.contains("ty.each")){
            String[] strSplit = htmlBody.split("\n");
            result.append(strSplit[0]).append("\n");

            Matcher matcher = pattern.matcher(strSplit[0]);

            matcher.find();
            String key = matcher.group(1);

            Object value = model.getAttribute(key);
            StringBuilder childLine = new StringBuilder();

            Collection<Object> collection = (Collection<Object>) value;

            if(collection.isEmpty()){
                return "";
            }

            int index = 1;
            for(Object object: collection){
                Class<?> clazz = object.getClass();

                for(int k = 1; k < strSplit.length - 1; k++){
                    Matcher eachMatcher = pattern.matcher(strSplit[k]);
                    StringBuilder eachLine = new StringBuilder(strSplit[k]);
                    StringBuilder temp = new StringBuilder();

                    while(eachMatcher.find()){
                        String eachKey = eachMatcher.group(1);

                        if(eachKey.equals("index")){
                            temp.setLength(0);
                            temp.append(makeTextLine(eachLine.toString(), eachKey, index++)).append("\n");

                            eachLine.setLength(0);
                            eachLine.append(temp);

                            continue;
                        }
                        Field field = clazz.getDeclaredField(eachKey);
                        field.setAccessible(true);

                        Object fieldValue = field.get(object);

                        temp.setLength(0);
                        temp.append(makeTextLine(eachLine.toString(), eachKey, fieldValue)).append("\n");

                        eachLine.setLength(0);
                        eachLine.append(temp);
                    }

                    childLine.append(eachLine).append("\n");
                }
            }

            result.append(childLine).append("\n");
            result.append(strSplit[strSplit.length - 1]).append("\n");
        } else{
            Matcher matcher = pattern.matcher(htmlBody);
            StringBuilder line = new StringBuilder(htmlBody);
            StringBuilder temp = new StringBuilder();

            while(matcher.find()){
                String key = matcher.group(1);

                temp.setLength(0);
                temp.append(makeTextLine(line.toString(), key, model.getAttribute(key))).append("\n");

                line.setLength(0);
                line.append(temp);
            }

            result.append(line);
        }

        return result.toString();
    }

    private String makeTextLine(String line, String key, Object value){
        String replaceWord = String.format("'${%s}'", key);

        return line.replace(replaceWord, String.valueOf(value));
    }

    private String getOpenTag(String line){
        return line.trim().substring(0, line.trim().indexOf(">")).split(" ")[0];
    }

    private String getCloseTag(String openTag){
        return "</" + openTag.substring(1) + ">";
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
