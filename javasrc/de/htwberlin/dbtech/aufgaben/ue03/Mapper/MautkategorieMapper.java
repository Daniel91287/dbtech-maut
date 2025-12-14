package de.htwberlin.dbtech.aufgaben.ue03.Mapper;

import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.MAUTKATEGORIE;
import de.htwberlin.dbtech.exceptions.DataException;

import java.sql.*;

public class MautkategorieMapper {
    private Connection connection;

    public MautkategorieMapper(Connection connection) {
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

    public void insert(MAUTKATEGORIE mautkategorie) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "INSERT INTO MAUTKATEGORIE " +
                        "(KATEGORIE_ID, SSKL_ID, KAT_BEZEICHNUNG, ACHSZAHL, MAUTSATZ_JE_KM) " +
                        "VALUES (?, ?, ?, ?, ?)")) {

            statement.setInt(1, mautkategorie.getKATEGORIE_ID());
            statement.setInt(2, mautkategorie.getSSKL_ID());
            statement.setString(3, mautkategorie.getKAT_BEZEICHNUNG());
            statement.setInt(4, mautkategorie.getACHSZAHL());
            statement.setDouble(5, mautkategorie.getMAUTSATZ_JE_KM());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Insert von MAUTKATEGORIE", e);
        }
    }

    public void update(MAUTKATEGORIE mautkategorie) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "UPDATE MAUTKATEGORIE SET " +
                        "SSKL_ID = ?, " +
                        "KAT_BEZEICHNUNG = ?, " +
                        "ACHSZAHL = ?, " +
                        "MAUTSATZ_JE_KM = ? " +
                        "WHERE KATEGORIE_ID = ?")) {

            statement.setInt(1, mautkategorie.getSSKL_ID());
            statement.setString(2, mautkategorie.getKAT_BEZEICHNUNG());
            statement.setInt(3, mautkategorie.getACHSZAHL());
            statement.setDouble(4, mautkategorie.getMAUTSATZ_JE_KM());
            statement.setInt(5, mautkategorie.getKATEGORIE_ID());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Update von MAUTKATEGORIE", e);
        }
    }

    public void delete(int kategorieId) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "DELETE FROM MAUTKATEGORIE WHERE KATEGORIE_ID = ?")) {

            statement.setInt(1, kategorieId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen von MAUTKATEGORIE", e);
        }
    }

    public MAUTKATEGORIE findById(int kategorieId) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT * FROM MAUTKATEGORIE WHERE KATEGORIE_ID = ?")) {

            statement.setInt(1, kategorieId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Select von MAUTKATEGORIE per ID", e);
        }
    }

    private MAUTKATEGORIE mapRow(ResultSet rs) throws SQLException {
        return new MAUTKATEGORIE(
                rs.getInt("KATEGORIE_ID"),
                rs.getInt("SSKL_ID"),
                rs.getString("KAT_BEZEICHNUNG"),
                rs.getInt("ACHSZAHL"),
                rs.getDouble("MAUTSATZ_JE_KM")
        );
    }

    // Zusätzliche Hilfsmethoden für die Fahrtberechnung
    public int getKategorieIdBySchadstoffklasseUndAchsen(int schadstoffklasse, int achsen) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT KATEGORIE_ID FROM MAUTKATEGORIE " +
                        "WHERE SSKL_ID = ? AND ACHSZAHL LIKE ?")) {
            statement.setInt(1, schadstoffklasse);
            statement.setString(2,"%" + achsen);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("KATEGORIE_ID");
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public double getMautsatzByKategorieId(int kategorieId) {
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT MAUTSATZ_JE_KM FROM MAUTKATEGORIE WHERE KATEGORIE_ID = ?")) {
            statement.setInt(1, kategorieId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("MAUTSATZ_JE_KM");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}