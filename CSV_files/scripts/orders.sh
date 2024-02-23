#!/bin/bash

# Initialize CSV file
echo "Creating $1 orders..."
echo "OrderID,DeliverAdd,Total,Date,Email,CardNum" > Order.csv

# Get card numbers from Card.csv
mapfile -t cardNums < <(tail -n +2 Card.csv | cut -d, -f1)

declare -A usedOrderIDs
counter=1

# Generate orders
for ((i=1; i<=$1; i++))
do
    # Generate unique OrderID
    while true; do
        orderID=$(( RANDOM % 900000000 + 100000000 ))
        if [[ -z ${usedOrderIDs[$orderID]} ]]; then
            usedOrderIDs[$orderID]=1
            break
        fi
    done

    # Select random card number
    cardNum=${cardNums[$RANDOM % ${#cardNums[@]}]}

    # Get associated UserID
    userID=$(awk -F, -v cardNum="$cardNum" '$1 == cardNum {print $3}' Card.csv)

    if [ -n "$userID" ] && [ "$userID" != "NULL" ]; then
        # Use associated customer's email
        email=$(awk -F, -v userID="$userID" '$1 == userID {print $4}' Customer.csv)
    else
        # Generate email for the order
        email="guest$counter@domain.com"
    fi

    deliverAdd="Address $counter"

    # Generate random past date
    daysAgo=$((RANDOM % 365))
    pastDate=$(date -d "-$daysAgo days" +%Y-%m-%d)

    # Write order data to CSV
    echo "$orderID,$deliverAdd,0.00,$pastDate,$email,$cardNum" >> Order.csv
    counter=$((counter + 1))
done

echo -e "Generated 'Order.csv' with $(( $(wc -l < Order.csv) - 1 )) rows.\n"