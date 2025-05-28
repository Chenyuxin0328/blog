package com.chenyuxin.rear.module.post.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreatePostDto {

    private String text;

    private String location;

    private Integer viewScope;

    private List<Long> imageIds;
}
