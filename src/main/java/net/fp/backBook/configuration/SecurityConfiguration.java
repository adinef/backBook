package net.fp.backBook.configuration;

import net.fp.backBook.security.fiter.AuthenticationTokenFilter;
import net.fp.backBook.security.service.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Profile("prod")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private TokenAuthenticationService tokenAuthenticationService;

    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    protected SecurityConfiguration(TokenAuthenticationService tokenAuthenticationService,
                                    AuthenticationEntryPoint authenticationEntryPoint) {
        super();
        this.tokenAuthenticationService = tokenAuthenticationService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth", "/register", "/users/**", "/docs/**").permitAll()
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources",
                        "/configuration/security", "/swagger-ui.html",
                        "/webjars/**", "/swagger-resources/configuration/ui", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new AuthenticationTokenFilter(tokenAuthenticationService),
                        UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
