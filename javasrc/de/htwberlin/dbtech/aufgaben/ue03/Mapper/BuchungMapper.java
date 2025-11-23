package de.htwberlin.dbtech.aufgaben.ue03.Mapper;

import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.Fahrzeug;
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
        public int getAchsenFromBuchung(String Kennzeichen, int achszahl){
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT Achszahl FROM BUCHUNGSTATUS b " +
                            "INNER JOIN BUCHUNG B2 ON b.B_ID = B2.B_ID " +
                            "INNER JOIN Mautkategorie M ON B2.KATEGORIE_ID = M.KATEGORIE_ID " +
                            "WHERE Kennzeichen = ? AND STATUS = 'offen'")) {
                statement.setString(1, Kennzeichen);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    String Achszahl  = rs.getString("Achszahl");
                    Achszahl = Achszahl.substring(Achszahl.length()-1);
                    int Achsen = Integer.parseInt(Achszahl);
                    if (Achsen >= 5) {return achszahl;}
                    return Achsen;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);

            }
            return 0;
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

    public int getBuchungsID(String Kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM BUCHUNG b " +
                        "INNER JOIN BUCHUNGSTATUS B2 ON b.B_ID = B2.B_ID " +
                        "WHERE Kennzeichen = ?")) {
            statement.setString(1, Kennzeichen);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("STATUS").equals("offen")) {
                    return rs.getInt("BUCHUNG_ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    public void setBuchungsStatusToAbgeschlossen(int Buchung) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE BUCHUNG " +
                        "SET B_ID = 3 " +
                        "SET Befahrungsdatum = ? " +
                        "WHERE BUCHUNG_ID = ?")) {
            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setInt(2, Buchung);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkDoppelbefahrung(int mautAbschnitt, String Kennzeichen) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM BUCHUNG b " +
                        "INNER JOIN MAUTKATEGORIE m ON b.KATEGORIE_ID = m.KATEGORIE_ID " +
                        "WHERE Kennzeichen = ?")) {
            statement.setString(1, Kennzeichen);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getInt("ABSCHNITTS_ID") ==  mautAbschnitt) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}

