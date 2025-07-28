package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BlockEntity;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.Optional;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public BlockEntity insert(BlockEntity blockEntity) throws SQLException {
        var sql = "INSERT INTO blocks (block_reason, card_id) VALUES (?, ?)";

        try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            var i = 1;

            statement.setString(i++, blockEntity.getBlockReason());
            statement.setLong(i++, blockEntity.getCard().getId());

            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                blockEntity.setId(resultSet.getLong(1));
            }

            return blockEntity;

        }
    }

    public boolean isBlocked(final Long cardId) throws SQLException {
        final String sql =
                "SELECT COUNT(*) AS cnt " +
                        "  FROM blocks " +
                        " WHERE card_id = ? " +
                        "   AND unblocked_at IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cardId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("cnt");
                    return count > 0;
                }
            }
        }
        return false;
    }

    public Optional<BlockEntity> findBlockedByCardId(final Long cardId) throws SQLException {
        var sql = "SELECT * FROM BLOCKS WHERE card_id = ? AND unblocked_at IS NULL";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setLong(i++, cardId);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            var block = new BlockEntity();

            block.setId(resultSet.getLong("id"));
            block.setBlockedAt(resultSet.getTimestamp("blocked_at").toInstant().atOffset(ZoneOffset.UTC));
            block.setBlockReason(resultSet.getString("block_reason"));
            block.setUnblockedAt(null);
            block.setBlockReason(null);

            var card = new CardEntity();

            card.setId(resultSet.getLong("card_id"));

            block.setCard(card);

            return Optional.of(block);
        }
    }

    public void unblock(final BlockEntity blockEntity) throws SQLException {
        var sql = "UPDATE blocks SET unblock_reason = ?, unblocked_at = ? WHERE id = ?";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            var i = 1;

            statement.setString(i++, blockEntity.getUnblockReason());
            statement.setTimestamp(i++,new Timestamp(System.currentTimeMillis()));
            statement.setLong(i++, blockEntity.getId());

            statement.executeUpdate();
        }
    }
}
