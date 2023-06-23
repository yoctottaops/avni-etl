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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
@Order(Ordered.LOWEST_PRECEDENCE)
public class ApiSecurity  {
    private final AuthService authService;
    private final OrganisationRepository organisationRepository;

    @Value("${avni.defaultUserName}")
    private String defaultUserName;

    @Value("${avni.idp.type}")
    private IdpType idpType;

    @Value("#{'${avni.security.allowedOrigins}'.split(',')}")
    private List<String> allowedOrigins;

    @Autowired
    public ApiSecurity(AuthService authService, OrganisationRepository organisationRepository) {
        this.authService = authService;
        this.organisationRepository = organisationRepository;
    }

    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(authService, organisationRepository, idpType, defaultUserName);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
            .csrf((csrf) -> csrf.disable())
            .formLogin((formLogin) -> formLogin.disable())
            .httpBasic((httpBasic) -> httpBasic.disable())
            .authorizeRequests().anyRequest().permitAll()
            .and()
            .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(authenticationFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }
}
