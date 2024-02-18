#!/bin/bash

num_models=$1
max_categories=$2

mapfile -t urls < <(tail -n +2 BrandPage.csv | cut -d, -f1)
mapfile -t cnames < <(tail -n +2 Category.csv | cut -d, -f1 | tr -d '\r')
declare -A modelIDs
declare -A assignedCategories

echo "ModelID,Price,URL,Stars" > Model.csv
echo "CName,ModelID" > Belongs.csv
echo "Creating $num_models models with 0-$max_categories categories each..."

models_added=0                                      # Initialize counter
for (( i=1; i<=$num_models; i++ ))                  # For each model to add
do
    while true; do                                  # Loop until a unique ModelID is generated
        ModelID=$(( RANDOM % 900000 + 100000 ))    
        if [[ -z ${modelIDs[$ModelID]} ]]; then
            modelIDs[$ModelID]=1
            break
        fi
    done

    Price=$(( RANDOM % 500 + 1 )).$(printf "%02d" $(( RANDOM % 100 )))  # Generate random price between 1.00 and 500.99
    URL=${urls[$RANDOM % ${#urls[@]}]}                                  # Select random URL from the array
    echo "$ModelID,$Price,$URL,0" >> Model.csv                          # Append row to Model.csv

    num_categories=$((RANDOM % (max_categories + 1)))                   # Generate a random number of categories (0-max_categories)
    unset assignedCategories                                            # Clear the associative array
    declare -A assignedCategories                                       # Declare the associative array

    for ((j=0; j<$num_categories; j++))                                 # For each category to assign
    do
        while true; do                                                  # Loop until a unique CName is generated
            CName=$(echo "${cnames[$RANDOM % ${#cnames[@]}]}" | tr -d '\n')
            if [[ -z ${assignedCategories[$CName]} ]]; then
                assignedCategories[$CName]=1
                break
            fi
        done
        echo "$CName,$ModelID" >> Belongs.csv                           # Append row to Belongs.csv
    done

    
    models_added=$((models_added + 1))                                         # Increment counter
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $models_added / $num_models}")  # Calculate progress
    printf "\rProgress: %d%%" $percent

done

echo
echo "Generated 'Model.csv' with $(( $(wc -l < Model.csv) - 1 )) rows."
echo "Generated 'Belongs.csv' with $(( $(wc -l < Belongs.csv) - 1 )) rows."