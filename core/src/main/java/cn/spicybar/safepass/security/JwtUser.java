package cn.spicybar.safepass.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * 安全用户模型
 *
 * @author hackyo
 * Created on 2017/12/8 9:20.
 */
public class JwtUser implements UserDetails {

    @JsonIgnore
    private String id;
    private String username;
    private String nickname;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String lastLoginIp;
    @JsonIgnore
    private Date lastPasswordResetTime;
    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

    JwtUser(String id, String username, String nickname, String password, String lastLoginIp, Date lastPasswordResetTime, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.lastLoginIp = lastLoginIp;
        this.lastPasswordResetTime = lastPasswordResetTime;
        this.authorities = authorities;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public Date getLastPasswordResetTime() {
        return lastPasswordResetTime;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

}
