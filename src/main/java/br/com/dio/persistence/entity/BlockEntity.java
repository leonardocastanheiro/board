package br.com.dio.persistence.entity;

import lombok.*;

import java.time.OffsetDateTime;

@Data
public class BlockEntity {
    private Long id;

    private String blockReason;
    private String unblockReason;

    private CardEntity card;

    private OffsetDateTime blockedAt;
    private OffsetDateTime unblockedAt;
}
