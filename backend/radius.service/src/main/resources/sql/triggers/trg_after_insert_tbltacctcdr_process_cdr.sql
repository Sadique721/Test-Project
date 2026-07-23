CREATE TRIGGER `trg_after_insert_tbltacctcdr` AFTER INSERT ON `tbltacctcdr` FOR EACH ROW BEGIN
    DECLARE total_upload BIGINT DEFAULT 0;
    DECLARE total_download BIGINT DEFAULT 0;
    DECLARE total_sum BIGINT DEFAULT 0;
    DECLARE total_session_time BIGINT DEFAULT 0;

    -- Calculate aggregated values for UPLOAD, DOWNLOAD, TOTAL, and CDRTIME (AcctSessionTime) by SESSIONID
    SELECT
        IFNULL(SUM(UPLOAD), 0),
        IFNULL(SUM(DOWNLOAD), 0),
        IFNULL(SUM(TOTAL), 0),
        IFNULL(SUM(CDRTIME), 0)
    INTO
        total_upload,
        total_download,
        total_sum,
        total_session_time
    FROM
        tblmprocesscdr
    WHERE
            SESSIONID = NEW.AcctSessionId;

    -- Insert a new record into tblmprocesscdr
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
        UPLOAD,
        DOWNLOAD,
        TOTAL,
        CDRTIME,
        STARTTIME,
        ENDTIME,
        REQUESTTYPE
    )
    VALUES (
               NEW.userName,
               NEW.AcctSessionId,
               NEW.FramedIPAddress,
               NEW.NASIPAddress,
               NEW.CallingStationId,
               NEW.NASPort,
               NEW.framedipv6address,
               NEW.FramedInterfaceId,
               NEW.DelegatedIPv6Prefix,
               -- Add current values to previously aggregated values
               NEW.AcctInputOctets - total_upload,
               NEW.AcctOutputOctets - total_download ,
               (NEW.AcctInputOctets + NEW.AcctOutputOctets) - total_sum,
               NEW.AcctSessionTime - total_session_time, -- Cumulative session time
               NEW.createdate,
               NEW.lastmodificationdate,
               NEW.AcctStatusType
           );
END
