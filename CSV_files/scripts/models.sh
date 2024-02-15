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

# Ask for maximum number of categories per model
read -p "Enter maximum number of categories per model: " max_categories

# Check if max_categories is a number
if ! [[ $max_categories =~ ^[0-9]+$ ]]; then
    echo "Error: Maximum number of categories must be a number"
    exit 1
fi

# Read URLs from BrandPage.csv into an array, skipping the header
mapfile -t urls < <(tail -n +2 BrandPage.csv | cut -d, -f1)

# Read CNames from Category.csv into an array, skipping the header
mapfile -t cnames < <(tail -n +2 Category.csv | cut -d, -f1)

# CSV header
echo "ModelID,Price,URL,Stars" > Model.csv
echo "CName,ModelID" > Belongs.csv

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

    # Append row to Model.csv
    echo "$ModelID,$Price,$URL,$Stars" >> Model.csv

    # Generate a random number of categories (0-max_categories)
    num_categories=$((RANDOM % (max_categories + 1)))

    # For each category
    for ((j=0; j<$num_categories; j++))
    do
        # Select random CName from the array
        CName=${cnames[$RANDOM % ${#cnames[@]}]}

        # Append row to Belongs.csv
        echo "$CName,$ModelID" >> Belongs.csv
    done
done

echo "Generated 'Model.csv' with $num_rows rows."
echo "Generated 'Belongs.csv' with $(wc -l < Belongs.csv) rows."