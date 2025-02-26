package com.elharche.security.filters;

import com.elharche.security.services.CustomUserDetailsService;
import com.elharche.security.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        String authToken = null;
        String email = null;

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        authToken = authHeader.substring(7);
//        email = jwtService.extractEmail(authToken);
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
//
//        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            if (jwtService.validateToken(authToken, userDetails)) {
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//            else {
//                // If the access token is expired, attempt to refresh with refresh token
//                String refreshToken = request.getHeader("Refresh-Token");
//                if (refreshToken != null && jwtService.validateRefreshToken(refreshToken)) {
//                    String newAccessToken = jwtService.refreshAccessToken(refreshToken);
//                    response.setHeader("Authorization", "Bearer " + newAccessToken);
//                }
//            }
//        }

        try {
            email = jwtService.extractEmail(authToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access token expired. Please refresh.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
