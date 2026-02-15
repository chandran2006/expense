package com.finmate.ai.service;

import com.finmate.ai.dto.UpdateProfileRequest;
import com.finmate.ai.dto.UserDTO;
import com.finmate.ai.entity.User;
import com.finmate.ai.exception.ResourceNotFoundException;
import com.finmate.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    public UserDTO getProfile() {
        User user = getCurrentUser();
        return mapToDTO(user);
    }
    
    public UserDTO updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPreferredLanguage() != null) {
            user.setPreferredLanguage(request.getPreferredLanguage());
        }
        if (request.getThemeMode() != null) {
            user.setThemeMode(request.getThemeMode());
        }
        
        userRepository.save(user);
        return mapToDTO(user);
    }
    
    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getPreferredLanguage(),
                user.getThemeMode()
        );
    }
}
