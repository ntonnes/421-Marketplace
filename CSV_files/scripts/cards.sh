#!/bin/bash

# Check if arguments are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <number_of_null_cards> <max_number_of_cards_per_customer>"
    exit 1
fi

# Check if arguments are positive integers
if ! [[ $1 =~ ^[0-9]+$ ]] || ! [[ $2 =~ ^[0-9]+$ ]]; then
    echo "Error: Arguments must be positive integers"
    exit 1
fi

# Get the number of null cards and max number of cards per customer from the script arguments
num_null_cards=$1
max_cards_per_customer=$2

# Clear the file and add headers
echo "CardNum,CardExp,UserID" > Card.csv

# Get all user IDs
mapfile -t userIDs < <(tail -n +2 Customer.csv | cut -d, -f1)

# Declare an associative array to store card numbers
declare -A cardNumbers

# For each user ID
for userID in "${userIDs[@]}"
do
    # Generate a random number of cards for this user ID
    num_cards=$((RANDOM % (max_cards_per_customer+1)))

    # For each card to create
    for ((i=1; i<=$num_cards; i++))
    do
        # Generate a unique card number in the range 1000000000000000-9999999999999999
        while true; do
            cardNum=$((RANDOM % 8999999999999999 + 1000000000000000))
            if [ -z "${cardNumbers[$cardNum]}" ]; then
                cardNumbers[$cardNum]=1
                break
            fi
        done

        # Generate a random expiration date between now and 5 years in the future
        cardExp=$(date -d "$((RANDOM % 1825)) days" +%Y-%m-%d)

        # Write the row to Card.csv
        echo "$cardNum,$cardExp,$userID" >> Card.csv
    done
done

# For each null card to create
for ((i=1; i<=$num_null_cards; i++))
do
    # Generate a unique card number in the range 1000000000000000-9999999999999999
    while true; do
        cardNum=$((RANDOM % 8999999999999999 + 1000000000000000))
        if [ -z "${cardNumbers[$cardNum]}" ]; then
            cardNumbers[$cardNum]=1
            break
        fi
    done

    # Generate a random expiration date between now and 5 years in the future
    cardExp=$(date -d "$((RANDOM % 1825)) days" +%Y-%m-%d)

    # Write the row to Card.csv
    echo "$cardNum,$cardExp,NULL" >> Card.csv
done

echo "Generated 'Card.csv' with $(wc -l < Card.csv) rows."
echo