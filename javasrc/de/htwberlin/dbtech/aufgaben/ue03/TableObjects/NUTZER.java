package de.htwberlin.dbtech.aufgaben.ue03.TableObjects;

public class NUTZER {
   private int NUTZER_ID;
   private String STATUS;
   private String FIRMENNAME;
   private String VORNAME;
   private String NACHNAME;
   private String LAND;
   private String STRASSE;
   private int HAUSNUMMER;
   private int POSTLEITZAHL;
   private int RECHNUNGSINTERVALL;
   private String RECHNUNGSZUSTELLUNG;

    public NUTZER(int NUTZER_ID, String STATUS, String FIRMENNAME, String VORNAME, String NACHNAME, String LAND, String STRASSE, int HAUSNUMMER, int POSTLEITZAHL, int RECHNUNGSINTERVALL, String RECHNUNGSZUSTELLUNG) {
        this.NUTZER_ID = NUTZER_ID;
        this.STATUS = STATUS;
        this.FIRMENNAME = FIRMENNAME;
        this.VORNAME = VORNAME;
        this.NACHNAME = NACHNAME;
        this.LAND = LAND;
        this.STRASSE = STRASSE;
        this.HAUSNUMMER = HAUSNUMMER;
        this.POSTLEITZAHL = POSTLEITZAHL;
        this.RECHNUNGSINTERVALL = RECHNUNGSINTERVALL;
        this.RECHNUNGSZUSTELLUNG = RECHNUNGSZUSTELLUNG;
    }

    public int getNUTZER_ID() {
        return NUTZER_ID;
    }

    public void setNUTZER_ID(int NUTZER_ID) {
        this.NUTZER_ID = NUTZER_ID;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getFIRMENNAME() {
        return FIRMENNAME;
    }

    public void setFIRMENNAME(String FIRMENNAME) {
        this.FIRMENNAME = FIRMENNAME;
    }

    public String getVORNAME() {
        return VORNAME;
    }

    public void setVORNAME(String VORNAME) {
        this.VORNAME = VORNAME;
    }

    public String getNACHNAME() {
        return NACHNAME;
    }

    public void setNACHNAME(String NACHNAME) {
        this.NACHNAME = NACHNAME;
    }

    public String getLAND() {
        return LAND;
    }

    public void setLAND(String LAND) {
        this.LAND = LAND;
    }

    public String getSTRASSE() {
        return STRASSE;
    }

    public void setSTRASSE(String STRASSE) {
        this.STRASSE = STRASSE;
    }

    public int getHAUSNUMMER() {
        return HAUSNUMMER;
    }

    public void setHAUSNUMMER(int HAUSNUMMER) {
        this.HAUSNUMMER = HAUSNUMMER;
    }

    public int getPOSTLEITZAHL() {
        return POSTLEITZAHL;
    }

    public void setPOSTLEITZAHL(int POSTLEITZAHL) {
        this.POSTLEITZAHL = POSTLEITZAHL;
    }

    public int getRECHNUNGSINTERVALL() {
        return RECHNUNGSINTERVALL;
    }

    public void setRECHNUNGSINTERVALL(int RECHNUNGSINTERVALL) {
        this.RECHNUNGSINTERVALL = RECHNUNGSINTERVALL;
    }

    public String getRECHNUNGSZUSTELLUNG() {
        return RECHNUNGSZUSTELLUNG;
    }

    public void setRECHNUNGSZUSTELLUNG(String RECHNUNGSZUSTELLUNG) {
        this.RECHNUNGSZUSTELLUNG = RECHNUNGSZUSTELLUNG;
    }
}
