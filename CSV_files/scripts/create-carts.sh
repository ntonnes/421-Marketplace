#!/bin/bash

# CSV header
echo "Assigning 0-5 InCart relationships with 1-3 copies per User..."
echo "UserID,ModelID,Copies" > InCart.csv

# Read User.csv into an array, excluding the header
mapfile -t users < <(tail -n +2 User.csv)

# For each user
for user in "${users[@]}"
do
    # Get UserID
    userID=$(echo "$user" | cut -d',' -f1)

    # Generate a random number of items (0-5)
    num_items=$((RANDOM % 6))

    # For each item
    for ((i=0; i<$num_items; i++))
    do
        # Get a random model from Model.csv, excluding the header
        modelID=$(tail -n +2 Model.csv | shuf -n 1 | cut -d',' -f1)

        # Generate a random number of copies (1-3)
        copies=$((RANDOM % 3 + 1))

        # Write the item to InCart.csv
        echo "$userID,$modelID,$copies" >> InCart.csv
    done
done

# Print success message
echo "Generated 'InCart.csv' with $(wc -l < InCart.csv) rows."