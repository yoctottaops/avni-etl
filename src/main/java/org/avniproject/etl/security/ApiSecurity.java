package org.avniproject.etl.security;

import org.avniproject.etl.config.IdpType;
import org.avniproject.etl.repository.OrganisationRepository;
import org.avniproject.etl.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
@Order(Ordered.LOWEST_PRECEDENCE)
public class ApiSecurity  {
    private final AuthService authService;

    @Value("${avni.defaultUserName}")
    private String defaultUserName;

    @Value("${avni.idp.type}")
    private IdpType idpType;

    @Autowired
    public ApiSecurity(AuthService authService) {
        this.authService = authService;
    }

    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(authService, idpType, defaultUserName);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable())
            .formLogin((formLogin) -> formLogin.disable())
            .httpBasic((httpBasic) -> httpBasic.disable())
            .authorizeRequests().anyRequest().permitAll()
            .and()
            .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(authenticationFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }
}
