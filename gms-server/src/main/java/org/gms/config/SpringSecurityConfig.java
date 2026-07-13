package org.gms.config;

import org.gms.aop.AuthEntryPointJwt;
import org.gms.aop.AuthTokenFilter;
import org.gms.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security安全配置类
 * <p>
 * 配置应用程序的安全策略，包括：
 * <ul>
 *   <li>JWT认证过滤器</li>
 *   <li>用户认证提供者</li>
 *   <li>密码编码器（BCrypt）</li>
 *   <li>HTTP安全过滤链配置</li>
 *   <li>接口权限控制</li>
 * </ul>
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class SpringSecurityConfig {

    /**
     * 用户详情服务实现，用于加载用户认证信息
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 未授权处理器，处理认证失败的情况
     */
    private final AuthEntryPointJwt unauthorizedHandler;

    /**
     * 构造函数，注入所需的依赖
     *
     * @param userDetailsService  用户详情服务
     * @param unauthorizedHandler 未授权处理器
     */
    @Autowired
    public SpringSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    /**
     * 创建JWT认证过滤器Bean
     * <p>
     * 该过滤器用于在每个请求中验证JWT令牌。
     * </p>
     *
     * @return AuthTokenFilter实例
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * 创建DAO认证提供者Bean
     * <p>
     * 配置使用UserDetailsService加载用户信息，并使用BCrypt密码编码器进行密码验证。
     * </p>
     *
     * @return DaoAuthenticationProvider实例
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * 创建认证管理器Bean
     *
     * @param authConfig 认证配置
     * @return AuthenticationManager实例
     * @throws Exception 如果获取认证管理器失败
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 创建密码编码器Bean
     * <p>
     * 使用BCrypt强哈希函数进行密码加密。
     * </p>
     *
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置HTTP安全过滤链
     * <p>
     * 配置规则：
     * <ul>
     *   <li>启用CORS，禁用CSRF</li>
     *   <li>配置未认证处理器</li>
     *   <li>使用无状态会话（STATELESS），适合JWT认证</li>
     *   <li>公开访问路径：认证接口、Swagger文档、前端静态资源</li>
     *   <li>其他所有请求需要认证</li>
     *   <li>在用户名密码认证过滤器之前添加JWT过滤器</li>
     * </ul>
     * </p>
     *
     * @param http HttpSecurity配置对象
     * @return SecurityFilterChain实例
     * @throws Exception 如果配置失败
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((config) -> config.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement((config) -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((config) -> config
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/", "/static/**", "/index.html", "/assets/**").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
