#!/bin/bash

# Get the number of null cards and max number of cards per customer from the script arguments
num_null_cards=$1
max_cards_per_customer=$2

echo "Creating $num_null_cards cards with no user and 0-$max_cards_per_customer cards per customer..."

# Clear the file and add headers
echo "CardNum,CardExp,UserID" > Card.csv

# Get all user IDs
mapfile -t userIDs < <(tail -n +2 Customer.csv | cut -d, -f1)

# Declare an associative array to store card numbers
declare -A cardNumbers

# Initialize counter for processed cards
processed_cards=0
num_cards=0

# Calculate total number of cards
total_cards=$num_null_cards

# For each user ID
for userID in "${userIDs[@]}"
do
    # Generate a random number of cards for this user ID
    num_cards=$((RANDOM % (max_cards_per_customer+1)))

    # For each card to create
    for ((i=1; i<=$num_cards; i++))
    do
        # Generate a unique 16-digit card number
        while true; do
            # Generate the first digit in the range 1-9
            cardNum=$((RANDOM % 9 + 1))

            for i in {1..15}; do
                # Generate a random digit in the range 0-9
                digit=$((RANDOM % 10))
                # Concatenate the digit to the card number
                cardNum="${cardNum}${digit}"
            done

            if [ -z "${cardNumbers[$cardNum]}" ]; then
                cardNumbers[$cardNum]=1
                break
            fi
        done

        # Generate a random expiration date between now and 5 years in the future
        cardExp=$(date -d "$((RANDOM % 1825)) days" +%Y-%m-%d)

        # Write the row to Card.csv
        echo "$cardNum,$cardExp,$userID" >> Card.csv

        # Increment counter for processed cards
        processed_cards=$((processed_cards + 1))
        total_cards=$((total_cards + 1))

        # Calculate percentage of progress as a floating-point number and convert it to an integer
        percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_cards / $total_cards}")

        # Print percentage of progress
        printf "\rProgress: %d%%" $percent
    done
done

# For each null card to create
for ((j=1; j<=$num_null_cards; j++))
do
    # Generate a unique 16-digit card number
    while true; do
        # Generate the first digit in the range 1-9
        cardNum=$((RANDOM % 9 + 1))

        for i in {1..15}; do
            # Generate a random digit in the range 0-9
            digit=$((RANDOM % 10))
            # Concatenate the digit to the card number
            cardNum="${cardNum}${digit}"
        done

        if [ -z "${cardNumbers[$cardNum]}" ]; then
            cardNumbers[$cardNum]=1
            break
        fi
    done

    # Generate a random expiration date between now and 5 years in the future
    cardExp=$(date -d "$((RANDOM % 1825)) days" +%Y-%m-%d)

    # Write the row to Card.csv
    echo "$cardNum,$cardExp,NULL" >> Card.csv

    # Increment counter for processed cards
    processed_cards=$((processed_cards + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_cards / $total_cards}")

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done
echo -e "\nGenerated 'Card.csv' with $(( $(wc -l < Card.csv) - 1 )) rows.\n"