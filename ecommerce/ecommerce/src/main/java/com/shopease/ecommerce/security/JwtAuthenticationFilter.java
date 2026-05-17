package com.shopease.ecommerce.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shopease.ecommerce.model.User;
import com.shopease.ecommerce.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 1️⃣ Get the "Authorization" header from the HTTP request
		String authHeader = request.getHeader("Authorization");

		// 2️⃣ Check if it starts with "Bearer "
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return; // No token → move on (it might be a public endpoint)
		}

		// 3️⃣ Extract the JWT token from the header
		String token = authHeader.substring(7); // remove "Bearer "

		// 4️⃣ Validate the token and extract the email
		String email = null;
		String role = null;
		if (jwtUtil.isTokenValid(token)) {
			email = jwtUtil.extractEmail(token);
			role = jwtUtil.extractRole(token);
		}

		// 5️⃣ If email is valid and not already authenticated
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			User user = userRepository.findByEmail(email).orElse(null);

			if (user != null) {
				List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
				// 6️⃣ Create authentication object (stores user identity + roles)
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
						authorities);

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// 7️⃣ Set authentication into the Security Context (Spring uses it later)
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		// 8️⃣ Continue the filter chain (let the request reach the controller)
		filterChain.doFilter(request, response);
	}

}
