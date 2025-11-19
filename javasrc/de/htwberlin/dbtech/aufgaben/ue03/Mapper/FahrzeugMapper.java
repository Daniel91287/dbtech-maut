package de.htwberlin.dbtech.aufgaben.ue03.Mapper;

import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.Fahrzeug;
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
    public Fahrzeug getVehicle(String Kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM FAHRZEUG WHERE Kennzeichen = ?")) {
            statement.setString(1, Kennzeichen);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Fahrzeug Fahrzeug = new Fahrzeug(rs.getLong(1), rs.getInt(2), rs.getInt(3),
                        rs.getString(4), rs.getString(5), rs.getInt(6),rs.getInt(7),
                        rs.getDate(8), rs.getDate(9), rs.getString(10));
                return Fahrzeug;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return null;
    }

    public int getAchsen(String Kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT Achsen FROM FAHRZEUG WHERE Kennezeichen = ?")) {
            statement.setString(1, Kennzeichen);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int achsen = rs.getInt("Achsen");
                return achsen;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    }