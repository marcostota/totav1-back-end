package com.tota.crudetotav1.service;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.tota.crudetotav1.dto.UserDTO;
import com.tota.crudetotav1.modal.User;
import com.tota.crudetotav1.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	
	private final UserRepository userRepository;

	public UserDTO getUserByUsername(String username) {
	Optional<User> userOpt = userRepository.findByUsername(username);
		
	var  userDTO = UserDTO.builder()
			.name(userOpt.get().getName())
			.phone(userOpt.get().getPhone())
			.email(userOpt.get().getEmail())
			.build();
	
	return userDTO;
	
	}

	@Transactional
	public void updateUserData(UserDTO userDTO) {
	    User user = userRepository.findByUsername(userDTO.getUsername()).orElseThrow();
		updateUserFromDTO(userDTO, user);
		userRepository.save(user);	
	}
	
	public static void updateUserFromDTO(UserDTO userDTO, User user) {
		if(StringUtils.isNotBlank(userDTO.getName())) {
			user.setName(userDTO.getName());
		}
		if(StringUtils.isNotBlank(userDTO.getEmail())) {
			user.setEmail(userDTO.getEmail());
		}
		if(StringUtils.isNotBlank(userDTO.getPhone())) {
			user.setPhone(userDTO.getPhone());
		}
	}
	
	
	
}
