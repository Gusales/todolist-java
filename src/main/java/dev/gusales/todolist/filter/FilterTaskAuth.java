package dev.gusales.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dev.gusales.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                var servletPath = request.getServletPath();

                if (servletPath.startsWith("/tasks")) {
                    // GET AUTHORIZATION
                    var authorization = request.getHeader("Authorization");
                    var authEncoded = authorization.substring("Basic".length()).trim();
                    
                    // DECODE AUTH
                    byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
                    var authString = new String(authDecoded);
                    
                    String[] credentials = authString.split(":");
                    String userName = credentials[0];
                    String password = credentials[1];

                    // FIND USER IN DATABASE
                    var userToAuth = this.userRepository.findByUsername(userName);
                    if (userToAuth == null) {
                        response.sendError(401);
                    }

                    else{

                        /* VALIDATE USER PASSWORD */
                        var isPasswordCorrect = BCrypt.verifyer().verify(password.toCharArray(), userToAuth.getPassword());
                        if (isPasswordCorrect.verified) {
                            request.setAttribute("idUser", userToAuth.getId());
                            filterChain.doFilter(request, response);
                        }
                        else{
                            response.sendError(401);
                        }
                }
                }
                else{
                    filterChain.doFilter(request, response);
                }
    }
    
}
