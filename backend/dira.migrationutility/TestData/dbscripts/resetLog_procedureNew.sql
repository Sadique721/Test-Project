DELIMITER $$

USE `Savbillcpm` $$

CREATE DEFINER=`root`@`%` PROCEDURE `savbillcpm`.`resetLog`()
BEGIN   
    create table if not exists procedurelog (ts timestamp default current_timestamp, module varchar(64), msg varchar(2048)); 
    truncate table procedurelog;
END$$
DELIMITER ;
