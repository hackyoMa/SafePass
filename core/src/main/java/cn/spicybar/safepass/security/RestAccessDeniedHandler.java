package cn.spicybar.safepass.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定403返回值
 *
 * @author hackyo
 * Created on 2017/12/9 20:10.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) {
        String originHeader = request.getHeader("Origin");
        String[] allowOrigins = {"http://localhost:8070", "https://safepass.spicybar.cn"};
        Set<String> allowOriginsSet = new HashSet<>(Arrays.asList(allowOrigins));
        if (allowOriginsSet.contains(originHeader)) {
            response.setHeader("Access-Control-Allow-Origin", originHeader);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setStatus(403);
        }
    }

}
