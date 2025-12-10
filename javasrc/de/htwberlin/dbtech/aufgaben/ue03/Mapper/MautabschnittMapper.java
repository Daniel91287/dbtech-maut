package de.htwberlin.dbtech.aufgaben.ue03.Mapper;

import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.Fahrzeug;
import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.SCHADSTOFFKLASSE;
import de.htwberlin.dbtech.exceptions.DataException;

import java.sql.*;
import java.time.LocalDate;

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

        int Schadstoffklasse = 0;
        double MausatzJeKM = 0.0;
        int MautLaenge = 0;
        long FZG_ID = 0;
        int KategorieID = 0;
        int NextFreeMautID = 0;

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT f.SSKL_ID, FZG_ID " +
                        "FROM FAHRZEUG f " +
                        "INNER JOIN FAHRZEUGGERAT f2 ON f.FZ_ID = f2.FZ_ID " +
                        "WHERE f.KENNZEICHEN = ?")) {
            statement.setString(1, kennzeichen);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Schadstoffklasse = rs.getInt(1);
                FZG_ID = rs.getLong(2);
            }

            if (achszahl > 5) {achszahl = 5;}

            try (PreparedStatement statement2 = connection.prepareStatement(
                    "SELECT Kategorie_ID, MAUTSATZ_JE_KM " +
                            "FROM MAUTKATEGORIE m " +
                            "WHERE SSKL_ID = ? AND m.ACHSZAHL LIKE ?")) {
                statement2.setInt(1, Schadstoffklasse);
                statement2.setString(2, "%" + achszahl);
                ResultSet rs2 = statement2.executeQuery();
                if (rs2.next()) {
                    KategorieID =  rs2.getInt(1);
                    MausatzJeKM = rs2.getDouble(2);

                }
            }
            try (PreparedStatement statement3 = connection.prepareStatement(
                    "SELECT LAENGE " +
                            "FROM Mautabschnitt M " +
                            "WHERE M.ABSCHNITTS_ID = ?")) {
                statement3.setInt(1, mautAbschnitt);
                ResultSet rs3 = statement3.executeQuery();
                if (rs3.next()) {
                    MautLaenge = rs3.getInt(1);
                }
            }
            double Kosten = (MautLaenge * MausatzJeKM) / (100*1000);

            try (PreparedStatement statement4 = connection.prepareStatement(
                    "SELECT max(Maut_ID) " +
                            "FROM MAUTERHEBUNG")) {
                ResultSet rs4 = statement4.executeQuery();
                if (rs4.next()) {
                    NextFreeMautID = rs4.getInt(1) + 1;
                }
            }

            try (PreparedStatement statement5 = connection.prepareStatement(
                    "INSERT INTO MAUTERHEBUNG " +
                            "VALUES (?,?,?,?,?,?) ")) {
                statement5.setInt(1, NextFreeMautID);
                statement5.setInt(2, mautAbschnitt);
                statement5.setLong(3, FZG_ID);
                statement5.setInt(4, KategorieID);
                statement5.setDate(5, Date.valueOf(LocalDate.now()));
                statement5.setDouble(6, Kosten);
                statement5.executeUpdate();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
