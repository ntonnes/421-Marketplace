#!/bin/bash

# Check if the number of products to add was provided
if [ $# -eq 0 ]
then
    echo "Please provide the number of products to add as a command line argument."
    exit 1
fi

# Read the header of Order.csv and find the index of OrderID and Total
IFS=',' read -ra order_header <<< "$(head -n 1 Order.csv)"
for ((i=0; i<${#order_header[@]}; i++)); do
    if [ "${order_header[$i]}" = "OrderID" ]; then
        orderID_index=$i
    elif [ "${order_header[$i]}" = "Total" ]; then
        total_index=$i
    fi
done

# Read the header of Product.csv and find the index of ProductID, ModelID, and Cost
IFS=',' read -ra product_header <<< "$(head -n 1 Product.csv)"
for ((i=0; i<${#product_header[@]}; i++)); do
    if [ "${product_header[$i]}" = "ProductID" ]; then
        productID_index=$i
    elif [ "${product_header[$i]}" = "ModelID" ]; then
        modelID_index=$i
    elif [ "${product_header[$i]}" = "Cost" ]; then
        cost_index=$i
    fi
done

# Read Order.csv into an array, excluding the header
mapfile -t orders < <(tail -n +2 Order.csv)

# Read Product.csv into an array, excluding the header, and shuffle the order
mapfile -t products < <(tail -n +2 Product.csv | shuf)

# Get total number of orders and products
total_orders=${#orders[@]}
total_products=${#products[@]}

# Initialize counter for processed orders
processed_orders=0

# For each order
for ((i=0; i<$total_orders; i++))
do
    # Get OrderID and Total from the order
    IFS=',' read -ra order <<< "${orders[$i]}"
    orderID=${order[$orderID_index]}
    total=${order[$total_index]}

    # Generate a random number of products to add (1-X)
    num_products=$((RANDOM % $1 + 1))

    # For each product to add
    for ((j=0; j<$num_products; j++))
    do
        # Get the product from the shuffled list
        IFS=',' read -ra product <<< "${products[$((i * $1 + j % total_products))]}"

        # Get ProductID, ModelID, and Cost from the product
        productID=${product[$productID_index]}
        modelID=${product[$modelID_index]}
        cost=${product[$cost_index]}

        # Update the product's row in Product.csv
        sed -i "s/^$productID,.*$/$productID,$modelID,$cost,$orderID,False/" Product.csv

        # Update the order's total
        total=$(awk "BEGIN {printf \"%.2f\", $total + $cost}")

        # Update the order's row in Order.csv
        order[$total_index]=$total
        sed -i "s/^$orderID,.*$/${order[*]}/" Order.csv
    done

    # Increment counter for processed orders
    processed_orders=$((processed_orders + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_orders / $total_orders}")

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done

printf "\n"