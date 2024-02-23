#!/bin/bash

num_categories=$1

# Initialize CSV file
echo -e "\nCreating $num_categories categories..."
echo "CName" > Category.csv

ascii_value=65

# Generate categories
for ((i=1; i<=num_categories; i++))
do
    # Convert ASCII value to character
    letter=$(printf "\\$(printf '%03o' $ascii_value)")

    # Write the row to Category.csv
    echo "category$letter" >> Category.csv

    # Increment ASCII value
    ascii_value=$((ascii_value + 1))
done

echo "Generated 'Category.csv' with $(( $(wc -l < Category.csv) - 1 )) categories."