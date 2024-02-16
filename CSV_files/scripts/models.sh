#!/bin/bash

# Number of rows to generate
num_rows=$1
max_categories=$2

# Read URLs from BrandPage.csv into an array, skipping the header
mapfile -t urls < <(tail -n +2 BrandPage.csv | cut -d, -f1)

# Read CNames from Category.csv into an array, skipping the header
mapfile -t cnames < <(tail -n +2 Category.csv | cut -d, -f1 | tr -d '\r')

# CSV header
echo "ModelID,Price,URL,Stars" > Model.csv
echo "CName,ModelID" > Belongs.csv

# Associative array to store generated ModelIDs and assigned categories
declare -A modelIDs
declare -A assignedCategories

echo "Assigning 0-$max_categories Categories per Model..."

# Initialize counter for processed rows
processed_rows=0

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

    # Clear assigned categories for this model
    unset assignedCategories
    declare -A assignedCategories

    # For each category
    for ((j=0; j<$num_categories; j++))
    do
        # Select random CName from the array
        while true; do
            CName=$(echo "${cnames[$RANDOM % ${#cnames[@]}]}" | tr -d '\n')
            if [[ -z ${assignedCategories[$CName]} ]]; then
                assignedCategories[$CName]=1
                break
            fi
        done

        # Append row to Belongs.csv
        echo "$CName,$ModelID" >> Belongs.csv
    done

    # Increment counter for processed rows
    processed_rows=$((processed_rows + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_rows / $num_rows}")

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done
echo