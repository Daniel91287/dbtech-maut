package de.htwberlin.dbtech.aufgaben.ue03.TableObjects;

public class ZAHLTYP {
    private int ZTYP_ID;
    private String ZTYP_BEZEICHNUNG;

    public ZAHLTYP(int ZTYP_ID, String ZTYP_BEZEICHNUNG) {
        this.ZTYP_ID = ZTYP_ID;
        this.ZTYP_BEZEICHNUNG = ZTYP_BEZEICHNUNG;
    }

    public int getZTYP_ID() {
        return ZTYP_ID;
    }

    public void setZTYP_ID(int ZTYP_ID) {
        this.ZTYP_ID = ZTYP_ID;
    }

    public String getZTYP_BEZEICHNUNG() {
        return ZTYP_BEZEICHNUNG;
    }

    public void setZTYP_BEZEICHNUNG(String ZTYP_BEZEICHNUNG) {
        this.ZTYP_BEZEICHNUNG = ZTYP_BEZEICHNUNG;
    }
}
