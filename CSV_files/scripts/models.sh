#!/bin/bash

# Check if argument is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <number_of_rows>"
    exit 1
fi

# Check if argument is a number
if ! [[ $1 =~ ^[0-9]+$ ]]; then
    echo "Error: Argument must be a number"
    exit 1
fi

# Number of rows to generate
num_rows=$1

# Read URLs from BrandPage.csv into an array, skipping the header
mapfile -t urls < <(tail -n +2 BrandPage.csv | cut -d, -f1)

# CSV header
echo "ModelID,Price,URL,Stars" > Model.csv

# Associative array to store generated ModelIDs
declare -A modelIDs

# Generate rows
for (( i=1; i<=$num_rows; i++ ))
do
    # Generate a unique ModelID
    while true; do
        ModelID=$(( RANDOM % 900000 + 100000 ))
        if [[ -z ${modelIDs[$ModelID]} ]]; then
            modelIDs[$ModelID]=1
            break
        fi
    done

    # Generate random Price between 1 and 1000 with 2 decimal places
    Price=$(( RANDOM % 500 + 1 )).$(printf "%02d" $(( RANDOM % 100 )))

    # Select random URL from the array
    URL=${urls[$RANDOM % ${#urls[@]}]}

    # Generate random Stars between 0 and 10
    Stars=$(( RANDOM % 11 ))

    # Append row to CSV
    echo "$ModelID,$Price,$URL,$Stars" >> Model.csv
done

echo
echo "Generated 'Model.csv' with $num_rows rows."