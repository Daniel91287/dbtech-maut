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

    public int getLaengeMautabschnitt (int Mautabschnitt) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT ABSCHNITTS_ID FROM Mautabschnitt m" +
                        "WHERE ABSCHNITTS_ID = ?")) {
            statement.setInt(1, Mautabschnitt);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int Laenge = rs.getInt("ABSCHNITTS_ID");
                return Laenge;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
