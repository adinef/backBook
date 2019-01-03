package net.fp.backBook.configuration;

import net.fp.backBook.security.fiter.AuthenticationTokenFilter;
import net.fp.backBook.security.service.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
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
                .antMatchers("/auth", "/users/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new AuthenticationTokenFilter(tokenAuthenticationService),
                        UsernamePasswordAuthenticationFilter.class);
    }

}
