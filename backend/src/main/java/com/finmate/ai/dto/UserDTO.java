package com.finmate.ai.dto;

import com.finmate.ai.entity.Language;
import com.finmate.ai.entity.Role;
import com.finmate.ai.entity.ThemeMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Language preferredLanguage;
    private ThemeMode themeMode;
}
