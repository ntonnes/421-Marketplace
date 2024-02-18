#!/bin/bash

num_categories=$1
echo -e "\nCreating $num_categories categories..."
echo "CName" > Category.csv

ascii_value=65      # ASCII value for 'A'

for ((i=1; i<=num_categories; i++))                     # For each category to add
do
    letter=$(printf "\\$(printf '%03o' $ascii_value)")  # Convert ASCII value to character
    echo "category$letter" >> Category.csv              # Write the row to Category.csv
    ascii_value=$((ascii_value + 1))                    # Increment ASCII value
done

echo "Generated 'Category.csv' with $(( $(wc -l < Category.csv) - 1 )) categories."