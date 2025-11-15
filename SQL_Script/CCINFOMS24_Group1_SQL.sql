/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  marcquizon
 * Created: Nov 15, 2025
 */
CREATE DATABASE IF NOT EXISTS CCINFOMS24_GROUP1_DB;
DROP TABLE IF EXISTS technicians;
DROP TABLE IF EXISTS machines;
DROP TABLE IF EXISTS software;
DROP TABLE IF EXISTS tester;
DROP TABLE IF EXISTS maintenance;
DROP TABLE IF EXISTS patch;
DROP TABLE IF EXISTS feedback;

-- Creating the CORE Records
CREATE TABLE technicians(
    technicianID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    position ENUM('Desktop Support', 'System Admin', 'Network Admin') NOT NULL,
    password VARCHAR(50) NOT NULL,
    status ENUM('Available', 'Unavailable') DEFAULT 'Available' NOT NULL
    );
    
CREATE TABLE machines (
    machineID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    machineName VARCHAR(30) NOT NULL,
    deviceType ENUM('PC', 'Switch', 'Router', 'Server') NOT NULL,
    status ENUM('Healthy', 'Vulnerable', 'Scheduled for patch') DEFAULT 'Healthy'
);

CREATE TABLE software (
    softwareID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    softwareName VARCHAR(30) NOT NULL,
    type ENUM('Application', 'System', 'Programming', 'Network', 'Server') NOT NULL,
    version VARCHAR(30) NOT NULL,
    releaseDate DATE NOT NULL
);

CREATE TABLE tester (
    testerID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);
    
-- Creating the transactional records
CREATE TABLE patch (
    patchID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    type ENUM('Application', 'System', 'Programming', 'Network', 'Server') NOT NULL,
    description VARCHAR(255) NOT NULL,
    releaseDate DATE NOT NULL,
    status ENUM('New', 'Working', 'Not Working', 'Inactive') DEFAULT 'New' NOT NULL,
    softwareID VARCHAR(6) NOT NULL,
    machineID VARCHAR(6) NOT NULL,
    technicianID VARCHAR(6) NOT NULL,

    FOREIGN KEY (machineID) REFERENCES machine(machineID),
    FOREIGN KEY (softwareID) REFERENCES software(softwareID),
    FOREIGN KEY (technicianID) REFERENCES technicians(technicianID)
);


CREATE TABLE maintenance(
    maintenanceID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    workType ENUM('Deploy', 'Rollback'),
    patchID VARCHAR(6) NOT NULL,
    technicianIDassigned VARCHAR(6) NOT NULL,
    dateAssigned DATE NOT NULL,
    targetDeadline DATE NOT NULL,
    dateFinished DATE,
    status ENUM('Not Started', 'In progress', 'Done') DEFAULT 'Not Started',
    description VARCHAR(255) NOT NULL,

    FOREIGN KEY (patchID) REFERENCES patch(patchID),
    FOREIGN KEY (technicianIDassigned) REFERENCES technicians(technicianID)
    );
    

CREATE TABLE feedback (
    feedbackID INT PRIMARY KEY AUTO_INCREMENT,
    testerID VARCHAR(6) NOT NULL,
    patchID INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    rating int NOT NULL,

    FOREIGN KEY (testerID) REFERENCES tester(testerID),
    FOREIGN KEY (patchID) REFERENCES patch(patchID)
);






