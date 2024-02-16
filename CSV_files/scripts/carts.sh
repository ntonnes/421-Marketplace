#!/bin/bash

per_user=$1
cps=$2

# CSV header
printf "Assigning 1-3 copies of 0-5 models to each user's cart...\n"
printf "UserID,ModelID,Copies\n" > InCart.csv

# Read User.csv into an array, excluding the header
mapfile -t users < <(tail -n +2 User.csv)

# Get total number of users
total_users=${#users[@]}

# Initialize counter for processed users
processed_users=0

# Get a shuffled list of ModelIDs
mapfile -t modelIDs < <(tail -n +2 Model.csv | shuf | cut -d',' -f1)

# For each user
for user in "${users[@]}"
do
    # Get UserID
    userID=$(echo "$user" | cut -d',' -f1)

    # Generate a random number of items (0-5)
    num_items=$((RANDOM % per_user))

    # For each item
    for ((i=0; i<$num_items; i++))
    do
        # Get a ModelID from the shuffled list
        modelID=${modelIDs[$i]}

        # Generate a random number of copies (1-3)
        copies=$((RANDOM % cps + 1))

        # Write the item to InCart.csv
        printf "%s,%s,%s\n" "$userID" "$modelID" "$copies" >> InCart.csv
    done

    # Increment counter for processed users
    processed_users=$((processed_users + 1))

    # Calculate percentage of progress
    percent=$((100 * processed_users / total_users))

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done
printf "\n"