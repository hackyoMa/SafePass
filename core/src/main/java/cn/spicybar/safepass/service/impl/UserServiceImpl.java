package cn.spicybar.safepass.service.impl;

import cn.spicybar.safepass.dao.IInfoRepository;
import cn.spicybar.safepass.dao.IUserRepository;
import cn.spicybar.safepass.domain.Info;
import cn.spicybar.safepass.domain.User;
import cn.spicybar.safepass.security.JwtTokenUtil;
import cn.spicybar.safepass.security.JwtUser;
import cn.spicybar.safepass.security.JwtUserDetailsServiceImpl;
import cn.spicybar.safepass.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户操作接口实现
 *
 * @author hackyo
 * Created on 2017/12/3 11:53.
 */
@Service
public class UserServiceImpl implements IUserService {

    private AuthenticationManager authenticationManager;
    private JwtUserDetailsServiceImpl jwtUserDetailsServiceImpl;
    private JwtTokenUtil jwtTokenUtil;
    private IUserRepository userRepository;
    private IInfoRepository infoRepository;

    @Autowired
    public UserServiceImpl(AuthenticationManager authenticationManager, JwtUserDetailsServiceImpl jwtUserDetailsServiceImpl, JwtTokenUtil jwtTokenUtil, IUserRepository userRepository, IInfoRepository infoRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUserDetailsServiceImpl = jwtUserDetailsServiceImpl;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.infoRepository = infoRepository;
    }

    @Override
    public String login(String username, String password, String currentIp) {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userRepository.updateLastLoginIpByUsername(username, currentIp);
        UserDetails userDetails = jwtUserDetailsServiceImpl.loadUserByUsername(username);
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public String register(User user) {
        System.out.println(user.getUsername() + "   +++   " + user.getPassword());
        String username = user.getUsername();
        if (userRepository.findByUsername(username) != null) {
            return "usernameExist";
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setLastPasswordResetTime(new Date());
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        userRepository.insert(user);
        return initUser(username);
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public String refreshToken(String oldToken, String currentIp) {
        String token = oldToken.substring("Bearer ".length());
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) jwtUserDetailsServiceImpl.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(token, currentIp, user.getLastLoginIp(), user.getLastPasswordResetTime())) {
            return jwtTokenUtil.refreshToken(token);
        }
        return "error";
    }

    private String initUser(String username) {
        String userId = userRepository.findByUsername(username).getId();
        Info info = new Info();
        Info info1 = new Info();
        Info info2 = new Info();
        info.setUserId(userId);
        info1.setUserId(userId);
        info2.setUserId(userId);
        info.setDescription("8888888");
        info1.setDescription("8888888@163.com");
        info2.setDescription("qq8888888");
        info.setContent("abc12345");
        info1.setContent("def456789");
        info2.setContent("ghi123789");
        String[] labels = new String[2];
        labels[0] = "QQ";
        labels[1] = "密码";
        String[] labels1 = new String[3];
        labels1[0] = "163";
        labels1[1] = "邮箱";
        labels1[2] = "密码";
        String[] labels2 = new String[3];
        labels2[0] = "博客园";
        labels2[1] = "博客";
        labels2[2] = "密码";
        info.setLabels(labels);
        info1.setLabels(labels1);
        info2.setLabels(labels2);
        infoRepository.insert(info);
        infoRepository.insert(info1);
        infoRepository.insert(info2);
        return "success";
    }

}
