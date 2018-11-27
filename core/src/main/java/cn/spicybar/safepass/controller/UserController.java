package cn.spicybar.safepass.controller;

import cn.spicybar.safepass.domain.User;
import cn.spicybar.safepass.security.JwtUser;
import cn.spicybar.safepass.service.IUserService;
import cn.spicybar.safepass.utils.GetIp;
import cn.spicybar.safepass.utils.GetVerifyCode;
import cn.spicybar.safepass.utils.ParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * 用户管理Controller
 *
 * @author hackyo
 * Created on 2017/12/3 11:53.
 */
@CrossOrigin(origins = {"https://safepass.spicybar.cn"}, allowCredentials = "true")
@RestController
@RequestMapping(value = "/user", produces = "text/html;charset=UTF-8")
public class UserController {

    private HttpServletRequest request;
    private HttpSession session;
    private IUserService userService;
    private ObjectMapper mapper;

    @Autowired
    public UserController(HttpServletRequest request, HttpSession session, IUserService userService) {
        this.request = request;
        this.session = session;
        this.userService = userService;
        this.mapper = new ObjectMapper();
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 操作结果
     */
    @PostMapping(value = "/login", params = {"username", "password", "verifyCode"})
    public String login(String username, String password, String verifyCode) throws JsonProcessingException {
        String trulyVerifyCode = (String) session.getAttribute("verifyCode");
        session.removeAttribute("verifyCode");
        if (!verifyCode.toLowerCase().equals(trulyVerifyCode)) {
            return mapper.writeValueAsString("verifyCodeError");
        }
        return mapper.writeValueAsString(userService.login(username, password, GetIp.getUserIp(request)));
    }

    /**
     * 用户注册
     *
     * @param user          用户信息
     * @param bindingResult 验证用户信息
     * @return 操作结果
     */
    @PostMapping(value = "/register", params = {"username", "nickname", "password", "verifyCode"})
    public String register(@Validated User user, BindingResult bindingResult, String verifyCode) throws JsonProcessingException {
        String trulyVerifyCode = (String) session.getAttribute("verifyCode");
        session.removeAttribute("verifyCode");
        if (!verifyCode.toLowerCase().equals(trulyVerifyCode)) {
            return mapper.writeValueAsString("verifyCodeError");
        }
        String returnValue;
        if (bindingResult.hasErrors()) {
            throw new ParameterException(bindingResult);
        } else {
            user.setLastLoginIp(GetIp.getUserIp(request));
            returnValue = userService.register(user);
        }
        return mapper.writeValueAsString(returnValue);
    }

    /**
     * 用户名是否被注册
     *
     * @param username 用户名
     * @return 是否被注册
     */
    @GetMapping(value = "/usernameExists", params = {"username"})
    public String usernameExists(String username) throws JsonProcessingException {
        return mapper.writeValueAsString(userService.usernameExists(username));
    }

    /**
     * 获取验证码
     *
     * @return 验证码图片
     */
    @GetMapping(value = "/getVerifyCode")
    public String getVerifyCode() throws JsonProcessingException {
        String verifyCode = GetVerifyCode.getVerifyCodeNum();
        String verifyCodeImg = GetVerifyCode.getVerifyCodeImg(verifyCode);
        session.setAttribute("verifyCode", verifyCode.toLowerCase());
        return mapper.writeValueAsString(verifyCodeImg);
    }

    /**
     * 验证验证码
     *
     * @param verifyCode 验证码
     * @return 是否正确
     */
    @GetMapping(value = "/validateVerifyCode", params = {"verifyCode"})
    public String validateVerifyCode(String verifyCode) throws JsonProcessingException {
        String trulyVerifyCode = (String) session.getAttribute("verifyCode");
        if (verifyCode.toLowerCase().equals(trulyVerifyCode)) {
            return mapper.writeValueAsString(true);
        }
        return mapper.writeValueAsString(false);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/getInfo")
    public String getInfo() throws JsonProcessingException {
        JwtUser user = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return mapper.writeValueAsString(user);
    }

    /**
     * 刷新密钥
     *
     * @param authorization 原密钥
     * @return 新密钥
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/refreshToken")
    public String refreshToken(@RequestHeader("Authorization") String authorization) throws JsonProcessingException {
        return mapper.writeValueAsString(userService.refreshToken(authorization, GetIp.getUserIp(request)));
    }

}
