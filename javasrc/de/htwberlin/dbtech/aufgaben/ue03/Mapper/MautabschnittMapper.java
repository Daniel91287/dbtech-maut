package de.htwberlin.dbtech.aufgaben.ue03.Mapper;

import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.Fahrzeug;
import de.htwberlin.dbtech.exceptions.DataException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MautabschnittMapper {
    private Connection connection;

    public MautabschnittMapper(Connection connection) {
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

    public void FahrtVerbuchen (int mautAbschnitt, int achszahl, String kennzeichen) {
        FahrzeugMapper fahrzeugMapper = new FahrzeugMapper(connection);
        long FZ_ID = fahrzeugMapper.checkFahrzeuggerat(kennzeichen).getFZ_ID();
        int SSKL_ID = fahrzeugMapper.checkFahrzeuggerat(kennzeichen).getSSKL_ID();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM MAUTKATEGORIE m " +
                        "WHERE ACHSZAHL LIKE ? " +
                        "AND m.SSKL_ID = ?")) {
            statement.setString(1, "% " + achszahl);
            statement.setInt(2, SSKL_ID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                double MAUTSATZ_JE_KM = rs.getDouble("MAUTSATZ_JE_KM");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
