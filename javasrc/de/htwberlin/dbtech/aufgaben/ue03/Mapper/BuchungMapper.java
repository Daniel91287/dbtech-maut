package de.htwberlin.dbtech.aufgaben.ue03.Mapper;

import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.Fahrzeug;
import de.htwberlin.dbtech.exceptions.DataException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        public String getAchsenFromBuchung(String Kennzeichen){
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM BUCHUNGSTATUS b" +
                            "INNER JOIN BUCHUNG B2 ON b.B_ID = B2.B_ID" +
                            "INNER JOIN Mautkategorie M ON B2.KATEGORIE_ID = M.KATEGORIE_ID" +
                            "WHERE Kennzeichen = ? AND BEFAHRUNGSDATUM is NULL")) {
                statement.setString(1, Kennzeichen);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    Fahrzeug Fahrzeug = new Fahrzeug(rs.getLong(1), rs.getInt(2), rs.getInt(3),
                            rs.getString(4), rs.getString(5), rs.getInt(6),rs.getInt(7),
                            rs.getDate(8), rs.getDate(9), rs.getString(10));
                    return "Fahrzeug";
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);

            }
            return null;

    }
}
