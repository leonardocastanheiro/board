package br.com.dio.persistence.entity;

import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class BoardColumnEntity {
    private Long id;

    private String name;
    private BoardColumnKindEnum kind;
    private int order;

    private List<CardEntity> cards;
    private BoardEntity board;
}
