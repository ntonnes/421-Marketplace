#!/bin/bash

# Clear the file and add headers
echo "OrderID,DeliverAdd,Total,Date,Email,CardNum" > Order.csv

# Get all order IDs
mapfile -t orderIDs < <(tail -n +2 Product.csv | cut -d, -f4 | sort | uniq)

# Get all card numbers
mapfile -t cardNums < <(tail -n +2 Card.csv | cut -d, -f1)

counter=1

# For each order ID
for orderID in "${orderIDs[@]}"
do
    # Skip if orderID is NULL
    if [ "$orderID" = "NULL" ]; then
        continue
    fi

    # Get a random card number
    cardNum=${cardNums[$RANDOM % ${#cardNums[@]}]}

    # Get the UserID associated with the card number
    userID=$(awk -F, -v cardNum="$cardNum" '$1 == cardNum {print $3}' Card.csv)

    if [ -n "$userID" ] && [ "$userID" != "NULL" ]; then
        # If the card is associated with a UserID, use that customer's email
        email=$(awk -F, -v userID="$userID" '$1 == userID {print $4}' Customer.csv)
    else
        # Otherwise, generate an email for the order
        email="guest$counter@domain.com"
    fi

    deliverAdd="Address $counter"

    # Filter Product.csv for rows where column 4 = $orderID and save to purchased_items.csv
    awk -F, -v orderID="$orderID" '$4 == orderID' Product.csv > purchased_items.csv

    # Initialize total
    total=0

    # Read the purchased_items.csv file line by line
    while IFS=, read -r modelID serialNo return currentOrderID supplierName restockNo; do
        # Retrieve the price for this modelID from Model.csv and add it to total
        price=$(awk -F, -v modelID="$modelID" '$1 == modelID {print $2}' Model.csv)
        total=$(awk -v total="$total" -v price="$price" 'BEGIN {print total + price}')
    done < purchased_items.csv
    rm purchased_items.csv

    # Get a random number of days in the past (up to 365)
    daysAgo=$((RANDOM % 365))

    # Get a date in the past
    pastDate=$(date -d "-$daysAgo days" +%Y-%m-%d)

    # Write the row to Order.csv
    echo "$orderID,$deliverAdd,$total,$pastDate,$email,$cardNum" >> Order.csv
    counter=$((counter + 1))
done