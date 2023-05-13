package com.tota.crudetotav1.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tota.crudetotav1.dto.AuthenticationResponseDTO;
import com.tota.crudetotav1.dto.LoginRequestDTO;
import com.tota.crudetotav1.dto.RegisterRequestDTO;
import com.tota.crudetotav1.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/totapi/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

	private final AuthenticationService authenticationService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
		if (StringUtils.isAnyBlank(registerRequestDTO.getUsername(), registerRequestDTO.getPassword())) {
			return ResponseEntity.badRequest().body("Fill in all the fields!");
		}
		if (authenticationService.isUsernameEmailPresent(registerRequestDTO)) {
			return new ResponseEntity<>("Username or Email already registred", HttpStatus.CONFLICT);
		}
		authenticationService.register(registerRequestDTO);
		return new ResponseEntity<>("User registred!", HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
		AuthenticationResponseDTO login = authenticationService.login(loginRequestDTO);

		if (login.getMsgErr() != null) {
			AuthenticationResponseDTO response = new AuthenticationResponseDTO();
			response.setMsgErr("User not registered!");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		} else
			return ResponseEntity.ok(login);

	}

	@GetMapping("/verifyTokenExpiration")
	public ResponseEntity<?> verifyTokenExp(@RequestHeader(name = "Authorization") String token) {

		AuthenticationResponseDTO response = authenticationService.verifyTokenExp(token);
		if (StringUtils.isBlank(response.getMsgErr())) {
			return new ResponseEntity<>("token is valid",HttpStatus.OK);
		}else {
			return new ResponseEntity<>("token is not valid!", HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
}
