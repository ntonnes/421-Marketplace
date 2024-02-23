#!/bin/bash

# Clear the file and add headers
echo "Creating $1 orders..."
echo "OrderID,DeliverAdd,Total,Date,Email,CardNum" > Order.csv

# Get all card numbers
mapfile -t cardNums < <(tail -n +2 Card.csv | cut -d, -f1)
counter=1

# Create an associative array to store used OrderIDs
declare -A usedOrderIDs

# For each order ID
for ((i=1; i<=$1; i++)) # replace 100 with the number of orders you want to generate
do
    # Generate a unique 9-digit OrderID
    while true; do
        orderID=$(( RANDOM % 900000000 + 100000000 )) # generate a random 9-digit number
        if [[ -z ${usedOrderIDs[$orderID]} ]]; then
            usedOrderIDs[$orderID]=1
            break
        fi
    done

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

    # Get a random number of days in the past (up to 365)
    daysAgo=$((RANDOM % 365))

    # Get a date in the past
    pastDate=$(date -d "-$daysAgo days" +%Y-%m-%d)

    # Write the row to Order.csv
    echo "$orderID,$deliverAdd,0.00,$pastDate,$email,$cardNum" >> Order.csv
    counter=$((counter + 1))
done
echo -e "Generated 'Order.csv' with $(( $(wc -l < Order.csv) - 1 )) rows.\n"