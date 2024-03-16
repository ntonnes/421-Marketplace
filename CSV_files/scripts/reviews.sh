#!/bin/bash

# Define the messages for each rating
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

# Calculate the number of rows to generate
total_lines=$(( $(wc -l < Purchased.csv) - 1 ))
num_rows=$((total_lines * 9 / 10))

echo "Generating reviews for 0-$num_rows products..."

# Get all rows from Purchased.csv
mapfile -t rows < <(tail -n +2 Purchased.csv)

# Output the header to Review.csv
echo "UserID,ModelID,Rating,Message" > Review.csv

count=1
declare -A user_model_pairs

# For each row in Purchased.csv
for purchase in "${rows[@]}"; do

    # Break the loop if we have generated enough rows
    if ((num_rows == 0)); then
        break
    fi

    # Get ModelID and OrderID from the row
    IFS=',' read -r ModelID _ OrderID _ <<< "$purchase"

    # Get the email associated with the OrderID from Order.csv
    Email=$(awk -F, -v OrderID="$OrderID" '$1 == OrderID {print $5}' Order.csv)

    # Get the UserID associated with the email from Customer.csv
    UserID=$(awk -F, -v Email="$Email" '$4 == Email {print $1}' Customer.csv)

    # If UserID is NULL or this user-model pair has already been processed, skip this product
    if [[ -z $UserID ]] || [[ -n ${user_model_pairs["$UserID,$ModelID"]} ]]; then
        continue
    fi
    user_model_pairs["$UserID,$ModelID"]=1

    # Generate a random rating between 0 and 5
    Rating=$((RANDOM % 6))

    # Output the row to Review.csv
    if (( count % 5 == 0 )); then
        echo "$UserID,$ModelID,$Rating,NULL" >> Review.csv
    else
        echo "$UserID,$ModelID,$Rating,\"${messages[$Rating]}\"" >> Review.csv
    fi
    num_rows=$((num_rows - 1))

    count=$((count + 1))
done

echo -e "Generated 'Review.csv' with $(( $(wc -l < Review.csv) - 1 )) rows.\n"