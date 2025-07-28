package br.com.dio.service;

import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BlockEntity;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public List<CardEntity> findByBoardColumnId(final Long boardColumnId) throws SQLException {
        var dao = new CardDAO(connection);

        List<CardEntity> list = dao.findByBoardColumnId(boardColumnId);


        return list;
    }

    public CardEntity insert(final CardEntity card) throws SQLException {
        var dao = new CardDAO(connection);

        try {
            dao.insert(card);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }

        return card;
    }

    public Optional<CardEntity> findById(final Long id) throws SQLException {
        var dao = new CardDAO(connection);

        Optional<CardEntity> card = dao.findById(id);

        if (card.isEmpty()) {
            return Optional.empty();
        }

        CardEntity cardEntity = card.get();

        var service = new BoardColumnService(connection);

        var optional = service.findById(cardEntity.getBoardColumn().getId());

        if (optional.isEmpty()) {
            cardEntity.setBoardColumn(null);
            return Optional.of(cardEntity);
        }

        cardEntity.setBoardColumn(optional.get());
        return Optional.of(cardEntity);
    }

    public void updateColumn(final CardEntity card) throws SQLException {
        var dao = new CardDAO(connection);

        try {
            dao.updateColumn(card);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
        }
    }

    public void blockCard(final Long cardId, String reason) throws SQLException {
        var dao = new CardDAO(connection);

        var service = new BlockService(connection);

        BlockEntity block = new BlockEntity();

        var card = dao.findById(cardId);

        if(card.isEmpty()) {
            return;
        }

        block.setCard(card.get());
        block.setBlockReason(reason);

        service.insert(block);
    }

    public void unblockCard(final Long cardId, String reason) throws SQLException {
        var dao = new CardDAO(connection);

        var service = new BlockService(connection);

        var blockOptional = service.findBlockedByCardId(cardId);

        if(blockOptional.isEmpty()) {
            return;
        }

        var block = blockOptional.get();

        block.setUnblockReason(reason);

        service.unblock(block);
    }

    public boolean isExists(final Long id) throws SQLException {
        var dao = new CardDAO(connection);

        return dao.isExists(id);
    }

    public void cancelCard(CardEntity card) throws SQLException {
        var dao = new CardDAO(connection);

        try {
            dao.cancelCard(card);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
        }

    }
}
