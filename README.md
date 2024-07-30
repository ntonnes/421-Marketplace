# Web Marketplace

This application is an offline simulator of a storefront-hosting website, inspired by websites like Amazon, Ebay, and Etsy. 
Originally an academic command-line project, I expanded the application to gain experience working with a full stack.
The simulator utilizes a DB2 database in the backend to store information, while the application itself is written in java.
The contents of the GUI are updated according to the results of queries that retrieve information dynamically from the database.


## Table of Contents

- [Features](#features)
- [Entity-Relationship Diagram](#entity-relationship-diagram)
- [Relational Schema](#relational-schema)
- [Sample Images](#sample-images)


## Features
Below is a brief introduction to some of the features of the simulator:

### Customer Accounts
When the application is opened, a unique userID is randomly generated this is necessary to track the guest while they use the app.
When the user chooses the Login button, They are taken to a form where they can submit their email and password. 
If a match is found, the guest userID is deleted, and the logged-in customer information is stored locally. 
If there is an error, an appropriate error message is displayed.
Once a user is logged in, the Login and Sign Up buttons are removed from the banner and replaced with a personalized message and a Logout button. 
The Logout button removes the user information from the local variables, then creates a new guest user just as it does at launch.
The Sign Up button opens a form to submit the required information to create a Customer. 
The fields are validated and the appropriate error messages are shown if needed.

### Parameter Search
The results of a database query is used to dynamically populate the search page in the GUI.
Once a search is complete, the results are stored locally as a string list in order to display the information in the GUI and allow users to select models. 
This data is used to pass the modelID of the selected row to a new sql statement if the user adds the model to their cart.

### Leaving Reviews
When the user selects the option to leave a review, a query searches for all unique modelIDs that were part of an order with the currently logged-in user’s email.
The query additionally removes any models that have already been reviewed by the user according to the Review table.
The customer is shown a list of the results of the query, and is allowed to select one. 
The user is then taken to a screen where they can enter a rating and an optional message.
Reviews are utilized in the parameter search to enable customers to search by average rating.

### User Carts
Both guests and customers can add items to their cart through the search page. 
If a guest user closes the application or logs into an account, all of the InCart relationships for that guest userID are deleted.
From the cart menu, users can remove items from their cart. 
After any cart action, the GUI is refreshed immediately by performing a new query of the InCart table.

### Placing Orders
From the cart screen, users can place an order. When they do, they are asked to provide an email address, card details, and a delivery address.
If the user is a customer, they are provided an option to use a previously saved card which is AES encrypted in the database.
When the user’s order is submitted several procedures occur. 
First, a preliminary procedure checks that there are enough non-purchased products to fulfill this order.
Then, the card information is validated, and an orderID is generated.
The order is then assigned a shipper and the shipment details are generated.
For each item in the user’s cart, its attributes are updated to reflect the completion of the order.
Because stock availability was checked before updating the database, this process should not throw an out of stock error, which would leave the garbage insertions in the database.


## Entity-Relationship Diagram

![WebstoreER-1](https://github.com/user-attachments/assets/06a18a9d-7303-4328-bebd-04b172c4756f)

The entity-relationship diagram of the backend DB2 database, which stores information about users, stores, and products and assiciated entities.


## Relational Schema

![image](https://github.com/user-attachments/assets/b7352350-760c-470c-9023-4d6b8f445770)

The relational schena of the backend DB2 database, which stores information about users, stores, and products and assiciated entities.


## Sample Images

![unnamed](https://github.com/user-attachments/assets/e3d08b2d-bfb6-4cdf-8b11-4717d58ba2ee)

Login page


![unnamed](https://github.com/user-attachments/assets/b2cb9c88-5672-48d9-8adc-5f01f2135c42)
![unnamed](https://github.com/user-attachments/assets/cf412495-c2e6-4b83-a470-8951936f3e52)

Parameter search and results


![unnamed](https://github.com/user-attachments/assets/19d46c87-a37d-432e-b444-56c68d8be0d8)

Leaving reviews


![unnamed](https://github.com/user-attachments/assets/4f5db1c5-1806-456d-a1cf-933a7328d9e9)
![unnamed](https://github.com/user-attachments/assets/01bb7f9d-686f-4079-9700-635e1a2c0259)

Cart functionality





