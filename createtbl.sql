CONNECT TO COMP421;

CREATE TABLE Category(
    CName VARCHAR(255) NOT NULL PRIMARY KEY CHECK (REGEXP_LIKE(CName, '^[A-Za-z ]+$'))
);

CREATE TABLE BrandPage(
    URL VARCHAR(255) NOT NULL PRIMARY KEY CHECK (REGEXP_LIKE(URL, '^www\.421market\.com/.*$')),
    Name VARCHAR(255) NOT NULL, 
    Description CLOB
);

CREATE TABLE Model(
    ModelID INT NOT NULL PRIMARY KEY CHECK (ModelID BETWEEN 100000 AND 999999),
    Price DECIMAL NOT NULL CHECK (Price > 0),         
    URL VARCHAR(255) NOT NULL,
    FOREIGN KEY (URL) REFERENCES BrandPage(URL),
    Stars INT CHECK (Stars BETWEEN 0 AND 10) 
);

CREATE TABLE Belongs(
    CName VARCHAR(255) NOT NULL,
    ModelID INT NOT NULL, 
    PRIMARY KEY (CName, ModelID),
    FOREIGN KEY (CName) REFERENCES Category(CName),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

CREATE TABLE User(
    UserID INT NOT NULL PRIMARY KEY CHECK (UserID BETWEEN 100000000 AND 999999999)
);

CREATE TABLE Customer(
    UserID INT NOT NULL PRIMARY KEY,
    DOB DATE,
    Password VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Name VARCHAR(255) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

CREATE TABLE InCart(
    UserID INT NOT NULL,
    ModelID INT NOT NULL,
    Copies INT NOT NULL CHECK (Copies > 0),
    PRIMARY KEY (UserID, ModelID),
    FOREIGN KEY (UserID) REFERENCES User(UserID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

CREATE TABLE Review(
    UserID INT NOT NULL,
    ModelID INT NOT NULL,
    Rating INT NOT NULL CHECK (Rating BETWEEN 0 AND 10),
    Message CLOB,
    PRIMARY KEY (UserID, ModelID),
    FOREIGN KEY (UserID) REFERENCES Customer(UserID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

CREATE TABLE Member(
    UserID INT NOT NULL PRIMARY KEY,
    ExpDate DATE NOT NULL,
    Points INT NOT NULL CHECK (Points >= 0),
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);

CREATE TABLE Coupon(
    UserID INT NOT NULL,
    ModelID INT NOT NULL,
    Discount DECIMAL NOT NULL CHECK (Discount >= 0 AND Discount < 1),
    PointCost INT NOT NULL CHECK (PointCost >= 0),
    Expiration DATE NOT NULL,
    PRIMARY KEY (UserID, ModelID),
    FOREIGN KEY (UserID) REFERENCES Member(UserID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

CREATE TABLE Admin(
    UserID INT NOT NULL PRIMARY KEY,
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

CREATE TABLE Manages(
    UserID INT NOT NULL,
    URL VARCHAR(255) NOT NULL,
    Since DATE NOT NULL,
    ClearanceFlag INT NOT NULL CHECK (ClearanceFlag > 0 AND ClearanceFlag <= 5),
    PRIMARY KEY (UserID, URL),
    FOREIGN KEY (UserID) REFERENCES Admin(UserID),
    FOREIGN KEY (URL) REFERENCES BrandPage(URL)
);

CREATE TABLE Card(
    CardNum BIGINT NOT NULL PRIMARY KEY CHECK (CardNum >= 1000000000000000 AND CardNum <= 9999999999999999),
    CardExp DATE NOT NULL,
    UserID INT,
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);

CREATE TABLE Restock(
    SupplierName VARCHAR(255) NOT NULL,
    RestockNo INT NOT NULL,
    Location VARCHAR(255) NOT NULL,
    Date DATE NOT NULL,
    PRIMARY KEY (SupplierName, RestockNo)
);

CREATE TABLE Order(
    OrderID INT NOT NULL PRIMARY KEY CHECK (OrderID >= 100000000 AND OrderID <= 999999999),
    DeliverAdd VARCHAR(255) NOT NULL,
    Total DECIMAL NOT NULL CHECK (Total >= 0),
    Date DATE NOT NULL,
    Email VARCHAR(255) NOT NULL,
    CardNum BIGINT NOT NULL,
    FOREIGN KEY (CardNum) REFERENCES Card(CardNum)
);

CREATE TABLE Product(
    ModelID INT NOT NULL,
    SerialNo INT NOT NULL CHECK (SerialNo >= 100000000 AND SerialNo <= 999999999),
    Return SMALLINT,
    OrderID INT,
    SupplierName VARCHAR(255) NOT NULL,
    RestockNo INT NOT NULL,
    PRIMARY KEY (SerialNo, ModelID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID),
    FOREIGN KEY (OrderID) REFERENCES Order(OrderID),
    FOREIGN KEY (SupplierName, RestockNo) REFERENCES Restock(SupplierName, RestockNo)
);

CREATE TABLE Shipment(
    ShipmentNo INT NOT NULL CHECK (ShipmentNo >= 100000 AND ShipmentNo <= 999999),
    ShipperName VARCHAR(255) NOT NULL,
    OrderID INT NOT NULL,
    ModelID INT NOT NULL,
    SerialNo INT NOT NULL,
    DeliverDate DATE NOT NULL,
    ShipDate DATE NOT NULL,
    PRIMARY KEY (ShipmentNo, ShipperName, OrderID, ModelID, SerialNo),
    FOREIGN KEY (OrderID) REFERENCES Order(OrderID),
    FOREIGN KEY (ModelID, SerialNo) REFERENCES Product(ModelID, SerialNo)
);
