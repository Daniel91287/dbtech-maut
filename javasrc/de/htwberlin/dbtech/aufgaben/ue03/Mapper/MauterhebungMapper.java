package de.htwberlin.dbtech.aufgaben.ue03.Mapper;

import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.MAUTERHEBUNG;
import de.htwberlin.dbtech.exceptions.DataException;

import java.sql.*;
import java.time.LocalDate;

public class MauterhebungMapper {
    private Connection connection;

    public MauterhebungMapper(Connection connection) {
        this.connection = connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Connection getConnection() {
        if (connection == null) {
            throw new DataException("Connection not set");
        }
        return connection;
    }

    public void insert(MAUTERHEBUNG mauterhebung) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "INSERT INTO MAUTERHEBUNG " +
                        "(MAUT_ID, ABSCHNITTS_ID, FZG_ID, KATEGORIE_ID, BEFAHRUNGSDATUM, KOSTEN) " +
                        "VALUES (?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, mauterhebung.getMAUT_ID());
            statement.setInt(2, mauterhebung.getABSCHNITTS_ID());
            statement.setLong(3, mauterhebung.getFZG_ID());
            statement.setInt(4, mauterhebung.getKATEGORIE_ID());
            statement.setDate(5, mauterhebung.getBEFAHRUNGSDATUM());
            statement.setDouble(6, mauterhebung.getKOSTEN());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Insert von MAUTERHEBUNG", e);
        }
    }

    public void update(MAUTERHEBUNG mauterhebung) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "UPDATE MAUTERHEBUNG SET " +
                        "ABSCHNITTS_ID = ?, " +
                        "FZG_ID = ?, " +
                        "KATEGORIE_ID = ?, " +
                        "BEFAHRUNGSDATUM = ?, " +
                        "KOSTEN = ? " +
                        "WHERE MAUT_ID = ?")) {

            statement.setInt(1, mauterhebung.getABSCHNITTS_ID());
            statement.setLong(2, mauterhebung.getFZG_ID());
            statement.setInt(3, mauterhebung.getKATEGORIE_ID());
            statement.setDate(4, mauterhebung.getBEFAHRUNGSDATUM());
            statement.setDouble(5, mauterhebung.getKOSTEN());
            statement.setInt(6, mauterhebung.getMAUT_ID());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Update von MAUTERHEBUNG", e);
        }
    }

    public void delete(int mautId) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "DELETE FROM MAUTERHEBUNG WHERE MAUT_ID = ?")) {

            statement.setInt(1, mautId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen von MAUTERHEBUNG", e);
        }
    }

    public MAUTERHEBUNG findById(int mautId) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT * FROM MAUTERHEBUNG WHERE MAUT_ID = ?")) {

            statement.setInt(1, mautId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Select von MAUTERHEBUNG per ID", e);
        }
    }

    private MAUTERHEBUNG mapRow(ResultSet rs) throws SQLException {
        return new MAUTERHEBUNG(
                rs.getInt("MAUT_ID"),
                rs.getInt("ABSCHNITTS_ID"),
                rs.getLong("FZG_ID"),
                rs.getInt("KATEGORIE_ID"),
                rs.getDate("BEFAHRUNGSDATUM"),
                rs.getDouble("KOSTEN")
        );
    }

    // Zusätzliche Hilfsmethoden
    public int getNextMautId() {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT COALESCE(MAX(MAUT_ID), 0) + 1 FROM MAUTERHEBUNG")) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 1; // Erste ID falls Tabelle leer
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsByAbschnittAndFzgIdAndDate(int abschnittsId, long fzgId, Date datum) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT 1 FROM MAUTERHEBUNG " +
                        "WHERE ABSCHNITTS_ID = ? AND FZG_ID = ? AND BEFAHRUNGSDATUM = ?")) {
            statement.setInt(1, abschnittsId);
            statement.setLong(2, fzgId);
            statement.setDate(3, datum);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}