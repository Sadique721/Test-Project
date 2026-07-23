DELIMITER $$

USE `Savbillcpm`$$
DROP PROCEDURE IF EXISTS `doLog`$$

CREATE DEFINER=`root`@`%` PROCEDURE `Savbillcpm`.`doLog`(in logMsg nvarchar(2048))
BEGIN  
  insert into procedurelog (msg) values(logMsg);
END$$
DELIMITER ;