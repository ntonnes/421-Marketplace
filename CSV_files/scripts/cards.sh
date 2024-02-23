#!/bin/bash

num_null_cards=$1
max_cards_per_customer=$2

echo "Creating $num_null_cards cards with no user and 0-$max_cards_per_customer cards per customer..."

# Initialize CSV file
echo "CardNum,CardExp,UserID" > Card.csv

# Get user IDs from Customer.csv
mapfile -t userIDs < <(tail -n +2 Customer.csv | cut -d, -f1)

declare -A cardNumbers

processed_cards=0
num_cards=0
total_cards=$num_null_cards

# Assign cards to users
for userID in "${userIDs[@]}"
do
    num_cards=$((RANDOM % (max_cards_per_customer+1)))

    for ((i=1; i<=$num_cards; i++))
    do
        # Generate unique card number
        while true; do
            cardNum=$((RANDOM % 9 + 1))

            for i in {1..15}; do
                digit=$((RANDOM % 10))
                cardNum="${cardNum}${digit}"
            done

            if [ -z "${cardNumbers[$cardNum]}" ]; then
                cardNumbers[$cardNum]=1
                break
            fi
        done

        # Generate card expiration date
        cardExp=$(date -d "$((RANDOM % 1825)) days" +%Y-%m-%d)

        # Write card data to CSV
        echo "$cardNum,$cardExp,$userID" >> Card.csv

        processed_cards=$((processed_cards + 1))
        total_cards=$((total_cards + 1))

        # Print progress
        percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_cards / $total_cards}")
        printf "\rProgress: %d%%" $percent
    done
done

# Generate null cards
for ((j=1; j<=$num_null_cards; j++))
do
    # Generate unique card number
    while true; do
        cardNum=$((RANDOM % 9 + 1))

        for i in {1..15}; do
            digit=$((RANDOM % 10))
            cardNum="${cardNum}${digit}"
        done

        if [ -z "${cardNumbers[$cardNum]}" ]; then
            cardNumbers[$cardNum]=1
            break
        fi
    done

    # Generate card expiration date
    cardExp=$(date -d "$((RANDOM % 1825)) days" +%Y-%m-%d)

    # Write card data to CSV
    echo "$cardNum,$cardExp,NULL" >> Card.csv

    # Print progress
    processed_cards=$((processed_cards + 1))
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_cards / $total_cards}")
    printf "\rProgress: %d%%" $percent
done

echo -e "\nGenerated 'Card.csv' with $(( $(wc -l < Card.csv) - 1 )) rows.\n"