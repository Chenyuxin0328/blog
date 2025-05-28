package com.chenyuxin.rear.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/png", "image/jpeg", "image/gif");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return false;
        return ALLOWED_TYPES.contains(file.getContentType());
    }
}
