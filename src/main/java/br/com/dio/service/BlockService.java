package br.com.dio.service;

import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BlockEntity;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BlockService {

    private final Connection connection;

    public BlockEntity insert(BlockEntity blockEntity) throws SQLException {
        var dao = new BlockDAO(connection);

        try {
            dao.insert(blockEntity);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }

        return blockEntity;
    }

    public boolean isBlocked(final Long cardId) throws SQLException {
        var dao = new BlockDAO(connection);

        return dao.isBlocked(cardId);
    }

    public Optional<BlockEntity> findBlockedByCardId(final Long cardId) throws SQLException {
        var dao = new BlockDAO(connection);

        return dao.findBlockedByCardId(cardId);
    }

    public void unblock(final BlockEntity blockEntity) throws SQLException {
        var dao = new BlockDAO(connection);

        try {
            dao.unblock(blockEntity);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
        }
    }


}
