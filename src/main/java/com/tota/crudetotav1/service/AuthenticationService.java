package com.tota.crudetotav1.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tota.crudetotav1.dto.AuthenticationResponseDTO;
import com.tota.crudetotav1.dto.LoginRequestDTO;
import com.tota.crudetotav1.dto.RegisterRequestDTO;
import com.tota.crudetotav1.enums.TokenType;
import com.tota.crudetotav1.enums.UserRole;
import com.tota.crudetotav1.modal.Token;
import com.tota.crudetotav1.modal.User;
import com.tota.crudetotav1.repository.TokenRepository;
import com.tota.crudetotav1.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepositiory;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final TokenRepository tokenRepository;

	public void register(RegisterRequestDTO requestDTO) {

		var user = User.builder()
				.username(requestDTO.getUsername())
				.email(requestDTO.getEmail())
				.password(passwordEncoder.encode(requestDTO.getPassword()))
				.role(UserRole.USER)
				.build();
//		var savedUser = userRepositiory.save(user);
//		var jwtToken = jwtService.generateToken(user);
//			saveUserToken(savedUser, jwtToken);
		userRepositiory.save(user);

	}

	public AuthenticationResponseDTO login(LoginRequestDTO loginRequestDTO) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),
					loginRequestDTO.getPassword()));
		} catch (AuthenticationException e) {
			return AuthenticationResponseDTO.builder().msgErr("Invalid login crendentials").build();
		}
		var user = userRepositiory.findByUsername(loginRequestDTO.getUsername()).orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);
		return AuthenticationResponseDTO.builder().token(jwtToken).build();
	}

	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty()) {
			return;
		}
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);

	}

	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false)
				.build();
		tokenRepository.save(token);
	}

	public boolean isUsernamePresent(LoginRequestDTO loginRequestDTO) {
		return userRepositiory.findByUsername(loginRequestDTO.getUsername()).isPresent();
	}

	public boolean isUsernameEmailPresent(RegisterRequestDTO requestDTO) {
		return userRepositiory.findByUsername(requestDTO.getUsername()).isPresent()
				|| userRepositiory.findByEmail(requestDTO.getEmail()).isPresent();
	}

	public AuthenticationResponseDTO verifyTokenExp(String token) {
		AuthenticationResponseDTO response = new AuthenticationResponseDTO();		
		try {
			User user = userRepositiory.findByUsername(jwtService.extractUsername(token)).orElseThrow();
			if (jwtService.isTokenValid(token, user)) {
				return response;
			}
			
		} catch (Exception e) {
			return response = AuthenticationResponseDTO.builder().msgErr("Token is valid").build();
		}
		return response = AuthenticationResponseDTO.builder().msgErr("Token is valid").build();
	}
}
