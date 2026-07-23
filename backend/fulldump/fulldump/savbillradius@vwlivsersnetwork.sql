-- MySQLShell dump 2.0.1  Distrib Ver 8.0.32 for Linux on x86_64 - for MySQL 8.0.32 (MySQL Community Server (GPL)), for Linux (x86_64)
--
-- Host: localhost    Database: savbillradius    Table: vwlivsersnetwork
-- ------------------------------------------------------
-- Server version	8.0.32

--
-- Final view structure for view `vwlivsersnetwork`
--

/*!50001 DROP VIEW IF EXISTS `vwlivsersnetwork`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`Savbillradius`@`%` SQL SECURITY DEFINER VIEW `vwlivsersnetwork` AS select `tblcustomers`.`servicearea_id` AS `serviceAreaId`,`tblcustomers`.`network_device_id` AS `oltId`,`tblcustomers`.`oltslotid` AS `slotId`,`tblcustomers`.`oltportid` AS `portId` from `tblcustomers` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
