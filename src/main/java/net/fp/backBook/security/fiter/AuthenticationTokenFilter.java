package net.fp.backBook.security.fiter;

import net.fp.backBook.dtos.ErrorDto;
import net.fp.backBook.exceptions.TokenExpiredException;
import net.fp.backBook.security.service.TokenAuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private TokenAuthenticationService authenticationService;

    public AuthenticationTokenFilter(TokenAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = authenticationService.authenticate(httpServletRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            SecurityContextHolder.getContext().setAuthentication(null);
        } catch (TokenExpiredException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setHeader("Content-Type", "application/json");
            httpServletResponse.getOutputStream().println( new ErrorDto(e.getMessage()).getJsonMessage() );
        }
    }
}
