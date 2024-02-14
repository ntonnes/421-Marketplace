CONNECT TO COMP421;

CREATE TABLE Category(
    CName VARCHAR(255) PRIMARY KEY CONSTRAINT letters CHECK (CName ~ '^[A-Za-z ]+$')
);

CREATE TABLE BrandPage(
    URL VARCHAR(255) PRIMARY KEY CONSTRAINT domain CHECK (URL LIKE 'www.421market.com/%'),
    Name VARCHAR(255) NOT NULL, 
    Description TEXT
);

CREATE TABLE Model(
    ModelID INT PRIMARY KEY CONSTRAINT rangeModel CHECK (ModelID >= 100000 AND ModelID <= 999999),
    Price DECIMAL NOT NULL CONSTRAINT rangePrice CHECK (Price > 0),         
    URL VARCHAR(255) NOT NULL,
    FOREIGN KEY (URL) REFERENCES BrandPage(URL),
    Stars INT CONSTRAINT star CHECK (Stars >= 0 AND Stars <= 10) 
);

CREATE TABLE Belongs(
    CName VARCHAR(255),
    ModelID INT, 
    PRIMARY KEY (CName, ModelID),
    FOREIGN KEY (CName) REFERENCES Category(CName),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

CREATE TABLE User(
    UserID INT PRIMARY KEY CONSTRAINT rangeUser CHECK (UserID >= 100000000 AND UserID <= 999999999)
);

CREATE TABLE Customer(
    UserID INT PRIMARY KEY,
    DOB DATE,
    Password VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Name VARCHAR(255) NOT NULL CONSTRAINT letters CHECK (Name ~ '^[A-Za-z ]+$'),
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

CREATE TABLE InCart(
    UserID INT,#!/bin/bash
    ModelID INT,
    Copies INT NOT NULL CONSTRAINT rangeCopies CHECK (Copies > 0),
    PRIMARY KEY (UserID, ModelID),
    FOREIGN KEY (UserID) REFERENCES Customer(UserID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

CREATE TABLE Review(
    UserID INT,
    ModelID INT,
    Rating INT NOT NULL CONSTRAINT rangeRating CHECK (Rating >= 0 AND Rating <= 10),
    Message TEXT,
    PRIMARY KEY (UserID, ModelID),
    FOREIGN KEY (UserID) REFERENCES Customer(UserID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

CREATE TABLE Member(
    UserID INT PRIMARY KEY,
    ExpDate DATE NOT NULL,
    Points INT NOT NULL CONSTRAINT rangePoints CHECK (Points >= 0),
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);

CREATE TABLE Coupon(
    UserID INT,
    ModelID INT,
    Discount DECIMAL NOT NULL CONSTRAINT rangeDiscount CHECK (Discount >= 0 AND Discount < 1),
    PointCost INT NOT NULL CONSTRAINT rangePointCost CHECK (PointCost >= 0),
    Expiration DATE NOT NULL,
    PRIMARY KEY (UserID, ModelID),
    FOREIGN KEY (UserID) REFERENCES Member(UserID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

CREATE TABLE Admin(
    UserID INT PRIMARY KEY,
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);

CREATE TABLE Manages(
    UserID INT,
    URL VARCHAR(255),
    Since DATE NOT NULL,
    ClearanceFlag INT NOT NULL CONSTRAINT rangeClearance CHECK (ClearanceFlag > 0 AND ClearanceFlag <= 5),
    PRIMARY KEY (UserID, URL),
    FOREIGN KEY (UserID) REFERENCES Admin(UserID),
    FOREIGN KEY (URL) REFERENCES BrandPage(URL)
);

CREATE TABLE Card(
    CardNum INT PRIMARY KEY CONSTRAINT rangeCard CHECK (CardNum >= 1000000000000000 AND CardNum <= 9999999999999999),
    CardExp DATE NOT NULL,
    UserID INT,
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);

CREATE TABLE Restock(
    SupplierName VARCHAR(255),
    RestockNo INT,
    Location VARCHAR(255) NOT NULL,
    Date DATE NOT NULL,
    PRIMARY KEY (SupplierName, RestockNo)
);

CREATE TABLE Order(
    OrderID INT PRIMARY KEY CONSTRAINT rangeOrder CHECK (OrderID >= 100000000 AND OrderID <= 999999999),
    DeliverAdd VARCHAR(255) NOT NULL,
    Total DECIMAL NOT NULL CONSTRAINT rangeTotal CHECK (Total > 0),
    Date DATE NOT NULL,
    Email VARCHAR(255) NOT NULL,
    CardNum INT NOT NULL,
    FOREIGN KEY (CardNum) REFERENCES Card(CardNum)
);

CREATE TABLE Product(
    ModelID INT,
    SerialNo INT CONSTRAINT rangeSerial CHECK (SerialNo >= 100000000 AND SerialNo <= 999999999),
    Return BOOLEAN,
    OrderID INT,
    SupplierName VARCHAR(255) NOT NULL,
    RestockNo INT NOT NULL,
    PRIMARY KEY (SerialNo, ModelID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID),
    FOREIGN KEY (OrderID) REFERENCES Order(OrderID),
    FOREIGN KEY (SupplierName, RestockNo) REFERENCES Restock(SupplierName, RestockNo)
);

CREATE TABLE Shipment(
    ShipmentNo INT CONSTRAINT rangeShipment CHECK (ShipmentNo >= 100000 AND ShipmentNo <= 999999),
    ShipperName VARCHAR(255),
    OrderID INT,
    ModelID INT,
    SerialNo INT,
    DeliverDate DATE NOT NULL,
    ShipDate DATE NOT NULL,
    PRIMARY KEY (ShipmentNo, ShipperName, OrderID, ModelID, SerialNo),
    FOREIGN KEY (OrderID) REFERENCES Order(OrderID),
    FOREIGN KEY (ModelID, SerialNo) REFERENCES Product(ModelID, SerialNo)
);