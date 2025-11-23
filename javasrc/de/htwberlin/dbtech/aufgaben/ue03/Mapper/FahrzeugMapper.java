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
    public boolean getFahrzeugBekannt(String Kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM FAHRZEUG WHERE Kennzeichen = ?")) {
            statement.setString(1, Kennzeichen);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        BuchungMapper buchungMapper = new BuchungMapper(connection);
        return buchungMapper.checkFahreugInBuchung(Kennzeichen);
    }

    public int getAchsen(String Kennzeichen, int achszahl) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT ACHSEN FROM FAHRZEUG WHERE KENNZEICHEN = ?")) {
            statement.setString(1, Kennzeichen);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int achsen = rs.getInt(1);
                return achsen;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        BuchungMapper buchungMapper = new BuchungMapper(connection);
        return buchungMapper.getAchsenFromBuchung(Kennzeichen, achszahl);
    }
    public Fahrzeug checkFahrzeuggerat(String Kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT f2.* FROM FAHRZEUGGERAT f " +
                        "INNER JOIN FAHRZEUG f2 ON f.FZ_ID = f2.FZ_ID " +
                        "WHERE Kennzeichen = ?")) {
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

    }