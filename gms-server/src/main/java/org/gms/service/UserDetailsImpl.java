package org.gms.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.gms.dao.entity.AccountsDO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Objects;

/**
 * Spring Security 用户详情实现，封装账号名、密码与权限集合。
 */
public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Integer id;
    private final String username;
    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * 构造 UserDetailsImpl。
     *
     * @param id 记录主键 ID
     * @param name 名称
     * @param password 密码
     * @param authorities authorities
     */
    public UserDetailsImpl(Integer id, String name, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = name;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * 执行 build 相关业务逻辑。
     *
     * @param user user
     * @param authorities authorities
     * @return UserDetailsImpl 类型结果
     */
    public static UserDetailsImpl build(AccountsDO user, Collection<? extends GrantedAuthority> authorities) {
        return new UserDetailsImpl(
                user.getId(),
                user.getName(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 执行 getId 相关业务逻辑。
     * @return Integer 类型结果
     */
    public Integer getId() {
        return id;
    }

    /**
     * 执行 getPassword 相关业务逻辑。
     * @return String 类型结果
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 执行 getUsername 相关业务逻辑。
     * @return String 类型结果
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 执行 isAccountNonExpired 相关业务逻辑。
     * @return boolean 类型结果
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 执行 isAccountNonLocked 相关业务逻辑。
     * @return boolean 类型结果
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 执行 isCredentialsNonExpired 相关业务逻辑。
     * @return boolean 类型结果
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 执行 isEnabled 相关业务逻辑。
     * @return boolean 类型结果
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 执行 equals 相关业务逻辑。
     *
     * @param o o
     * @return boolean 类型结果
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
