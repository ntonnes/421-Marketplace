#!/bin/bash

# Define the messages
declare -A messages
messages=(
    [0]="I wouldn''t buy this again."
    [1]="Very dissatisfied."
    [2]="Not what I expected."
    [3]="Could be better."
    [4]="It''s okay, but not great."
    [5]="Average product."
    [6]="Slightly above average."
    [7]="I''m satisfied."
    [8]="Quite good, Would buy again."
    [9]="I''m very satisfied."
    [10]="Excellent product! I love it!"
)

total_lines=$(( $(wc -l < Purchased.csv) - 1 ))
num_rows=$((total_lines * 9 / 10))

echo "Generating reviews for 0-$num_rows products..."

mapfile -t rows < <(tail -n +2 Purchased.csv)

# Output the header
echo "UserID,ModelID,Rating,Message" > Review.csv

count=1
declare -A user_model_pairs

# For each selected row
for purchase in "${rows[@]}"; do

    if ((num_rows == 0)); then
        break
    fi

    IFS=',' read -r ModelID _ OrderID _ <<< "$purchase"

    # Find the row with the corresponding OrderID in Order.csv and retrieve the email
    Email=$(awk -F, -v OrderID="$OrderID" '$1 == OrderID {print $5}' Order.csv)

    # Find the row in Customer.csv with that email and retrieve the UserID
    UserID=$(awk -F, -v Email="$Email" '$4 == Email {print $1}' Customer.csv)

    # If UserID is NULL, skip this product
    if [[ -z $UserID ]]; then
        continue
    elif [[ -n ${user_model_pairs["$UserID,$ModelID"]} ]]; then
        continue
    fi
    user_model_pairs["$UserID,$ModelID"]=1

    # Generate a random rating between 0 and 10
    Rating=$((RANDOM % 6))

    # Select the corresponding message
    if (( count % 5 == 0 )); then
        echo "$UserID,$ModelID,$Rating,NULL" >> Review.csv
    else
        echo "$UserID,$ModelID,$Rating,\"${messages[$Rating]}\"" >> Review.csv
    fi
    num_rows=$((num_rows - 1))

done

echo -e "Generated 'Review.csv' with $(( $(wc -l < Review.csv) - 1 )) rows.\n"