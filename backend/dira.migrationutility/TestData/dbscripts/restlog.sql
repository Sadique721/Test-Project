DELIMITER $$

USE `Savbillcpm`$$
DROP PROCEDURE IF EXISTS `resetLog`$$

CREATE DEFINER=`root`@`%` PROCEDURE `Savbillcpm`.`resetLog`()
BEGIN   
    create table if not exists procedurelog (ts timestamp default current_timestamp, msg varchar(2048)); 
    truncate table procedurelog;
END$$
DELIMITER ;