#!/bin/bash

# Check if argument is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <max_number_of_coupons>"
    exit 1
fi

# Get maximum number of coupons per member from the first script argument
max_coupons=$1

# Check if max_coupons is a number
if ! [[ $max_coupons =~ ^[0-9]+$ ]]; then
    echo "Error: Maximum number of coupons must be a number"
    exit 1
fi

# CSV header
echo "UserID,ModelID,Discount,PointCost,Expiration" > Coupon.csv

# Read member.csv into an array, excluding the header
mapfile -t members < <(tail -n +2 member.csv)

# For each member
for member in "${members[@]}"
do
    # Get UserID
    userID=$(echo "$member" | cut -d',' -f1)

    # Generate a random number of coupons (0-max_coupons)
    num_coupons=$((RANDOM % (max_coupons + 1)))

    # For each coupon
    for ((i=0; i<$num_coupons; i++))
    do
        # Generate a random ModelID (100000-999999)
        modelID=$((RANDOM % 900000 + 100000))

        # Generate a random Discount (0.00-0.99) without using bc
        discount="0.$((RANDOM % 100))"

        # Generate a random PointCost (0-100)
        pointCost=$((RANDOM % 101))

        # Generate a random Expiration date (2022-01-01 to 2026-12-31)
        year=$((RANDOM % 5 + 2022))
        month=$(printf "%02d" $((RANDOM % 12 + 1)))
        day=$(printf "%02d" $((RANDOM % 28 + 1)))
        expiration="$year-$month-$day"

        # Write the coupon to Coupon.csv
        echo "$userID,$modelID,$discount,$pointCost,$expiration" >> Coupon.csv
    done
done

# Print success message
echo "Generated 'Coupon.csv' with $(wc -l < Coupon.csv) rows."