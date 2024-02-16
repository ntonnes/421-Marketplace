#!/bin/bash

# Get number of categories from the first script argument
num_categories=$1

# Create CSV header
echo "CName" > Category.csv

# ASCII value for 'A'
ascii_value=65

# Create categories
for ((i=1; i<=num_categories; i++))
do
    # Convert ASCII value to character
    letter=$(printf "\\$(printf '%03o' $ascii_value)")

    echo "category$letter" >> Category.csv

    # Increment ASCII value
    ascii_value=$((ascii_value + 1))

done