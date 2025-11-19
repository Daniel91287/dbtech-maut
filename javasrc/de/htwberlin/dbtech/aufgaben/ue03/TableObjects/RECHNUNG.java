package de.htwberlin.dbtech.aufgaben.ue03.TableObjects;

import java.sql.Date;

public class RECHNUNG {
    private int R_ID;
    private int STATUS_ID;
    private int NUTZER_ID;
    private Date BUCHUNGSDATUM;
    private int SUMME;

    public RECHNUNG(int r_ID, int STATUS_ID, int NUTZER_ID, Date BUCHUNGSDATUM, int SUMME) {
        R_ID = r_ID;
        this.STATUS_ID = STATUS_ID;
        this.NUTZER_ID = NUTZER_ID;
        this.BUCHUNGSDATUM = BUCHUNGSDATUM;
        this.SUMME = SUMME;
    }

    public int getR_ID() {
        return R_ID;
    }

    public void setR_ID(int r_ID) {
        R_ID = r_ID;
    }

    public int getSTATUS_ID() {
        return STATUS_ID;
    }

    public void setSTATUS_ID(int STATUS_ID) {
        this.STATUS_ID = STATUS_ID;
    }

    public int getNUTZER_ID() {
        return NUTZER_ID;
    }

    public void setNUTZER_ID(int NUTZER_ID) {
        this.NUTZER_ID = NUTZER_ID;
    }

    public Date getBUCHUNGSDATUM() {
        return BUCHUNGSDATUM;
    }

    public void setBUCHUNGSDATUM(Date BUCHUNGSDATUM) {
        this.BUCHUNGSDATUM = BUCHUNGSDATUM;
    }

    public int getSUMME() {
        return SUMME;
    }

    public void setSUMME(int SUMME) {
        this.SUMME = SUMME;
    }
}
