package de.htwberlin.dbtech.aufgaben.ue03.TableObjects;

public class RECHNUNNGSSTATUS {
    private int STATUS_ID;
    private String STATUS_BEZ;

    public RECHNUNNGSSTATUS(int STATUS_ID, String STATUS_BEZ) {
        this.STATUS_ID = STATUS_ID;
        this.STATUS_BEZ = STATUS_BEZ;
    }

    public int getSTATUS_ID() {
        return STATUS_ID;
    }

    public void setSTATUS_ID(int STATUS_ID) {
        this.STATUS_ID = STATUS_ID;
    }

    public String getSTATUS_BEZ() {
        return STATUS_BEZ;
    }

    public void setSTATUS_BEZ(String STATUS_BEZ) {
        this.STATUS_BEZ = STATUS_BEZ;
    }
}
