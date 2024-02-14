-- Category(*CName)
-- could add a CHECK to ensure the category consists of letters only
CREATE TABLE Category(
    CName VARCHAR(255) PRIMARY KEY --CONSTRAINT letters CHECK (CName ~ '^[A-Za-z ]+$')
);

-- BrandPage(*URL, Name NOT NULL, Description)
-- could add a CHECK to ensure URL is within our site's domain
-- Name in this case should allow for special characters and numbers, no need for a CHECK
CREATE TABLE BrandPage(
    URL VARCHAR(255) PRIMARY KEY, --CONSTRAINT domain CHECK (URL LIKE 'www.421market.com/%')
    Name VARCHAR(255) NOT NULL, 
    Description TEXT
);

-- Model(*ModelID, Price NOT NULL, URL References BrandPage NOT NULL, Stars)
-- could add a CHECK to ensure ModelID is a positive 6 digit integer
-- could add a CHECK to ensure Price is a positive decimal
CREATE TABLE Model(
    ModelID INT PRIMARY KEY, --CONSTRAINT rangeModel CHECK (ModelID >= 100000 AND ModelID <= 999999)
    Price DECIMAL NOT NULL, --CONSTRAINT rangePrice CHECK (Price > 0)         
    URL VARCHAR(255) NOT NULL,
    FOREIGN KEY (URL) REFERENCES BrandPage(URL),
    Stars INT CONSTRAINT star CHECK (Stars >= 0 AND Stars <= 10) 
);


-- Belongs(*CName References Category, *ModelID References Model)
CREATE TABLE Belongs(
    CName VARCHAR(255),
    ModelID INT, 
    PRIMARY KEY (CName, ModelID),
    FOREIGN KEY (CName) REFERENCES Category(CName),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);


-- User(*UserID)
-- could add a CHECK to ensure UserID is a positive 9 digit integer
CREATE TABLE User(
    UserID INT PRIMARY KEY --CONSTRAINT rangeUser CHECK (UserID >= 100000000 AND UserID <= 999999999)
);

-- Customer(*UserID References User, DOB, Password NOT NULL, Email NOT NULL, Name NOT NULL)
-- could add a CHECK to ensure Name consists of letters only
-- could add a UNIQUE constraint on Email prevent creating multiple accounts with one email
CREATE TABLE Customer(
    UserID INT PRIMARY KEY,
    DOB DATE,
    Password VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL, --UNIQUE
    Name VARCHAR(255) NOT NULL, --CONSTRAINT letters CHECK (CName ~ '^[A-Za-z ]+$')
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

-- InCart(*UserID References User, *ModelID References Model, Copies NOT NULL)
-- could add a CHECK to ensure Copies is a positive integer
CREATE TABLE InCart(
    UserID INT,
    ModelID INT,
    Copies INT NOT NULL, --CONSTRAINT rangeCopies CHECK (Copies > 0)
    PRIMARY KEY (UserID, ModelID),
    FOREIGN KEY (UserID) REFERENCES Customer(UserID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

-- Review(*UserID References Customer, *ModelID References Model, Rating NOT NULL, Message)
CREATE TABLE Review(
    UserID INT,
    ModelID INT,
    Rating INT NOT NULL CONSTRAINT rangeRating CHECK (Rating >= 0 AND Rating <= 10),
    Message TEXT,
    PRIMARY KEY (UserID, ModelID),
    FOREIGN KEY (UserID) REFERENCES Customer(UserID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

-- Member(*UserID References Customer, ExpDate NOT NULL, Points NOT NULL)
-- could add a CHECK to ensure Points is a positive integer (see below)
CREATE TABLE Member(
    UserID INT PRIMARY KEY,
    ExpDate DATE NOT NULL,
    Points INT NOT NULL, --CONSTRAINT rangePoints CHECK (Points > 0)
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);

-- Coupon(*UserID References Member, *ModelID References Model, Discount NOT NULL, PointCost NOT NULL, Expiration NOT NULL)
-- could add a CHECK to ensure Discount is a positive decimal between 0 (inclusive since 0 means free) and 1 (exclusive since 1 original price), 
--      so the discounted price is easily calculated with (Price)*(Discount)
-- could add a CHECK to ensure PointCost is a positive integer or 0 (see below)
CREATE TABLE Coupon(
    UserID INT,
    ModelID INT,
    Discount DECIMAL NOT NULL, --CONSTRAINT rangeDiscount CHECK (Rating >= 0 AND Rating < 1),
    PointCost INT NOT NULL, --CONSTRAINT rangePointCost CHECK (PointCost >= 0),
    Expiration DATE NOT NULL,
    PRIMARY KEY (UserID, ModelID),
    FOREIGN KEY (UserID) REFERENCES Member(UserID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID)
);

-- Admin(*UserID References Customer)
CREATE TABLE Admin(
    UserID INT PRIMARY KEY,
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);

-- Manages(*UserID References Admin, *URL References BrandPage, Since NOT NULL, ClearanceFlag NOT NULL)
CREATE TABLE Manages(
    UserID INT,
    URL VARCHAR(255),
    Since DATE NOT NULL,
    ClearanceFlag INT NOT NULL,
    PRIMARY KEY (UserID, URL),
    FOREIGN KEY (UserID) REFERENCES Admin(UserID),
    FOREIGN KEY (URL) REFERENCES BrandPage(URL)
);

-- Card(*CardNum, CardExp NOT NULL, UserID References Customer)
-- could add a CHECK to ensure CardNum is a positive 16 digit integer
CREATE TABLE Card(
    CardNum INT PRIMARY KEY, --CONSTRAINT rangeCard CHECK (CardNum >= 1000000000000000 AND CardNum <= 9999999999999999)
    CardExp DATE NOT NULL,
    UserID INT,
    FOREIGN KEY (UserID) REFERENCES Customer(UserID)
);

-- Restock(*SupplierName, *RestockNo, Location NOT NULL, Date NOT NULL)
CREATE TABLE Restock(
    SupplierName VARCHAR(255),
    RestockNo INT,
    Location VARCHAR(255) NOT NULL,
    Date DATE NOT NULL,
    PRIMARY KEY (SupplierName, RestockNo)
);

-- Order(*OrderID, DeliverAdd NOT NULL, Total NOT NULL, Date NOT NULL, Email NOT NULL, CardNum References Card NOT NULL)
-- could add a CHECK to ensure OrderID is a positive 9 digit integer
-- could add a CHECK to ensure Total is a positive decimal
CREATE TABLE Order(
    OrderID INT PRIMARY KEY, --CONSTRAINT rangeOrder CHECK (OrderID >= 100000000 AND OrderID <= 999999999)
    DeliverAdd VARCHAR(255) NOT NULL,
    Total DECIMAL NOT NULL, --CONSTRAINT rangeTotal CHECK (Total > 0)
    Date DATE NOT NULL,
    Email VARCHAR(255) NOT NULL,
    CardNum INT NOT NULL,
    FOREIGN KEY (CardNum) REFERENCES Card(CardNum)
);

-- Product(*ModelID References Model, *SerialNo, Return, OrderID References Order, (SupplierName, RestockNo) References Restock NOT NULL)
-- could add a CHECK to ensure SerialNo is a positive 9 digit integer
CREATE TABLE Product(
    ModelID INT,
    SerialNo INT, --CONSTRAINT rangeSerial CHECK (SerialNo >= 100000000 AND SerialNo <= 999999999)
    Return BOOLEAN,
    OrderID INT,
    SupplierName VARCHAR(255) NOT NULL,
    RestockNo INT NOT NULL,
    PRIMARY KEY (SerialNo, ModelID),
    FOREIGN KEY (ModelID) REFERENCES Model(ModelID),
    FOREIGN KEY (OrderID) REFERENCES Order(OrderID),
    FOREIGN KEY (SupplierName, RestockNo) REFERENCES Restock(SupplierName, RestockNo)
);

-- Shipment(*ShipmentNo, *ShipperName, *OrderID References Order, *(ModelID, SerialNo) References Product, DeliverDate NOT NULL, ShipDate NOT NULL)
-- I'm not sure if this fully captures what we want it to capture
-- could add a CHECK to ensure ShipmentNo is a positive 6 digit integer
CREATE TABLE Shipment(
    ShipmentNo INT, --CONSTRAINT rangeShipment CHECK (ShipmentNo >= 100000 AND ShipmentNo <= 999999)
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