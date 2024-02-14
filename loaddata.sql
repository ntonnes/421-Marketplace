-- Category
INSERT INTO Category (Cname) VALUES 
('Electronics'),
('Clothing'),
('Food'),
('Home Appliances'),
('Sports');

-- Model
INSERT INTO Model (ModelID, Price, URL, Stars) VALUES 
(1, 101.00, 'www.website/nike', 8),
(2, 45.00, 'www.website/apple', 7),
(3, 390.22, 'www.website/nespresso', 9),
(4, 5.06, 'www.website/lays', 6),
(5, 599.99, 'www.website/decathlon', 10);

-- Belongs
INSERT INTO Belongs (CName, ModelID) VALUES 
('Electronics', 2),
('Clothing', 1),
('Food', 4),
('Home Appliances', 3),
('Sports', 5);

-- User
INSERT INTO User VALUES 
(1),
(2),
(3),
(4),
(5);

-- Customer
INSERT INTO Customer (UserID, DOB, Email, Name) VALUES 
(1, '2000-01-01', 'password1', 'john.doe@gmail.com', 'John Doe'),
(2, '2001-02-02', 'password2', 'jane.doe@yahoo.com', 'Jane Doe'),
(3, '2002-03-03', 'password3', 'bob.smith@gmail.com', 'Bob Smith'),
(4, '2003-04-04', 'password4', 'alice.johnson@dell.com', 'Alice Johnson'),
(5, '2004-05-05', 'password5', 'charlie.brown@mcgill.ca', 'Charlie Brown');

-- InCart
INSERT INTO InCart (UserID, ModelID, Copies) VALUES 
(1, 1, 2),
(1, 4, 1),
(1, 3, 2),
(2, 2, 1),
(3, 3, 3),
(4, 4, 4),
(5, 5, 5);

-- Review
INSERT INTO Review (UserID, ModelID, Rating, Message) VALUES 
(1, 1, 8, 'Great product!'),
(2, 2, 7, 'Good value for money.'),
(3, 3, 9, 'Excellent quality!'),
(4, 4, 6, NULL),
(5, 5, 10, 'Perfect!');

-- Member
INSERT INTO Member (UserID, ExpDate, Points) VALUES 
(1, '2023-01-01', 100),
(2, '2023-02-02', 200),
(3, '2023-03-03', 300),
(4, '2023-04-04', 400),
(5, '2023-05-05', 500);

-- Coupon
INSERT INTO Coupon (UserID, ModelID, Discount, PointCost, Expiration) VALUES 
(1, 1, 0.10, 50, '2024-12-31'),
(2, 2, 0.20, 100, '2024-11-30'),
(3, 3, 0.30, 150, '2024-10-31'),
(4, 4, 0.40, 200, '2024-09-30'),
(5, 5, 0.50, 250, '2024-08-31');

-- Admin
INSERT INTO Admin (UserID) VALUES 
(6),
(7),
(8),
(9),
(10);

-- BrandPage
INSERT INTO BrandPage (URL, Name, Description) VALUES 
('url1', 'Nike', 'Leading manufacturer of sports equipment and apparel'),
('url2', 'Apple', 'Innovative technology and home of the iPhone, iPad, and Mac'),
('url3', 'Nespresso', 'Premium coffee capsules offering a variety of flavors and intensities'),
('url4', 'Lays', 'Potato chips in a wide range of flavors'),
('url5', 'Decathlon', 'Worldwide retailer of sports equipment for over 80 different sports');

-- Manages
INSERT INTO Manages (UserID, URL, Since, ClearanceFlag) VALUES 
(6, 'www.website/nike', '2021-01-01', 1),
(7, 'www.website/apple', '2021-02-02', 5),
(8, 'www.website/nespresso', '2021-03-03', 3),
(9, 'www.website/lays', '2021-04-04', 2),
(10, 'www.website/decathlon', '2021-05-05', 1);

-- Card
INSERT INTO Card (CardNum, CardExp, UserID) VALUES 
(1234567890, '2025-01-01', 1),
(2345678901, '2025-02-02', 2),
(3456789012, '2025-03-03', 3),
(4567890123, '2025-04-04', 4),
(5678901234, '2025-05-05', 5);
(9235710243, '2025-04-03', NULL);

-- Product
INSERT INTO Product (ModelID, SerialNo, Return, OrderID, SupplierName, RestockNo) VALUES 
(1, 1001, FALSE, 1, 'Alkadri Trading Company', 1),
(2, 2002, FALSE, 2, 'Tokyo Components', 2),
(3, 3003, FALSE, 3, 'Nespresso Headquarters Toronto', 3),
(4, 4004, TRUE, 4, 'Dutch Valley Food', 4),
(5, 5005, FALSE, 5, 'Tydan Blades', 5);

-- Restock
INSERT INTO Restock (SupplierName, RestockNo, Location, Date) VALUES 
('Alkadri Trading Company', 1, 'Jeddah, Saudi Arabia', '2021-01-01'),
('Tokyo Components', 2, 'Tokyo, Japan', '2021-02-02'),
('Nespresso Headquarters Toronto', 3, 'Toronto, ON, Canada', '2021-03-03'),
('Dutch Valley Food', 4, 'Myerstown, PA, USA', '2021-04-04'),
('Tydan Blades', 5, 'London, ON, Canada', '2021-05-05');

-- Order
INSERT INTO Order VALUES 
(1, 'Address1', 100.00, '2021-01-01', 'john.doe@gmail.com', 1234567890),
(2, 'Address2', 200.00, '2021-02-02', 'jane.doe@yahoo.com', 2345678901),
(3, 'Address3', 300.00, '2021-03-03', 'bob.smith@gmail.com', 3456789012),
(4, 'Address4', 400.00, '2021-04-04', 'alice.johnson@dell.com', 4567890123),
(5, 'Address5', 500.00, '2021-05-05', 'charlie.brown@mcgill.ca', 5678901234);
(6, 'Address6', 20.00, '2021-06-06', NULL, 9235710243);


(1, '2000-01-01', 'password1', 'john.doe@gmail.com', 'John Doe'),
(2, '2001-02-02', 'password2', 'jane.doe@yahoo.com', 'Jane Doe'),
(3, '2002-03-03', 'password3', 'bob.smith@gmail.com', 'Bob Smith'),
(4, '2003-04-04', 'password4', 'alice.johnson@dell.com', 'Alice Johnson'),
(5, '2004-05-05', 'password5', 'charlie.brown@mcgill.ca', 'Charlie Brown');

-- Shipment
INSERT INTO Shipment VALUES 
(1, 'Shipper1', 1, 1, 1001, '2021-02-01', '2021-01-01'),
(2, 'Shipper2', 2, 2, 2002, '2021-03-02', '2021-02-02'),
(3, 'Shipper3', 3, 3, 3003, '2021-04-03', '2021-03-03'),
(4, 'Shipper4', 4, 4, 4004, '2021-05-04', '2021-04-04'),
(5, 'Shipper5', 5, 5, 5005, '2021-06-05', '2021-05-05');