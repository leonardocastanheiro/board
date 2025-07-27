package br.com.dio.persistence.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class BoardEntity {

    private Long id;

    private String name;

    private List<BoardColumnEntity> columns;
}
