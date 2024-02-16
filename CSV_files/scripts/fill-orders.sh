#!/bin/bash

# Read Order.csv into an array, excluding the header
mapfile -t orders < <(tail -n +2 Order.csv)
mapfile -t products < <(tail -n +2 Product.csv | shuf)
mapfile -t cards < <(tail -n +2 Card.csv | shuf)

# Get total number of orders and products
total_orders=${#orders[@]}
total_products=${#products[@]}

# Initialize counter for processed orders
processed_orders=0
processed_products=0

# Initialize arrays to store updated orders and products
declare -a updated_orders
declare -a updated_products

# For each order
for ((i=0; i<$total_orders; i++))
do
    # Get OrderID and Total from the order
    IFS=',' read -ra order <<< "${orders[$i]}"
    orderID=${order[0]}
    total=${order[2]}

    # Generate a random number of products to add (1-X)
    num_products=$((RANDOM % $1 + 1))

    # For each product to add
    for ((j=0; j<$num_products; j++))
    do
        # Get the product from the shuffled list
        IFS=',' read -ra product <<< "${products[$((processed_products))]}"

        # Get ProductID, ModelID, and Cost from the product
        productID=${product[0]}
        modelID=${product[1]}
        cost=${product[2]}

        # Update the product's row in Product.csv
        product[3]="$orderID"
        product[4]="False"
        updated_products+=("${product[*]}")

        # Update the order's total
        total=$(awk -v total="$total" -v cost="$cost" 'BEGIN {print total + cost}')

        # Update the order's row in Order.csv
        order[2]=$total
        updated_orders+=("${order[*]}")
    done

    # Increment counter for processed orders
    processed_orders=$((processed_orders + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk -v processed_orders="$processed_orders" -v total_orders="$total_orders" 'BEGIN {printf "%.0f", 100 * processed_orders / total_orders}')

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done

printf "\n"

# Write updated orders and products back to the files
printf "%s\n" "OrderID,DeliverAdd,Total,Date,Email,CardNum" > Order.csv
printf "%s\n" "${updated_orders[@]}" >> Order.csv

printf "%s\n" "ModelID,SerialNo,Cost,OrderID,Return,SupplierName,RestockNo" > Product.csv
printf "%s\n" "${updated_products[@]}" >> Product.csv