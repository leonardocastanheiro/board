package br.com.dio.service;

import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public List<CardEntity> findByBoardColumnId(final Long boardColumnId) throws SQLException {
        var dao = new CardDAO(connection);

        List<CardEntity> list = dao.findByBoardColumnId(boardColumnId);


        return list;
    }
}
