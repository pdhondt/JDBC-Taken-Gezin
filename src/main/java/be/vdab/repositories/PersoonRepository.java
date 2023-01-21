package be.vdab.repositories;

import be.vdab.domain.Gezin;

import java.sql.*;

public class PersoonRepository extends AbstractRepository {
    public void create(Gezin gezin) throws SQLException {
        var sql = """
                insert into personen(voornaam, papaId, mamaId)
                values (?, ?, ?)
                """;
        try (var connection = super.getConnection();
            var statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statement.setString(1, gezin.getPapa());
            statement.setNull(2, Types.INTEGER);
            statement.setNull(3, Types.INTEGER);
            statement.addBatch();
            statement.setString(1, gezin.getMama());
            statement.addBatch();
            statement.executeBatch();
            var result = statement.getGeneratedKeys();
            result.next();
            statement.setLong(2, result.getLong(1));
            result.next();
            statement.setLong(3, result.getLong(1));
            for (var kind : gezin.getKinderen()) {
                statement.setString(1, kind);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
        }
    }
}
