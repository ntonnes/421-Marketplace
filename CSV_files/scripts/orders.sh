#!/bin/bash

# Check if the number of orders to create was provided
if [ $# -eq 0 ]
then
    echo "Please provide the number of orders to create as a command line argument."
    exit 1
fi

# CSV header
echo "OrderID,DeliverAdd,Total,Date,Email,CardNum" > Order.csv

# Read Card.csv into an array, excluding the header
mapfile -t cards < <(tail -n +2 Card.csv | shuf)

# Get total number of cards
total_cards=${#cards[@]}

# Initialize counter for created orders
created_orders=0

# For each order to create
for ((i=1; i<=$1; i++))
do
    # Generate a unique OrderID
    orderID=$((100000000 + i))

    # Generate a unique DeliverAdd
    deliverAdd="Address $i"

    # Initialize Total to 0
    total=0

    # Generate a random Date in the past
    date=$(date -d "$((RANDOM % 365)) days ago" +%Y-%m-%d)

    # Generate a unique Email
    email="Email$i@example.com"

    # Get a CardNum from the shuffled list
    cardNum=$(echo "${cards[$((i % total_cards))]}" | cut -d',' -f1)

    # Write the order to Order.csv
    echo "$orderID,$deliverAdd,$total,$date,$email,$cardNum" >> Order.csv

    # Increment counter for created orders
    created_orders=$((created_orders + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $created_orders / $1}")

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done

printf "\n"