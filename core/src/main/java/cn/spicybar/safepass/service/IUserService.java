package cn.spicybar.safepass.service;

import cn.spicybar.safepass.domain.User;

/**
 * 用户操作接口
 *
 * @author hackyo
 * Created on 2017/12/3 11:53.
 */
public interface IUserService {

    /**
     * 用户登录
     *
     * @param username  用户名
     * @param password  密码
     * @param currentIp 当前IP
     * @return 操作结果
     */
    String login(String username, String password, String currentIp);

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 操作结果
     */
    String register(User user);

    /**
     * 验证用户是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean usernameExists(String username);

    /**
     * 刷新密钥
     *
     * @param oldToken  原密钥
     * @param currentIp 当前IP
     * @return 新密钥
     */
    String refreshToken(String oldToken, String currentIp);

}
