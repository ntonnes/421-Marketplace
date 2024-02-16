#!/bin/bash

# Get number of categories from the first script argument
num_categories=$1

# Create CSV header
echo "CName" > Category.csv

# Create categories
for ((i=1; i<=num_categories; i++))
do
    echo "category$i" >> Category.csv
done