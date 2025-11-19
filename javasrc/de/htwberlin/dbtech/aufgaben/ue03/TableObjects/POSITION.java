package de.htwberlin.dbtech.aufgaben.ue03.TableObjects;

public class POSITION {
    private int MAUT_ID;
    private int R_ID;

    public POSITION(int MAUT_ID, int r_ID) {
        this.MAUT_ID = MAUT_ID;
        R_ID = r_ID;
    }

    public int getMAUT_ID() {
        return MAUT_ID;
    }

    public void setMAUT_ID(int MAUT_ID) {
        this.MAUT_ID = MAUT_ID;
    }

    public int getR_ID() {
        return R_ID;
    }

    public void setR_ID(int r_ID) {
        R_ID = r_ID;
    }
}
