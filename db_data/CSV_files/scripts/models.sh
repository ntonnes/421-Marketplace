#!/bin/bash

num_models=$1
max_categories=$2

# Get URLs from BrandPage.csv
mapfile -t urls < <(tail -n +2 BrandPage.csv | cut -d, -f1)

# Get category names from Category.csv
mapfile -t cnames < <(tail -n +2 Category.csv | cut -d, -f1 | tr -d '\r')

declare -A modelIDs
declare -A assignedCategories

# Initialize CSV files
echo "ModelID,Price,URL,Stars" > Model.csv
echo "CName,ModelID" > Belongs.csv

models_added=0
for (( i=1; i<=$num_models; i++ ))
do
    # Generate unique ModelID
    while true; do
        ModelID=$(( RANDOM % 900000 + 100000 ))
        if [[ -z ${modelIDs[$ModelID]} ]]; then
            modelIDs[$ModelID]=1
            break
        fi
    done

    # Generate random price and select random URL
    Price=$(( RANDOM % 500 + 1 )).$(printf "%02d" $(( RANDOM % 100 )))
    URL=${urls[$RANDOM % ${#urls[@]}]}

    # Write model data to CSV
    echo "$ModelID,$Price,$URL,0" >> Model.csv

    num_categories=$((RANDOM % (max_categories + 1)))
    unset assignedCategories
    declare -A assignedCategories

    # Assign categories to model
    for ((j=0; j<$num_categories; j++))
    do
        # Generate unique CName
        while true; do
            CName=$(echo "${cnames[$RANDOM % ${#cnames[@]}]}" | tr -d '\n')
            if [[ -z ${assignedCategories[$CName]} ]]; then
                assignedCategories[$CName]=1
                break
            fi
        done

        # Write category data to CSV
        echo "$CName,$ModelID" >> Belongs.csv
    done

    # Print progress
    models_added=$((models_added + 1))
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $models_added / $num_models}")
    printf "\rProgress: %d%%" $percent
done

echo -e "\nGenerated 'Model.csv' with $(( $(wc -l < Model.csv) - 1 )) rows."
echo -e "Generated 'Belongs.csv' with $(( $(wc -l < Belongs.csv) - 1 )) rows.\n"