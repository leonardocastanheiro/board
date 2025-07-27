package br.com.dio.persistence.dao;


import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
