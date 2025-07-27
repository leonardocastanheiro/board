package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {

    private final Connection connection;

    public BoardEntity insert(final BoardEntity boardEntity) throws SQLException {
        var sql = "INSERT INTO BOARDS (name) values (?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, boardEntity.getName());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                boardEntity.setId(resultSet.getLong(1));
            }
        }

        return boardEntity;
    }

    public void delete(final Long id) throws SQLException {
        var sql = "DELETE FROM BOARDS WHERE ID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var sql = "SELECT * FROM BOARDS WHERE ID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {

                    var entity = new BoardEntity();

                    entity.setId(resultSet.getLong("id"));
                    entity.setName(resultSet.getString("name"));

                    return Optional.of(entity);
                }

                return Optional.empty();
            }
        }

    }

    public boolean exists(final Long id) throws SQLException {

        var sql = "SELECT 1 FROM BOARDS WHERE ID = ?";

        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var rs = statement.executeQuery();

            return rs.next();
        }
    }


}
