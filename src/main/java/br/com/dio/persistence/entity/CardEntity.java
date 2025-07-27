package br.com.dio.persistence.entity;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class CardEntity {
    private Long id;

    private String title;
    private String description;
    private OffsetDateTime createdAt;

    private List<BlockEntity> blocks;
    private BoardColumnEntity boardColumn;
}
