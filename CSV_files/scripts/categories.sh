#!/bin/bash

num_categories=$1

# Define 30 categories
categories=(
    "Books" "Clothing" "Home & Kitchen" "Toys & Games" "Sports & Outdoors" 
    "Health & Personal Care" "Beauty & Cosmetics" "Automotive" "Groceries" 
    "Office Supplies" "Pet Supplies" "Jewelry" "Musical Instruments" 
    "Crafts & Sewing" "Garden & Outdoor" "Tools & Home Improvement" "Baby" 
    "Movies & TV" "Music" "Furniture" "Appliances" "Industrial" "Cell Phones & Accessories" 
    "Cameras & Photography" "Audio & Home Theater" "Software" "Gift Cards" "Kitchen Appliances" 
    "Outdoor Recreation" "Travel Gear"
)

# Initialize CSV file
echo -e "\nCreating $num_categories categories..."
echo "CName" > Category.csv

# Shuffle categories and select the first $num_categories
IFS=$'\n' selected_categories=($(printf "%s\n" "${categories[@]}" | shuf -n $num_categories))

# Write selected categories to Category.csv
for category in "${selected_categories[@]}"; do
    echo "$category" >> Category.csv
done

echo "Generated 'Category.csv' with $(( $(wc -l < Category.csv) - 1 )) categories."