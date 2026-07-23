DELIMITER $$

USE `Savbillcpm` $$

CREATE DEFINER=`root`@`%` PROCEDURE `savbillcpm`.`doLog`(in moduleName varchar(64),in logMsg varchar(2048))
BEGIN  
  insert into procedurelog (module,msg) values(moduleName,logMsg);
END$$
DELIMITER ;
