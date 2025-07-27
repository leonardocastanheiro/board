package br.com.dio.persistence.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Data
public class BoardColumnEntity {
    private Long id;

    private String name;
    private BoardColumnKindEnum kind;
    private int order;

    private List<CardEntity> cards = new ArrayList<>();
    private BoardEntity board = new BoardEntity();

}
