INSERT INTO savbillcpm.tblmbranch (
    branchid, name, status, MVNOID, is_deleted,
    createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate,
    branch_code, revenue_sharing, sharing_percentage, dunning_days,
    createbyname, updatebyname
)
SELECT
    b.branchid,
    LEFT(b.name, 100),
    b.status,
    b.MVNOID,
    b.is_deleted,
    CAST(b.createdbystaffid AS DECIMAL(20,0)),
    b.createdate,
    CAST(b.lastmodifiedbystaffid AS DECIMAL(20,0)),
    b.lastmodifieddate,
    b.branch_code,
    b.revenue_sharing,
    b.sharing_percentage,
    b.dunning_days,
    b.createbyname,
    b.updatebyname
FROM commongateway.tblmbranch b
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    status = VALUES(status),
    MVNOID = VALUES(MVNOID),
    is_deleted = VALUES(is_deleted),
    lastmodifiedbystaffid = VALUES(lastmodifiedbystaffid),
    lastmodifieddate = VALUES(lastmodifieddate),
    branch_code = VALUES(branch_code),
    revenue_sharing = VALUES(revenue_sharing),
    sharing_percentage = VALUES(sharing_percentage),
    dunning_days = VALUES(dunning_days),
    updatebyname = VALUES(updatebyname);
   
   
   INSERT INTO savbillinventorymanagement.tblmbranch (
    branchid, name, status, MVNOID, is_deleted,
    createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate,
    branch_code, revenue_sharing, sharing_percentage, dunning_days,
    createbyname, updatebyname
)
SELECT
    b.branchid,
    LEFT(b.name, 250),
    b.status,
    b.MVNOID,
    b.is_deleted,
    b.createdbystaffid,
    b.createdate,
    b.lastmodifiedbystaffid,
    b.lastmodifieddate,
    b.branch_code,
    b.revenue_sharing,
    b.sharing_percentage,
    b.dunning_days,
    b.createbyname,
    b.updatebyname
FROM commongateway.tblmbranch b
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    status = VALUES(status),
    MVNOID = VALUES(MVNOID),
    is_deleted = VALUES(is_deleted),
    lastmodifiedbystaffid = VALUES(lastmodifiedbystaffid),
    lastmodifieddate = VALUES(lastmodifieddate),
    branch_code = VALUES(branch_code),
    revenue_sharing = VALUES(revenue_sharing),
    sharing_percentage = VALUES(sharing_percentage),
    dunning_days = VALUES(dunning_days),
    updatebyname = VALUES(updatebyname);
   
   INSERT INTO savbillrevenuemanagement.tblmbranch (
    branchid, name, status, MVNOID, is_deleted,
    createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate,
    branch_code, revenue_sharing, sharing_percentage, dunning_days,
    createbyname, updatebyname
)
SELECT
    b.branchid,
    LEFT(b.name, 250),
    b.status,
    b.MVNOID,
    b.is_deleted,
    b.createdbystaffid,
    b.createdate,
    b.lastmodifiedbystaffid,
    b.lastmodifieddate,
    b.branch_code,
    b.revenue_sharing,
    b.sharing_percentage,
    b.dunning_days,
    b.createbyname,
    b.updatebyname
FROM commongateway.tblmbranch b
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    status = VALUES(status),
    MVNOID = VALUES(MVNOID),
    is_deleted = VALUES(is_deleted),
    lastmodifiedbystaffid = VALUES(lastmodifiedbystaffid),
    lastmodifieddate = VALUES(lastmodifieddate),
    branch_code = VALUES(branch_code),
    revenue_sharing = VALUES(revenue_sharing),
    sharing_percentage = VALUES(sharing_percentage),
    dunning_days = VALUES(dunning_days),
    updatebyname = VALUES(updatebyname);
   
   INSERT INTO savbillticketmanagement.tblmbranch (
    branchid, name, status, MVNOID, is_deleted,
    createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate,
    createbyname, updatebyname, branch_code,
    revenue_sharing, sharing_percentage, dunning_days
)
SELECT
    b.branchid,
    LEFT(b.name, 100),
    b.status,
    b.MVNOID,
    b.is_deleted,
    CAST(b.createdbystaffid AS DECIMAL(20,0)),
    b.createdate,
    CAST(b.lastmodifiedbystaffid AS DECIMAL(20,0)),
    b.lastmodifieddate,
    b.createbyname,
    b.updatebyname,
    b.branch_code,
    b.revenue_sharing,
    b.sharing_percentage,
    b.dunning_days
FROM commongateway.tblmbranch b
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    status = VALUES(status),
    MVNOID = VALUES(MVNOID),
    is_deleted = VALUES(is_deleted),
    lastmodifiedbystaffid = VALUES(lastmodifiedbystaffid),
    lastmodifieddate = VALUES(lastmodifieddate),
    branch_code = VALUES(branch_code),
    revenue_sharing = VALUES(revenue_sharing),
    sharing_percentage = VALUES(sharing_percentage),
    dunning_days = VALUES(dunning_days),
    updatebyname = VALUES(updatebyname);
   
   
  
 
 INSERT INTO savbillcpm.tblmbranchservicearearel (
    branchid, servicearea_id, created_on, lastmodified_on,
    CREATEDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID, LASTMODIFIEDDATE
)
SELECT
    b.branchid,
    b.servicearea_id,
    b.created_on,
    b.lastmodified_on,
    b.createdate,
    CAST(b.createdbystaffid AS DECIMAL(20,0)),
    CAST(b.lastmodifiedbystaffid AS DECIMAL(20,0)),
    b.lastmodifieddate
FROM commongateway.tbltbranchservicearearel b
ON DUPLICATE KEY UPDATE
    lastmodified_on = VALUES(lastmodified_on),
    LASTMODIFIEDBYSTAFFID = VALUES(LASTMODIFIEDBYSTAFFID),
    LASTMODIFIEDDATE = VALUES(LASTMODIFIEDDATE);
   
   INSERT INTO savbillrevenuemanagement.tbltbranchservicearearel (
    id, branchid, servicearea_id, created_on, lastmodified_on,
    createdate, createdbystaffid, lastmodifiedbystaffid, lastmodifieddate,
    createbyname, updatebyname
)
SELECT
    b.id,
    b.branchid,
    b.servicearea_id,
    b.created_on,
    b.lastmodified_on,
    b.createdate,
    b.createdbystaffid,
    b.lastmodifiedbystaffid,
    b.lastmodifieddate,
    b.createbyname,
    b.updatebyname
FROM commongateway.tbltbranchservicearearel b
ON DUPLICATE KEY UPDATE
    branchid = VALUES(branchid),
    servicearea_id = VALUES(servicearea_id),
    lastmodified_on = VALUES(lastmodified_on),
    lastmodifiedbystaffid = VALUES(lastmodifiedbystaffid),
    lastmodifieddate = VALUES(lastmodifieddate),
    updatebyname = VALUES(updatebyname);
   
   
   INSERT INTO savbillticketmanagement.tblmbranchservicearearel (
    branchid, servicearea_id, created_on, lastmodified_on,
    CREATEDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID, LASTMODIFIEDDATE,
    createbyname, updatebyname
)
SELECT
    b.branchid,
    b.servicearea_id,
    b.created_on,
    b.lastmodified_on,
    b.createdate,
    CAST(b.createdbystaffid AS DECIMAL(20,0)),
    CAST(b.lastmodifiedbystaffid AS DECIMAL(20,0)),
    b.lastmodifieddate,
    b.createbyname,
    b.updatebyname
FROM commongateway.tbltbranchservicearearel b
ON DUPLICATE KEY UPDATE
    lastmodified_on = VALUES(lastmodified_on),
    LASTMODIFIEDBYSTAFFID = VALUES(LASTMODIFIEDBYSTAFFID),
    LASTMODIFIEDDATE = VALUES(LASTMODIFIEDDATE),
    updatebyname = VALUES(updatebyname);