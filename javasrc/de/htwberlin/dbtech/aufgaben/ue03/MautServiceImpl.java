package de.htwberlin.dbtech.aufgaben.ue03;

import java.sql.Connection;

import de.htwberlin.dbtech.aufgaben.ue03.Mapper.BuchungMapper;
import de.htwberlin.dbtech.aufgaben.ue03.Mapper.FahrzeugMapper;
import de.htwberlin.dbtech.aufgaben.ue03.Mapper.MautabschnittMapper;
import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.Fahrzeug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.htwberlin.dbtech.exceptions.DataException;
import de.htwberlin.dbtech.exceptions.AlreadyCruisedException;
import de.htwberlin.dbtech.exceptions.InvalidVehicleDataException;
import de.htwberlin.dbtech.exceptions.UnkownVehicleException;

/**
 * Die Klasse realisiert den AusleiheService.
 * 
 * @author Patrick Dohmeier
 * @author David Kiedacz
 */
public class MautServiceImpl implements IMautService {

    private static final Logger L = LoggerFactory.getLogger(MautServiceImpl.class);
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
    public void berechneMaut(int mautAbschnitt, int achszahl, String kennzeichen)
            throws UnkownVehicleException, InvalidVehicleDataException, AlreadyCruisedException {
        //Prüft ob das Fahzeug bekannt ist
        FahrzeugMapper fahrzeugMapper = new FahrzeugMapper(connection); //muss noch Abfragen, ob in den Buchungen das Fahrzeug bekannt ist
        BuchungMapper buchungMapper = new BuchungMapper(connection);
        if (!fahrzeugMapper.getFahrzeugBekannt(kennzeichen)) {
            throw new UnkownVehicleException();
        }
        //Prüft die Achsenzahl
        if (achszahl != fahrzeugMapper.getAchsen(kennzeichen, achszahl) || achszahl != buchungMapper.getAchsenFromBuchung(kennzeichen, achszahl)) {
            throw new InvalidVehicleDataException();
        }
        //Verfahren prüfen ob Zahlung über Fahrzeuggerät oder Buchungsverfahren erfolgt
        if (null == fahrzeugMapper.checkFahrzeuggerat(kennzeichen)) {
            //Manuelles Verfahren durchlaufen
            int BuchungsID = buchungMapper.getBuchungsID(kennzeichen);
            if (buchungMapper.checkDoppelbefahrung(mautAbschnitt, kennzeichen)) {
                throw new AlreadyCruisedException();
            } else {
                buchungMapper.setBuchungsStatusToAbgeschlossen(BuchungsID);
                System.out.println("Buchung wurde auf 'Abgeschlossen' gesetzt");
            }
            //wenn Fahrzeuggerät vorhanden
        } else {
                MautabschnittMapper mautabschnittMapper = new MautabschnittMapper(connection);
                int Laenge = mautabschnittMapper.getLaengeMautabschnitt(mautAbschnitt);

            }
        }
}
