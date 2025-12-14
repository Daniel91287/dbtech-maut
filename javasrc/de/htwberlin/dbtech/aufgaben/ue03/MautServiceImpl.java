package de.htwberlin.dbtech.aufgaben.ue03;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;

import de.htwberlin.dbtech.aufgaben.ue03.Mapper.*;
import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.BUCHUNG;
import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.FAHRZEUG;
import de.htwberlin.dbtech.aufgaben.ue03.TableObjects.MAUTERHEBUNG;
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

        FahrzeugMapper fahrzeugMapper = new FahrzeugMapper(connection);
        BuchungMapper buchungMapper = new BuchungMapper(connection);
        MautabschnittMapper mautabschnittMapper = new MautabschnittMapper(connection);

        // Prüft, ob Fahrzeug im automatischen oder manuellen Verfahren fährt
        boolean fahrzeugExistiert = fahrzeugMapper.existsByKennzeichen(kennzeichen);
        boolean buchungExistiert = buchungMapper.checkFahreugInBuchung(kennzeichen);

        // Wenn gar kein Fahrzeug gefunden wird
        if (!fahrzeugExistiert && !buchungExistiert) {
            throw new UnkownVehicleException();
        }

        // Achsenprüfung nur in der Tabelle, in der das Fahrzeug existiert
        if (fahrzeugExistiert) {
            int fahrzeugAchsen = fahrzeugMapper.findAchsenByKennzeichen(kennzeichen);
            if (achszahl != fahrzeugAchsen) {
                throw new InvalidVehicleDataException();
            }
        } else {
            int buchungsAchsen = buchungMapper.getAchsenFromBuchung(kennzeichen);
            if (achszahl >= 5) {
                achszahl = 5;
            }
            if (achszahl != buchungsAchsen) {
                throw new InvalidVehicleDataException();
            }
        }

        if (buchungExistiert) {
            if (buchungMapper.checkDoppelbefahrung(mautAbschnitt, kennzeichen)) {
                throw new AlreadyCruisedException();
            }
            BUCHUNG buchung = buchungMapper.findLatestByKennzeichen(kennzeichen);
            if (buchung != null) {
                buchung.setB_ID(3);
                buchung.setBEFAHRUNGSDATUM(Date.valueOf(LocalDate.now()));
                buchungMapper.update(buchung);
            }
        }

        if (fahrzeugExistiert) {

            MautkategorieMapper mautkategorieMapper = new MautkategorieMapper(connection);
            MauterhebungMapper mauterhebungMapper = new MauterhebungMapper(connection);

            // Schadstoffklasse aus Fahrzeug holen
            int schadstoffklasse = getSchadstoffklasseFromFahrzeug(fahrzeugMapper, kennzeichen);

            // FZG_ID aus FAHRZEUGGERAT holen
            Long fzgId = getFzgIdFromFahrzeugGerat(kennzeichen);
            if (fzgId == null) {
                throw new RuntimeException("FZG_ID nicht gefunden für Fahrzeug: " + kennzeichen);
            }

            // Achsen begrenzen
            if (achszahl > 5) { //unschöne implementierung, noch Ändern
                achszahl = 5;
            }

            // Kategorie-ID basierend auf Schadstoffklasse und Achsen finden
            int kategorieId = mautkategorieMapper.getKategorieIdBySchadstoffklasseUndAchsen(schadstoffklasse, achszahl);
            if (kategorieId == -1) {
                throw new RuntimeException("Keine passende Mautkategorie gefunden für Schadstoffklasse: " + schadstoffklasse + " und Achsen: " + achszahl);
            }

            // Mautsatz holen
            double mautsatzJeKm = mautkategorieMapper.getMautsatzByKategorieId(kategorieId);

            // Länge des Mautabschnitts holen
            int mautLaenge = mautabschnittMapper.getLaengeByAbschnittsId(mautAbschnitt);
            if (mautLaenge == -1) {
                throw new RuntimeException("Mautabschnitt nicht gefunden: " + mautAbschnitt);
            }

            // Kosten berechnen
            double kosten = (mautLaenge * mautsatzJeKm) / (100 * 1000); //BigDecimal besser?

            // Nächste MAUT_ID holen
            int nextMautId = mauterhebungMapper.getNextMautId();

            // MAUTERHEBUNG-Objekt erstellen und speichern
            MAUTERHEBUNG mauterhebung = new MAUTERHEBUNG(
                    nextMautId,
                    mautAbschnitt,
                    fzgId,
                    kategorieId,
                    Date.valueOf(LocalDate.now()),
                    kosten
            );

            mauterhebungMapper.insert(mauterhebung);
        }
    }

    // Hilfsmethode für Schadstoffklasse
    private int getSchadstoffklasseFromFahrzeug(FahrzeugMapper fahrzeugMapper, String kennzeichen) {
        try (var statement = connection.prepareStatement(
                "SELECT f.SSKL_ID FROM FAHRZEUG f WHERE f.KENNZEICHEN = ?")) {
            statement.setString(1, kennzeichen);
            var rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new RuntimeException("Schadstoffklasse nicht gefunden für Fahrzeug: " + kennzeichen);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Hilfsmethode für FZG_ID aus FAHRZEUGGERAT
    private Long getFzgIdFromFahrzeugGerat(String kennzeichen) {
        try (var statement = connection.prepareStatement(
                "SELECT f2.FZG_ID " +
                        "FROM FAHRZEUG f " +
                        "INNER JOIN FAHRZEUGGERAT f2 ON f.FZ_ID = f2.FZ_ID " +
                        "WHERE f.KENNZEICHEN = ?")) {
            statement.setString(1, kennzeichen);
            var rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
