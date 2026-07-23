CREATE TRIGGER `trg_after_insert_tbltliveuser` AFTER INSERT ON `tbltliveuser` FOR EACH ROW BEGIN
    DECLARE plan_name VARCHAR(500) DEFAULT '';
    DECLARE Acct_status_type VARCHAR(500) DEFAULT 'Start';

    SELECT pp.NAME
    INTO plan_name
    FROM tblmpostpaidplan pp
    WHERE pp.POSTPAIDPLANID =
          (SELECT planid
           FROM savbillradius.tblcustquotadtls tcq
           WHERE custid = (
               SELECT tc.custid
               FROM tblcustomers tc
               WHERE tc.username = NEW.userName
           )
           ORDER BY tcq.quotadtlsid DESC
           LIMIT 1);


    INSERT INTO tblmprocesscdr (
        USERNAME,
        SESSIONID,
        FRAMEDIPADDRESS,
        NASIPADDRESS,
        MACADDRESS,
        NASPORTID,
        FRAMED_IPV6_ADDRESS,
        FRAMED_INTERFACE_ID,
        DELEGATED_IPV6_PREFIX,
        STARTTIME,
        ENDTIME,
        REQUESTTYPE,
        AGGREGATEKEY,
        SESSIONAUTHRULE,
        UPLOAD,
        DOWNLOAD,
        TOTAL,
        CDRTIME
    )
    VALUES (
               NEW.userName,
               NEW.AcctSessionId,
               NEW.FramedIPAddress,
               NEW.NASIPAddress,
               REPLACE(NEW.CallingStationId, '-', ':'),
               NEW.NASPort,
               NEW.framedipv6address,
               (
                   SELECT GROUP_CONCAT(SUBSTRING(REPLACE(NEW.FramedInterfaceId, ':', ''), (n * 2) + 1, 2) SEPARATOR ':')
                   FROM (
                            SELECT @rownum := @rownum + 1 AS n
                            FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
                                 (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
                                 (SELECT @rownum := -1) r
                        ) numbers
                   WHERE (n * 2) < LENGTH(REPLACE(NEW.FramedInterfaceId, ':', ''))
               )
               ,
               NEW.DelegatedIPv6Prefix,
               NEW.createdate,
               NEW.lastmodificationdate,
               IFNULL(NEW.AcctStatusType, 'Start'),
               "Default Service",
               CONCAT(plan_name, "##", "TOTAL_QoS_Profile", "#",
                      CASE
                          WHEN NEW.isthrottlespeed THEN '1'
                          ELSE '0'
                          END),0,0,0,0
           );
END
