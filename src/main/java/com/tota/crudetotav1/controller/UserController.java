package com.tota.crudetotav1.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tota.crudetotav1.dto.UserDTO;
import com.tota.crudetotav1.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/totapi/user")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/getUser")
	public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetails userDetails) {
		try {
			String username = userDetails.getUsername();
			UserDTO userDate = userService.getUserByUsername(username);
			if (ObjectUtils.isNotEmpty(userDate)) {
				return new ResponseEntity<>(userDate, HttpStatus.OK);
			}

		} catch (Exception e) {
			return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Somthing wrong", HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/saveUserData")
	public ResponseEntity<?> saveUserData(@RequestBody UserDTO userDTO){
		try {
			userService.updateUserData(userDTO);
			return new ResponseEntity<>("User save successfully", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("User not save", HttpStatus.BAD_REQUEST);
		}
	}
}
