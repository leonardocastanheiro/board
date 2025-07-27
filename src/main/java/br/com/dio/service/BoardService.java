package br.com.dio.service;

import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardService {

    private final Connection connection;

    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);

        try {

            if(!dao.exists(id)) {
                return false;
            }

            dao.delete(id);
            connection.commit();

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }

        return true;
    }

    public BoardEntity insert(final BoardEntity board) throws SQLException {
        var dao = new BoardDAO(connection);
        var columnDao = new BoardColumnDAO(connection);

        try {
            dao.insert(board);

            var columns = board.getColumns().stream().peek(column -> column.setBoard(board)).toList();

            for(var column : columns) {
                columnDao.insert(column);
            }

            connection.commit();

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }

        return board;
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnService = new BoardColumnService(connection);


        var optional = dao.findById(id);

        if(optional.isEmpty()) {
            return Optional.empty();
        }

        var entity = optional.get();

        entity.setColumns(boardColumnService.findByBoardId(entity.getId()));

        return Optional.of(entity);
    }

}
