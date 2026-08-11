package com.varun.ecommerce.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component  // Same as express authMiddleware, this runs before evry request to Controller
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization"); // get haeder from cookie, whcih contains token
        final String jwt;
        final String userEmail;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;  // if no hearder,it emnas public api, open for all
        }
        
        jwt = authHeader.substring(7); // extarct jwt token from header
        userEmail = jwtService.extractUsername(jwt);  // extract users email,which is in token itslef, we need email to load from db
        
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { // if user not alraedy authenticated before
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); //  load userdeatils from db
            
            if (jwtService.validateToken(jwt, userDetails)) { //   if jwt token is correct with user via jtsService class
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, // logged-in user object for Spring,userDetails has email,pass, roles
                        null, // password not needed now,already verified during login,so null
                        userDetails.getAuthorities()// this gets role from userDetails
                    );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));// not so imp line
                SecurityContextHolder.getContext().setAuthentication(authToken); // this tells spring user is now authenticated,
            }
        }
        filterChain.doFilter(request, response);
    }
}