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

	@Override
	public void registerVehicle(long fz_id, int sskl_id, int nutzer_id, String kennzeichen, String fin, int achsen,
                                int gewicht, String zulassungsland) {
        try (PreparedStatement statement = connection.prepareStatement("Insert into FAHRZEUG " +
                "(FZ_ID,SSKL_ID,NUTZER_ID,KENNZEICHEN,FIN,ACHSEN,GEWICHT,ANMELDEDATUM,ABMELDEDATUM,ZULASSUNGSLAND) " +
                "values (?,?,?,?,?,?,?," +
                "?,null,?)")) {
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

	@Override
	public void updateStatusForOnBoardUnit(long fzg_id, String status) {
        try(PreparedStatement statement = connection.prepareStatement("" +
                "UPDATE FAHRZEUGGERAT SET status = ? WHERE FZG_ID = ?")){
            statement.setString(1, status);
            statement.setLong(2, fzg_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

	}

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
