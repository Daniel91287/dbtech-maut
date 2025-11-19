package de.htwberlin.dbtech.aufgaben.ue03.TableObjects;

public class SCHADSTOFFKLASSE {
    private int SSKL_ID;
    private String MAUTSCHADSTOFFKLASSE;
    private String BESCHREIBUNG;

    public SCHADSTOFFKLASSE(int SSKL_ID, String MAUTSCHADSTOFFKLASSE, String BESCHREIBUNG) {
        this.SSKL_ID = SSKL_ID;
        this.MAUTSCHADSTOFFKLASSE = MAUTSCHADSTOFFKLASSE;
        this.BESCHREIBUNG = BESCHREIBUNG;
    }

    public int getSSKL_ID() {
        return SSKL_ID;
    }

    public void setSSKL_ID(int SSKL_ID) {
        this.SSKL_ID = SSKL_ID;
    }

    public String getMAUTSCHADSTOFFKLASSE() {
        return MAUTSCHADSTOFFKLASSE;
    }

    public void setMAUTSCHADSTOFFKLASSE(String MAUTSCHADSTOFFKLASSE) {
        this.MAUTSCHADSTOFFKLASSE = MAUTSCHADSTOFFKLASSE;
    }

    public String getBESCHREIBUNG() {
        return BESCHREIBUNG;
    }

    public void setBESCHREIBUNG(String BESCHREIBUNG) {
        this.BESCHREIBUNG = BESCHREIBUNG;
    }
}
