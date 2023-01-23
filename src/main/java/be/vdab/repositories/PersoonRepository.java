package be.vdab.repositories;

import be.vdab.domain.Gezin;
import be.vdab.dto.PersoonMetOptionelePapaEnMama;
import be.vdab.dto.PersoonMetPapaEnMama;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<String> findGrootsteVermogen() throws SQLException {
        var personen = new ArrayList<String>();
        var sql = """
                SELECT voornaam FROM familie.personen
                WHERE vermogen = (SELECT max(vermogen) from familie.personen)
                ORDER BY voornaam
                """;
        try (var connection = super.getConnection();
            var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            var result = statement.executeQuery();
            while (result.next()) {
                personen.add(result.getString("voornaam"));
            }
            connection.commit();
            return personen;
        }
    }
    public List<PersoonMetPapaEnMama> findPersonenMetPapaEnMama() throws SQLException {
        var personenMetPapaEnMama = new ArrayList<PersoonMetPapaEnMama>();
        var sql = """
                select kinderen.voornaam as kindVoornaam, papas.voornaam as papaVoornaam, mamas.voornaam as mamaVoornaam
                from personen as kinderen
                inner join personen as papas on kinderen.papaid = papas.id
                inner join personen as mamas on kinderen.mamaid = mamas.id
                order by kinderen.voornaam
                """;
        try (var connection = super.getConnection();
            var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            var result = statement.executeQuery();
            while (result.next()) {
                personenMetPapaEnMama.add(new PersoonMetPapaEnMama(result.getString("kindVoornaam"),
                        result.getString("papaVoornaam"), result.getString("mamaVoornaam")));
            }
            connection.commit();
            return personenMetPapaEnMama;
        }
    }
    public String findById(Long id) throws SQLException {
        var sql = """
                select voornaam
                from personen
                where id = ?
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statement.setLong(1, id);
            var result = statement.executeQuery();
            connection.commit();
            return result.next() ? result.getString("voornaam") : null;
        }
    }
    public PersoonMetPapaEnMama findPersoonMetOudersById(Long id) throws SQLException {
        var sql = """
                select kinderen.voornaam as kindVoornaam, papas.voornaam as papaVoornaam, mamas.voornaam as mamaVoornaam
                from personen as kinderen
                inner join personen as papas on kinderen.papaid = papas.id
                inner join personen as mamas on kinderen.mamaid = mamas.id
                where kinderen.id = ?
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statement.setLong(1, id);
            var result = statement.executeQuery();
            connection.commit();
            if (!result.next()) {
                return null;
            } else {
                return new PersoonMetPapaEnMama(result.getString("kindVoornaam"),
                        result.getString("papaVoornaam"), result.getString("mamaVoornaam"));
            }
        }
    }
    public Optional<PersoonMetOptionelePapaEnMama> findPersoonMetOptioneleOudersById(Long id) throws SQLException {
        var sql = """
                select kinderen.voornaam as kindVoornaam, papas.voornaam as papaVoornaam, mamas.voornaam as mamaVoornaam
                from personen as kinderen
                left outer join personen as papas on kinderen.papaid = papas.id
                left outer join personen as mamas on kinderen.mamaid = mamas.id
                where kinderen.id = ?
                """;
        try (var connection = super.getConnection();
             var statement = connection.prepareStatement(sql)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statement.setLong(1, id);
            var result = statement.executeQuery();
            connection.commit();
            return result.next() ?
                    Optional.of(new PersoonMetOptionelePapaEnMama(result.getString("kindVoornaam"),
                    Optional.ofNullable(result.getString("papaVoornaam")),
                            Optional.ofNullable(result.getString("mamaVoornaam")))) :
                    Optional.empty();
        }
    }
}
