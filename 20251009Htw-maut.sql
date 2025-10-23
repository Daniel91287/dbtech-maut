--Aufgabe 1
SELECT a.nutzer_id, a.emmitent
From ZAHLART a
Join ZAHLTYP t on a.ZTYP_ID = t.ztyp_id
WHERE t.ZTYP_BEZEICHNUNG = 'Tankkarte'
--    AND (z.GUELTIG_BIS IS NULL OR z.GUELTIG_BIS >= SYSDATE)
    AND a.STATUS = 'active'
    
    