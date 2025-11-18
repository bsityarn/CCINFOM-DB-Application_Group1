/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  marcquizon
 * Created: Nov 15, 2025
 */
-- CREATE DATABASE IF NOT EXISTS CCINFOMS24_GROUP1_DB;
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
    status ENUM('Available', 'Unavailable', 'Inactive') DEFAULT 'Available' NOT NULL
    );

INSERT INTO technicians (technicianID, firstName, lastName, email, position, password, status)
VALUES
('TH0001', 'James', 'Santos', 'jsantos@ptrackerdb.com', 'Desktop Support', 'pass123', 'Available'),
('TH0002', 'Maria', 'Reyes', 'mreyes@ptrackerdb.com', 'System Admin', 'pass123', 'Available'),
('TH0003', 'John', 'Cruz', 'jcruz@ptrackerdb.com', 'Network Admin', 'pass123', 'Available'),
('TH0004', 'Angela', 'DelaCruz', 'adelacruz@ptrackerdb.com', 'Desktop Support', 'pass123', 'Available'),
('TH0005', 'Robert', 'Garcia', 'rgarcia@ptrackerdb.com', 'System Admin', 'pass123', 'Available'),
('TH0006', 'Carla', 'Mendoza', 'cmendoza@ptrackerdb.com', 'Network Admin', 'pass123', 'Available'),
('TH0007', 'Joseph', 'Villanueva', 'jvillanueva@ptrackerdb.com', 'Desktop Support', 'pass123', 'Available'),
('TH0008', 'Patricia', 'Torres', 'ptorres@ptrackerdb.com', 'System Admin', 'pass123', 'Available'),
('TH0009', 'Daniel', 'Lopez', 'dlopez@ptrackerdb.com', 'Network Admin', 'pass123', 'Available'),
('TH0010', 'Sophia', 'Bautista', 'sbautista@ptrackerdb.com', 'Desktop Support', 'pass123', 'Available');

    
CREATE TABLE machines (
    machineID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    machineName VARCHAR(30) NOT NULL,
    deviceType ENUM('PC', 'Switch', 'Router', 'Server') NOT NULL,
    status ENUM('Healthy', 'Vulnerable', 'Scheduled for patch') DEFAULT 'Healthy'
);

INSERT INTO machines (machineID, machineName, deviceType, status)
VALUES
('MC0001', 'Workstation Alpha', 'PC', 'Healthy'),
('MC0002', 'Database Node 1', 'Server', 'Healthy'),
('MC0003', 'API Server 2', 'Server', 'Scheduled for patch'),
('MC0004', 'Core Switch A', 'Switch', 'Vulnerable'),
('MC0005', 'Network Router X', 'Router', 'Scheduled for patch'),
('MC0006', 'Development PC Bravo', 'PC', 'Healthy'),
('MC0007', 'Backup Server Delta', 'Server', 'Healthy'),
('MC0008', 'Core Switch B', 'Switch', 'Healthy'),
('MC0009', 'Router Zeta', 'Router', 'Vulnerable'),
('MC0010', 'File Server Omega', 'Server', 'Scheduled for patch');


CREATE TABLE software (
    softwareID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    softwareName VARCHAR(30) NOT NULL,
    type ENUM('Application', 'System', 'Programming', 'Network', 'Server') NOT NULL,
    version VARCHAR(30) NOT NULL,
    releaseDate DATE NOT NULL
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

INSERT INTO software (softwareID, softwareName, type, version, releaseDate)
VALUES
('SF0001', 'AuthSecure', 'Application', 'v1.0.0', '2025-09-20'),
('SF0002', 'DataLink DB Engine', 'Programming', 'v3.5.0', '2025-09-30'),
('SF0003', 'UIFlow Framework', 'Application', 'v2.0.0', '2025-10-05'),
('SF0004', 'SysBoost Core', 'System', 'v1.2.0', '2025-10-10'),
('SF0005', 'NetConnect Suite', 'Network', 'v4.2.0', '2025-10-15'),
('SF0006', 'ServerMon Dashboard', 'Server', 'v2.1.0', '2025-10-20'),
('SF0007', 'PacketTrace Utility', 'Network', 'v1.5.0', '2025-10-22'),
('SF0008', 'AutoDeploy Manager', 'System', 'v3.0.1', '2025-10-25'),
('SF0009', 'CodeBridge IDE', 'Programming', 'v5.4.2', '2025-10-28'),
('SF0010', 'UserFlow Portal', 'Application', 'v1.3.5', '2025-11-01');


CREATE TABLE tester (
    testerID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

INSERT INTO tester (testerID, firstName, lastName, email, password, status)
VALUES
('TS0001', 'Janelle', 'Morales', 'janelle@ptrackerdb.com', 'pass123', 'Active'),
('TS0002', 'Ryan', 'Lim', 'ryan@ptrackerdb.com', 'pass123', 'Active'),
('TS0003', 'Sofia', 'Cruz', 'sofia@ptrackerdb.com', 'pass123', 'Active'),
('TS0004', 'Daniel', 'Tan', 'daniel@ptrackerdb.com', 'pass123', 'Inactive'),
('TS0005', 'Lea', 'Reyes', 'lea@ptrackerdb.com', 'pass123', 'Active'),
('TS0006', 'Kevin', 'Ocampo', 'kevin@ptrackerdb.com', 'pass123', 'Active'),
('TS0007', 'Marianne', 'Lopez', 'marianne@ptrackerdb.com', 'pass123', 'Active'),
('TS0008', 'Patrick', 'Dizon', 'patrick@ptrackerdb.com', 'pass123', 'Inactive'),
('TS0009', 'Faith', 'Garcia', 'faith@ptrackerdb.com', 'pass123', 'Active'),
('TS0010', 'Oliver', 'Chua', 'oliver@ptrackerdb.com', 'pass123', 'Active');


    
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

    FOREIGN KEY (machineID) REFERENCES machines(machineID),
    FOREIGN KEY (softwareID) REFERENCES software(softwareID),
    FOREIGN KEY (technicianID) REFERENCES technicians(technicianID)
);

INSERT INTO patch 
(patchID, name, type, description, releaseDate, status, softwareID, machineID, technicianID)
VALUES
('PT0001', 'Login Security Fix', 'Application', 'Fixes authentication bypass vulnerability in login module', '2025-09-28', 'Working', 'SF0001', 'MC0001', 'TH0001'),
('PT0002', 'Database Driver Update', 'Programming', 'Updates JDBC driver to improve query performance and stability', '2025-10-08', 'Working', 'SF0002', 'MC0002', 'TH0002'),
('PT0003', 'UI Rollback Patch', 'Application', 'Reverts UI changes causing layout issues in dashboard module', '2025-10-14', 'New', 'SF0003', 'MC0001', 'TH0003'),
('PT0004', 'Performance Optimization', 'System', 'Improves cache handling and reduces latency for concurrent requests', '2025-10-19', 'Working', 'SF0004', 'MC0003', 'TH0002'),
('PT0005', 'Security Protocol Update', 'Network', 'Implements TLS 1.3 and enhanced encryption algorithms for secure connections', '2025-10-21', 'Working', 'SF0001', 'MC0002', 'TH0004'),
('PT0006', 'Network Stability Rollback', 'Network', 'Reverts network module changes that caused disconnection issues', '2025-10-26', 'Not Working', 'SF0005', 'MC0005', 'TH0005'),
('PT0007', 'Analytics Deployment Patch', 'Application', 'Adds analytics module for real-time user behavior tracking', '2025-10-31', 'New', 'SF0003', 'MC0001', 'TH0003'),
('PT0008', 'Authentication System Overhaul', 'Programming', 'Adds multi-factor authentication and token validation service', '2025-11-01', 'New', 'SF0002', 'MC0004', 'TH0006'),
('PT0009', 'Firmware Compatibility Patch', 'System', 'Fixes compatibility between firmware v2.5 and legacy network drivers', '2025-11-04', 'Inactive', 'SF0005', 'MC0005', 'TH0004'),
('PT0010', 'API Optimization Patch', 'Server', 'Enhances API throughput by optimizing thread pooling and memory usage', '2025-11-09', 'New', 'SF0004', 'MC0003', 'TH0002');


CREATE TABLE maintenance(
    maintenanceID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    workType ENUM('Deploy', 'Rollback'),
    patchID VARCHAR(6) NOT NULL,
    technicianIDassigned VARCHAR(6) NOT NULL,
    dateAssigned DATE NOT NULL DEFAULT NOW(),
    targetDeadline DATE NOT NULL,
    dateFinished DATE,
    status ENUM('Not Started', 'In progress', 'Done') DEFAULT 'Not Started',
    description VARCHAR(255) NOT NULL,

    FOREIGN KEY (patchID) REFERENCES patch(patchID),
    FOREIGN KEY (technicianIDassigned) REFERENCES technicians(technicianID)
    );

INSERT INTO maintenance 
(maintenanceID, workType, patchID, technicianIDassigned, dateAssigned, targetDeadline, dateFinished, status, description)
VALUES
('MT0001', 'Deploy', 'PT0001', 'TH0001', '2025-10-01', '2025-10-05', '2025-10-04', 'Done', 'Deployed security patch for login module'),
('MT0002', 'Deploy', 'PT0002', 'TH0002', '2025-10-10', '2025-10-14', '2025-10-13', 'Done', 'Applied version update to database driver'),
('MT0003', 'Rollback', 'PT0003', 'TH0003', '2025-10-15', '2025-10-18', '2025-10-17', 'Done', 'Rolled back faulty UI update causing crashes'),
('MT0004', 'Deploy', 'PT0004', 'TH0002', '2025-10-20', '2025-10-25', NULL, 'In progress', 'Deploying patch for performance optimization'),
('MT0005', 'Deploy', 'PT0005', 'TH0004', '2025-10-22', '2025-10-26', NULL, 'In progress', 'Implementing new security protocol'),
('MT0006', 'Rollback', 'PT0006', 'TH0005', '2025-10-27', '2025-10-29', '2025-10-29', 'Done', 'Rollback network patch due to connectivity issues'),
('MT0007', 'Deploy', 'PT0007', 'TH0003', '2025-11-01', '2025-11-05', NULL, 'Not Started', 'Scheduled deployment for analytics module'),
('MT0008', 'Deploy', 'PT0008', 'TH0006', '2025-11-02', '2025-11-07', NULL, 'Not Started', 'Deploying new authentication mechanism'),
('MT0009', 'Rollback', 'PT0009', 'TH0004', '2025-11-05', '2025-11-08', NULL, 'In progress', 'Testing rollback for incompatible firmware patch'),
('MT0010', 'Deploy', 'PT0010', 'TH0002', '2025-11-10', '2025-11-15', NULL, 'Not Started', 'Upcoming deployment of API performance enhancements');
    

CREATE TABLE feedback (
    feedbackID VARCHAR(6) NOT NULL UNIQUE PRIMARY KEY,
    testerID VARCHAR(6) NOT NULL,
    patchID VARCHAR(6) NOT NULL,
    description VARCHAR(255) NOT NULL,
    rating int NOT NULL,

    FOREIGN KEY (testerID) REFERENCES tester(testerID),
    FOREIGN KEY (patchID) REFERENCES patch(patchID)
);

INSERT INTO feedback (feedbackID, testerID, patchID, description, rating)
VALUES
('FB0001', 'TS0001', 'PT0001', 'Patch successfully resolved login security flaw.', 5),
('FB0002', 'TS0002', 'PT0002', 'Database performance noticeably improved after update.', 4),
('FB0003', 'TS0003', 'PT0003', 'Rollback fixed UI glitches effectively.', 5),
('FB0004', 'TS0004', 'PT0004', 'Performance optimization worked but caused minor cache issues.', 3),
('FB0005', 'TS0005', 'PT0005', 'Security update was stable and improved encryption.', 5),
('FB0006', 'TS0001', 'PT0006', 'Network rollback restored connectivity.', 4),
('FB0007', 'TS0002', 'PT0007', 'Analytics deployment worked but delayed data syncs.', 3),
('FB0008', 'TS0003', 'PT0008', 'Authentication overhaul works perfectly.', 5),
('FB0009', 'TS0004', 'PT0009', 'Firmware patch incompatible with old devices.', 2),
('FB0010', 'TS0005', 'PT0010', 'API optimization noticeably improved response time.', 5);







