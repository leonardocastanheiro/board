package br.com.dio.service;

import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BoardColumnService {

    private final Connection connection;

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {

        var dao = new BoardColumnDAO(connection);

        var cardService = new CardService(connection);

        List<BoardColumnEntity> list = dao.findByBoardId(boardId);

        for(var entity : list) {
            entity.setCards(cardService.findByBoardColumnId(entity.getId()));
        }

        return list;
    }
}
