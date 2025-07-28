package br.com.dio.persistence.dao;


import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import javax.smartcardio.Card;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CardDAO {

    private final Connection connection;

    public List<CardEntity> findByBoardColumnId(final Long columnId) throws SQLException {
        var sql = "SELECT * FROM cards WHERE board_column_id = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, columnId);

            ResultSet resultSet = preparedStatement.executeQuery();;

            List<CardEntity> list = new ArrayList<>();

            while (resultSet.next()) {
                CardEntity cardEntity = new CardEntity();

                cardEntity.setId(resultSet.getLong("id"));
                cardEntity.setTitle(resultSet.getString("title"));
                cardEntity.setDescription(resultSet.getString("description"));

                list.add(cardEntity);
            }

            return list;
        }
    }

    public CardEntity insert(final CardEntity cardEntity) throws SQLException {
        String sql = "INSERT INTO cards (title, description, board_column_id) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            stmt.setString(i++, cardEntity.getTitle());
            stmt.setString(i++, cardEntity.getDescription());
            stmt.setLong(i++, cardEntity.getBoardColumn().getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir o card, nenhum registro afetado.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cardEntity.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Falha ao obter ID gerado do card.");
                }
            }

            return cardEntity;
        }
    }

    public Optional<CardEntity> findById(final Long id) throws SQLException {
        var sql = "SELECT * FROM cards WHERE id = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            CardEntity cardEntity = new CardEntity();
            cardEntity.setId(resultSet.getLong("id"));
            cardEntity.setTitle(resultSet.getString("title"));
            cardEntity.setDescription(resultSet.getString("description"));

            BoardColumnEntity boardColumnEntity = new BoardColumnEntity();

            boardColumnEntity.setId(resultSet.getLong("board_column_id"));

            cardEntity.setBoardColumn(boardColumnEntity);

            return Optional.of(cardEntity);
        }
    }

    public void updateColumn(final CardEntity cardEntity) throws SQLException {
        var sql = "UPDATE cards SET board_column_id = ? WHERE id = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, cardEntity.getBoardColumn().getId());
            preparedStatement.setLong(2, cardEntity.getId());

            preparedStatement.executeUpdate();
        }
    }

    public boolean isExists(final Long id) throws SQLException {
        var sql = "SELECT 1 FROM cards WHERE id = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return true;
            }

        }

        return false;
    }

    public CardEntity cancelCard(final CardEntity cardEntity) throws SQLException {
        var sql = "UPDATE cards SET board_column_id = ? WHERE id = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, cardEntity.getBoardColumn().getBoard().getColumns().getLast().getId());
            preparedStatement.setLong(2, cardEntity.getId());

            preparedStatement.executeUpdate();
        }

        return cardEntity;
    }
}
