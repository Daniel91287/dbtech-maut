package de.htwberlin.dbtech.aufgaben.ue03.Mapper;

import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.BUCHUNG;
import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.FAHRZEUG;
import de.htwberlin.dbtech.exceptions.DataException;

import java.sql.*;
import java.time.LocalDate;

public class BuchungMapper {
    private Connection connection;

    public BuchungMapper(Connection connection) {
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

    public void insert(BUCHUNG buchung) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO BUCHUNG " +
                        "(BUCHUNG_ID, B_ID, ABSCHNITTS_ID, KATEGORIE_ID, KENNZEICHEN, " +
                        "BUCHUNGSDATUM, BEFAHRUNGSDATUM, KOSTEN) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, buchung.getBUCHUNG_ID());
            statement.setInt(2, buchung.getB_ID());
            statement.setInt(3, buchung.getABSCHNITTS_ID());
            statement.setInt(4, buchung.getKATEGORIE_ID());
            statement.setString(5, buchung.getKENNZEICHEN());
            statement.setDate(6, buchung.getBUCHUNGSDATUM());
            statement.setDate(7, buchung.getBEFAHRUNGSDATUM());
            statement.setDouble(8, buchung.getKOSTEN());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Insert von BUCHUNG", e);
        }
    }

    public void update(BUCHUNG buchung) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE BUCHUNG SET " +
                        "B_ID = ?, " +
                        "ABSCHNITTS_ID = ?, " +
                        "KATEGORIE_ID = ?, " +
                        "KENNZEICHEN = ?, " +
                        "BUCHUNGSDATUM = ?, " +
                        "BEFAHRUNGSDATUM = ?, " +
                        "KOSTEN = ? " +
                        "WHERE BUCHUNG_ID = ?")) {

            statement.setInt(1, buchung.getB_ID());
            statement.setInt(2, buchung.getABSCHNITTS_ID());
            statement.setInt(3, buchung.getKATEGORIE_ID());
            statement.setString(4, buchung.getKENNZEICHEN());
            statement.setDate(5, buchung.getBUCHUNGSDATUM());
            statement.setDate(6, buchung.getBEFAHRUNGSDATUM());
            statement.setDouble(7, buchung.getKOSTEN());
            statement.setInt(8, buchung.getBUCHUNG_ID());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Update von BUCHUNG", e);
        }
    }

    public void delete(int buchungId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM BUCHUNG WHERE BUCHUNG_ID = ?")) {

            statement.setInt(1, buchungId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen von BUCHUNG", e);
        }
    }

    public BUCHUNG findById(int buchungId) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT * FROM BUCHUNG WHERE BUCHUNG_ID = ?")) {

            statement.setInt(1, buchungId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Select von BUCHUNG per ID", e);
        }
    }

    public BUCHUNG findLatestByKennzeichen(String kennzeichen) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT * FROM BUCHUNG " +
                        "WHERE KENNZEICHEN = ? " +
                        "ORDER BY BUCHUNG_ID DESC")) {

            statement.setString(1, kennzeichen);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Select von BUCHUNG per Kennzeichen", e);
        }
    }


    public int getAchsenFromBuchung(String kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT M.Achszahl " +
                        "FROM BUCHUNG B " +
                        "INNER JOIN BUCHUNGSTATUS BS ON B.B_ID = BS.B_ID " +
                        "INNER JOIN MAUTKATEGORIE M ON B.KATEGORIE_ID = M.KATEGORIE_ID " +
                        "WHERE B.KENNZEICHEN = ? " +
                        "  AND BS.STATUS = 'offen' " +
                        "ORDER BY B.BUCHUNG_ID DESC")) {

            statement.setString(1, kennzeichen);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String a = rs.getString(1).trim(); // "= 4"
                return  Character.getNumericValue(a.charAt(a.length() - 1));
            }
            return -1;

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Abrufen der Achsen aus Buchung", e);
        }
    }


    public boolean checkFahreugInBuchung(String Kennzeichen){
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT Kennzeichen FROM BUCHUNGSTATUS b " +
                        "INNER JOIN BUCHUNG B2 ON b.B_ID = B2.B_ID " +
                        "WHERE Kennzeichen = ?")) {
            statement.setString(1, Kennzeichen);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return false;
    }

    public boolean checkDoppelbefahrung(int mautAbschnitt, String Kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM BUCHUNG b " +
                        "INNER JOIN MAUTKATEGORIE m ON b.KATEGORIE_ID = m.KATEGORIE_ID " +
                        "WHERE Kennzeichen = ? AND B_ID = 1 " +
                        "ORDER BY b.BUCHUNG_ID DESC")) {
            statement.setString(1, Kennzeichen);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                if (rs.getInt("ABSCHNITTS_ID") ==  mautAbschnitt) {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private BUCHUNG mapRow(ResultSet rs) throws SQLException {
        return new BUCHUNG(
                rs.getInt("BUCHUNG_ID"),
                rs.getInt("B_ID"),
                rs.getInt("ABSCHNITTS_ID"),
                rs.getInt("KATEGORIE_ID"),
                rs.getString("KENNZEICHEN"),
                rs.getDate("BUCHUNGSDATUM"),
                rs.getDate("BEFAHRUNGSDATUM"),
                rs.getDouble("KOSTEN")
        );
    }
}

