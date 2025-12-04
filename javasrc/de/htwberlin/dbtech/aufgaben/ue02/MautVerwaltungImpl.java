package de.htwberlin.dbtech.aufgaben.ue02;

import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.dbtech.utils.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.htwberlin.dbtech.exceptions.DataException;

/**
 * Die Klasse realisiert die Mautverwaltung.
 * 
 * @author Patrick Dohmeier
 */
public class MautVerwaltungImpl implements IMautVerwaltung {

	private static final Logger L = LoggerFactory.getLogger(MautVerwaltungImpl.class);
	private Connection connection;

    /**
     * Speichert die uebergebene Datenbankverbindung in einer Instanzvariablen.
     *
     * @author Ingo Classen
     */
	@Override
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	private Connection getConnection() {
		if (connection == null) {
			throw new DataException("Connection not set");
		}
		return connection;
	}

    /**1
     * Liefert den Status eines Fahrzeugerätes zurück.
     *
     * @param fzg_id
     *            - die ID des Fahrzeuggerätes
     * @return status - den Status des Fahrzeuggerätes
     **/
	@Override
	public String getStatusForOnBoardUnit(long fzg_id) {
        String s = null;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT Status FROM FAHRZEUGGERAT f WHERE FZG_ID = ?")) {
            statement.setLong(1, fzg_id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                s = rs.getString("Status");

            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return s;
    }

    /**2
     * Liefert die Nutzernummer für eine Mauterhebung, die durch ein Fahrzeug im
     * Automatischen Verfahren ausgelöst worden ist.
     *
     * @param maut_id
     *            - die ID aus der Mauterhebung
     * @return nutzer_id - die Nutzernummer des Fahrzeughalters
     **/
	@Override
	public int getUsernumber(int maut_id) {
		int i = 0;
        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT Nutzer_ID FROM MAUTERHEBUNG m \n" +
                "JOIN FAHRZEUGGERAT f ON f.FZG_ID = m.FZG_ID \n" +
                "JOIN FAHRZEUG f2 ON f2.FZ_ID = f.FZ_ID \n" +
                "WHERE Maut_ID = ?")){
            statement.setInt(1, maut_id);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                i = rs.getInt("Nutzer_ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return i;
	}

    /**3
     * Registriert ein Fahrzeug in der Datenbank für einen bestimmten Nutzer.
     *
     * @param fz_id
     *            - die eindeutige ID des Fahrzeug
     * @param sskl_id
     *            - die ID der Schadstoffklasse mit dem das Fahrzeug angemeldet
     *            wird
     * @param nutzer_id
     *            - der Nutzer auf dem das Fahrzeug angemeldet wird
     * @param kennzeichen
     *            - das amtliche Kennzeichen des Fahrzeugs
     * @param fin
     *            - die eindeutige Fahrzeugindentifikationsnummer
     * @param achsen
     *            - die Anzahl der Achsen, die das Fahrzeug hat
     * @param gewicht
     *            - das zulässige Gesamtgewicht des Fahrzeugs
     * @param zulassungsland
     *            - die Landesbezeichnung für das Fahrzeug in dem es offiziell
     *            angemeldet ist
     *
     * **/
	@Override
	public void registerVehicle(long fz_id, int sskl_id, int nutzer_id, String kennzeichen, String fin, int achsen,
                                int gewicht, String zulassungsland) {
        try (PreparedStatement statement = connection.prepareStatement("Insert into FAHRZEUG " +
                "(FZ_ID,SSKL_ID,NUTZER_ID,KENNZEICHEN,FIN,ACHSEN,GEWICHT,ANMELDEDATUM,ABMELDEDATUM,ZULASSUNGSLAND) " +
                "values (?,?,?,?,?,?,?," +
                "?,null,?)")) {//null für Abmeldedatum
            statement.setLong(1, fz_id);
            statement.setInt(2, sskl_id);
            statement.setInt(3, nutzer_id);
            statement.setString(4, kennzeichen);
            statement.setString(5, fin);
            statement.setInt(6, achsen);
            statement.setInt(7, gewicht);
            statement.setDate(8, Date.valueOf(LocalDate.now()));
            //statement.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
            statement.setString(9, zulassungsland);
            statement.executeUpdate();

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

	}

    /**4
     * Aktualisiert den Status eines Fahrzeuggerätes in der Datenbank.
     *
     * @param fzg_id
     *            - die ID des Fahrzeuggerätes
     * @param status
     *            - der Status auf dem das Fahrzeuggerät aktualisiert werden
     *            soll
     */
	@Override
	public void updateStatusForOnBoardUnit(long fzg_id, String status) {
        try(PreparedStatement statement = connection.prepareStatement(
                "UPDATE FAHRZEUGGERAT SET status = ? WHERE FZG_ID = ?")){
            statement.setString(1, status);
            statement.setLong(2, fzg_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

	}
    /**5
     * Löscht ein Fahrzeug in der Datenbank.
     *
     * @param fz_id
     *            - die eindeutige ID des Fahrzeugs
     */
	@Override
	public void deleteVehicle(long fz_id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM FAHRZEUG WHERE fz_id = ?")){
            statement.setLong(1, fz_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

	}

    /**6
     * liefert eine Liste von Mautabschnitten eines bestimmten Abschnittstypen
     * zurück. z.B. alle Mautabschnitte der Autobahn A10
     *
     * @param abschnittsTyp
     *            - der AbschnittsTyp kann bspw. eine bestimmte Autobahn (A10)
     *            oder Bundesstrasse (B1) sein
     * @return List<Mautabschnitt> - eine Liste des Abschnittstypen, bspw. alle
     *         Abschnitte der Autobahn A10
     **/
	@Override
	public List<Mautabschnitt> getTrackInformations(String abschnittstyp) {
        List<Mautabschnitt> list = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM MAUTABSCHNITT m WHERE Abschnittstyp LIKE ?")) {
            statement.setString(1, abschnittstyp);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Mautabschnitt m = new Mautabschnitt(rs.getInt(1), rs.getInt(2),
                        rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6));
                list.add(m);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
