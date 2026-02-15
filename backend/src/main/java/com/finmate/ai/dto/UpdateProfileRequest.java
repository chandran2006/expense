package com.finmate.ai.dto;

import com.finmate.ai.entity.Language;
import com.finmate.ai.entity.ThemeMode;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private Language preferredLanguage;
    private ThemeMode themeMode;
}
