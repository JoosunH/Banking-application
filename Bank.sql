// Create a database 
CREATE DATABASE BANK; 


// Create table
CREATE TABLE `customer` (

 `account_number` int NOT NULL AUTO_INCREMENT,

 `customer_name` varchar(45) DEFAULT NULL,

 `balance` varchar(45) DEFAULT NULL,

 `pass_code` int DEFAULT NULL,

 PRIMARY KEY (`account_number`),

 UNIQUE KEY `cname_UNIQUE` (`customer_name`)

) ;