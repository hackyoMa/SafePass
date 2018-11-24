package cn.spicybar.safepass.domain;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户模型
 *
 * @author hackyo
 * Created on 2017/12/3 11:53.
 */
@Document
public class User implements Serializable {

    @Id
    private String id;
    @Pattern(regexp = "[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]+$|1\\d{10}$", message = "邮箱或手机号格式不正确")
    @Length(min = 5, max = 32, message = "邮箱长度必须在5-32位之间")
    @Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
    private String username;
    @NotEmpty(message = "昵称不能为空")
    @Length(min = 1, max = 16, message = "昵称长度必须在1-16位之间")
    private String nickname;
    @Pattern(regexp = "^(?![^a-zA-Z]+$)(?!\\D+$).{8,32}$", message = "密码长度必须在8-32位之间，且必须包含数字和字母")
    private String password;
    private String lastLoginIp;
    private Date lastPasswordResetTime;
    private List<String> roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Date getLastPasswordResetTime() {
        return lastPasswordResetTime;
    }

    public void setLastPasswordResetTime(Date lastPasswordResetTime) {
        this.lastPasswordResetTime = lastPasswordResetTime;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
