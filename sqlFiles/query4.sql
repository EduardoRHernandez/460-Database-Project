SELECT eq.EID as EquipmentID, eq.ETYPE as EquipmentType, eq.ESIZE as EquipmentSize, eq.ESTATUS as CurrentStatus, log.OLDTYPE,  log.NEWTYPE, log.OLDSIZE, log.NEWSIZE, log.OLDSTATUS, log.NEWSTATUS, TO_CHAR(log.changeDate, 'YYYY-MM-DD HH24:MI:SS') AS ChangeDate
FROM 
    EquipmentChangeLog log 
JOIN
    Equipment eq ON log.EID = eq.EID
WHERE 
    eq.EID = 2
ORDER BY 
    log.changeDate DESC;