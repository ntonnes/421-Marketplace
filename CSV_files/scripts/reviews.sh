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

# Filter Product.csv for rows where OrderID is not NULL, select $1 percent of those rows
declare -a filtered_products
while IFS=, read -r ModelID SerialNo Return OrderID SupplierName RestockNo
do
    if [[ $OrderID != NULL && $OrderID != OrderID ]]; then
        filtered_products+=("$ModelID,$SerialNo,$Return,$OrderID,$SupplierName,$RestockNo")
    fi
done < Product.csv

# Calculate the number of rows to select based on the provided percentage
total_lines=${#filtered_products[@]}
num_rows=$((total_lines * 9 / 10))

# Select a random subset of the filtered rows
selected_products=()
for i in $(shuf -i  0-$((total_lines-1)) -n $num_rows)
do
    selected_products+=("${filtered_products[$i]}")
done

# Output the header
echo "UserID,ModelID,Rating,Message" > Review.csv

count=1
declare -A user_model_pairs

# For each selected row
for product in "${filtered_products[@]}"; do
    IFS=, read -r ModelID SerialNo Return OrderID SupplierName RestockNo <<< "$product"

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
    Rating=$((RANDOM % 11))

    # Select the corresponding message
    if (( count % 5 == 0 )); then
        echo "$UserID,$ModelID,$Rating,NULL" >> Review.csv
    else
        echo "$UserID,$ModelID,$Rating,\"${messages[$Rating]}\"" >> Review.csv
    fi
    count=$((count + 1))

done