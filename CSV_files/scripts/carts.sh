#!/bin/bash

per_user=$1
cps=$2

# CSV header
printf "Assigning 1-$cps copies of 0-$per_user models to each user's cart...\n"
printf "UserID,ModelID,Copies\n" > InCart.csv

# Read User.csv into an array, excluding the header
mapfile -t users < <(tail -n +2 User.csv)

# Get total number of users
total_users=${#users[@]}

# Initialize counter for processed users
processed_users=0

# Get a shuffled list of ModelIDs
mapfile -t modelIDs < <(tail -n +2 Model.csv | shuf | cut -d',' -f1)

j=0
next_model () {
    if (( j < ${#modelIDs[@]} - 1 )); then
        j=$((j + 1))
    else
        readarray -t modelIDs < <(printf '%s\n' "${modelIDs[@]}" | shuf)
        j=0
    fi
}

# For each user
for user in "${users[@]}"
do
    # Get UserID
    userID=$(echo "$user" | cut -d',' -f1)

    # Generate a random number of items (0-per_user)
    num_items=$((RANDOM % per_user))

    # For each item
    for ((i=0; i<$num_items; i++))
    do
        # Get a ModelID from the shuffled list
        next_model
        modelID=${modelIDs[$j]}

        # Generate a random number of copies (1-cps)
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