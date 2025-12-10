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

    /***
     * Die Methode realisiert einen Algorithmus, der die übermittelten
     * Fahrzeugdaten mit der Datenbank auf Richtigkeit überprüft und für einen
     * mautpflichtigen Streckenabschnitt entweder eine Buchung im Manuellen
     * Verfahren schliesst oder die zu zahlende Maut für ein Fahrzeug
     * im Automatischen Verfahren berechnet und eine entsprechende Mauterhebung speichert.
     *
     * Sind die Daten des Fahrzeugs im Automatischen Verfahren korrekt, wird
     * anhand der Mautkategorie (die sich aus der Achszahl und der
     * Schadstoffklasse des Fahrzeugs zusammensetzt) und der Mautabschnittslänge
     * die zu zahlende Maut berechnet und in der Mauterhebung gespeichert.
     *
     * Bei Fahrzeugen im Manuellen Verfahren wird darüberhinaus geprüft,
     * ob es noch offene Buchungen für den Mautabschnitt
     * gibt oder eine Doppelbefahrung aufgetreten ist. Besteht noch eine offene
     * Buchung für den Mautabschnitt, so wird diese Buchung für das Fahrzeug auf
     * abgeschlossen gesetzt.
     *
     * @param mautAbschnitt
     *            - identifiziert einen mautpflichtigen Abschnitt
     * @param achszahl
     *            - identifiziert die Anzahl der Achsen für das Fahrzeug das
     *            durch ein Kontrollsystem erfasst worden ist
     * @param kennzeichen
     *            - idenfiziert das amtliche Kennzeichen des Fahrzeugs das durch
     *            das Kontrollsystem erfasst worden ist
     * @throws UnkownVehicleException
     *             - falls das Fahrzeug weder registriert ist, noch eine offene
     *             Buchung vorliegt
     * @throws InvalidVehicleDataException
     *             - falls Daten des Kontrollsystems nicht mit den hinterlegten
     *             Daten in der Datenbank übereinstimmt
     * @throws AlreadyCruisedException
     *             - falls eine Doppelbefahrung für Fahrzeuge im Manuellen
     *             Verfahren vorliegt
     * @return die berechnete Maut für das Fahrzeug im Automatischen Verfahren
     *         auf dem Streckenabschnitt anhand der Fahrzeugdaten
     */
    @Override
    public void berechneMaut(int mautAbschnitt, int achszahl, String kennzeichen)
            throws UnkownVehicleException, InvalidVehicleDataException, AlreadyCruisedException {
        //Prüft, ob das Fahrzeug bekannt ist
        FahrzeugMapper fahrzeugMapper = new FahrzeugMapper(connection);
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
                mautabschnittMapper.FahrtVerbuchen(mautAbschnitt, achszahl, kennzeichen);
            }
        }
}
