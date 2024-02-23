#!/bin/bash

per_user=$1
cps=$2

# Initialize CSV file
printf "Assigning 1-$cps copies of 0-$per_user models to each user's cart...\n"
printf "UserID,ModelID,Copies\n" > InCart.csv

# Get user data from User.csv
mapfile -t users < <(tail -n +2 User.csv)

total_users=${#users[@]}
processed_users=0

# Get shuffled list of ModelIDs from Model.csv
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

# Assign models to users' carts
for user in "${users[@]}"
do
    userID=$(echo "$user" | cut -d',' -f1)
    num_items=$((RANDOM % per_user))

    for ((i=0; i<$num_items; i++))
    do
        # Get a unique ModelID for the user
        next_model
        while grep -q "^$userID,${modelIDs[$j]}" InCart.csv; do
            next_model
        done
        modelID=${modelIDs[$j]}

        # Generate random number of copies
        copies=$((RANDOM % cps + 1))

        # Write data to CSV
        printf "%s,%s,%s\n" "$userID" "$modelID" "$copies" >> InCart.csv
    done

    # Print progress
    processed_users=$((processed_users + 1))
    percent=$((100 * processed_users / total_users))
    printf "\rProgress: %d%%" $percent
done

echo -e "\nGenerated 'InCart.csv' with $(( $(wc -l < InCart.csv) - 1 )) rows.\n"