package de.htwberlin.dbtech.aufgaben.ue03.Mapper;

import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.FAHRZEUG;
import de.htwberlin.dbtech.exceptions.DataException;

import java.sql.*;

public class FahrzeugMapper {
    private Connection connection;

    public FahrzeugMapper(Connection connection) {
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

    public void insertFahrzeug(FAHRZEUG fahrzeug) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO FAHRZEUG\n" +
                "        (FZ_ID, SSKL_ID, NUTZER_ID, KENNZEICHEN, FIN, ACHSEN, GEWICHT,\n" +
                "         ANMELDEDATUM, ABMELDEDATUM, ZULASSUNGSLAND)\n" +
                "        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setLong(1, fahrzeug.getFZ_ID());
            statement.setInt(2, fahrzeug.getSSKL_ID());
            statement.setInt(3, fahrzeug.getNUTZER_ID());
            statement.setString(4, fahrzeug.getKENNZEICHEN());
            statement.setString(5, fahrzeug.getFIN());
            statement.setInt(6, fahrzeug.getACHSEN());
            statement.setInt(7, fahrzeug.getGEWICHT());
            statement.setDate(8, fahrzeug.getANMELDEDATUM());
            statement.setDate(9, fahrzeug.getABMELDEDATUM());
            statement.setString(10, fahrzeug.getZULASSUNGSLAND());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Insert von FAHRZEUG", e);
        }
    }

    public void updateFahrzeug(FAHRZEUG fahrzeug) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE FAHRZEUG SET " +
                        "SSKL_ID = ?, " +
                        "NUTZER_ID = ?, " +
                        "KENNZEICHEN = ?, " +
                        "FIN = ?, " +
                        "ACHSEN = ?, " +
                        "GEWICHT = ?, " +
                        "ANMELDEDATUM = ?, " +
                        "ABMELDEDATUM = ?, " +
                        "ZULASSUNGSLAND = ? " +
                        "WHERE FZ_ID = ?")) {

            statement.setInt(1, fahrzeug.getSSKL_ID());
            statement.setInt(2, fahrzeug.getNUTZER_ID());
            statement.setString(3, fahrzeug.getKENNZEICHEN());
            statement.setString(4, fahrzeug.getFIN());
            statement.setInt(5, fahrzeug.getACHSEN());
            statement.setInt(6, fahrzeug.getGEWICHT());
            statement.setDate(7, fahrzeug.getANMELDEDATUM());
            statement.setDate(8, fahrzeug.getABMELDEDATUM());
            statement.setString(9, fahrzeug.getZULASSUNGSLAND());
            statement.setLong(10, fahrzeug.getFZ_ID());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Update von FAHRZEUG", e);
        }
    }

    public void deleteFahrzeug(FAHRZEUG fahrzeug) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM FAHRZEUG WHERE FZ_ID = ?")) {

            statement.setLong(1, fahrzeug.getFZ_ID());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen von FAHRZEUG", e);
        }
    }

    public FAHRZEUG findById(long fzId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM FAHRZEUG WHERE FZ_ID = ?")) {

            statement.setLong(1, fzId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Select von FAHRZEUG per FZ_ID", e);
        }
    }

    public FAHRZEUG findByKennzeichen(String kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM FAHRZEUG WHERE KENNZEICHEN = ?")) {

            statement.setString(1, kennzeichen);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Select von FAHRZEUG per Kennzeichen", e);
        }
    }

    public boolean existsByKennzeichen(String kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM FAHRZEUG WHERE KENNZEICHEN = ?")) {

            statement.setString(1, kennzeichen);
            ResultSet rs = statement.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler bei Existenzprüfung FAHRZEUG", e);
        }
    }

    public int findAchsenByKennzeichen(String kennzeichen) {
        FAHRZEUG fahrzeug = findByKennzeichen(kennzeichen);
        if (fahrzeug != null) {
            return fahrzeug.getACHSEN();
        }
        throw new RuntimeException("Fahrzeug nicht gefunden für Achsenprüfung"); // oder null-check im Service
    }

    private FAHRZEUG mapRow(ResultSet rs) throws SQLException {
        return new FAHRZEUG(
                rs.getLong("FZ_ID"),
                rs.getInt("SSKL_ID"),
                rs.getInt("NUTZER_ID"),
                rs.getString("KENNZEICHEN"),
                rs.getString("FIN"),
                rs.getInt("ACHSEN"),
                rs.getInt("GEWICHT"),
                rs.getDate("ANMELDEDATUM"),
                rs.getDate("ABMELDEDATUM"),
                rs.getString("ZULASSUNGSLAND")
        );
    }
}