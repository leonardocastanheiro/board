package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(BoardColumnEntity boardColumnEntity) throws SQLException {
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            var i = 1;

            preparedStatement.setString(i++, boardColumnEntity.getName());
            preparedStatement.setInt(i++, boardColumnEntity.getOrder());
            preparedStatement.setString(i++, boardColumnEntity.getKind().name());
            preparedStatement.setLong(i++, boardColumnEntity.getBoard().getId());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if(resultSet.next()) {
                boardColumnEntity.setId(resultSet.getLong(1));
            }

            return boardColumnEntity;
        }
    }

    public List<BoardColumnEntity> findByBoardId(final Long id) throws SQLException {

        var cardDAO = new CardDAO(connection);

        var sql = "SELECT * FROM BOARDS_COLUMNS WHERE board_id = ? ORDER  BY `order`";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();;

            List<BoardColumnEntity> boardColumnEntityList = new ArrayList<>();

            while (resultSet.next()) {
                var boardColumnEntity = new BoardColumnEntity();

                boardColumnEntity.setId(resultSet.getLong("id"));
                boardColumnEntity.setName(resultSet.getString("name"));
                boardColumnEntity.setOrder(resultSet.getInt("order"));
                boardColumnEntity.setKind(BoardColumnKindEnum.fromString(resultSet.getString("kind")));
                boardColumnEntity.setCards(cardDAO.findByBoardColumnId(boardColumnEntity.getId()));

                boardColumnEntityList.add(boardColumnEntity);
            }

            return boardColumnEntityList;
        }

    }


}
