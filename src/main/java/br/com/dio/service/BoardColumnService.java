package br.com.dio.service;

import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<BoardColumnEntity> findById(final Long boardColumnId) throws SQLException {
        var dao = new BoardColumnDAO(connection);

        Optional<BoardColumnEntity> optional = dao.findById(boardColumnId);

        if(optional.isEmpty()) {
            return Optional.empty();
        }

        var boardService = new BoardService(connection);

        Optional<BoardEntity> optionalBoard = boardService.findById(optional.get().getBoard().getId());

        var entity = optional.get();

        if(optionalBoard.isEmpty()) {

            entity.setBoard(null);

            return Optional.of(entity);
        }

        entity.setBoard(optionalBoard.get());

        return Optional.of(entity);
    }
}
