#!/bin/bash

# Get maximum number of coupons per member from the first script argument
max_coupons=$1

echo "Assigning 0-$max_coupons coupons per member..."

# CSV header
echo "UserID,ModelID,Discount,PointCost,Expiration" > Coupon.csv

# Read member.csv into an array, excluding the header
mapfile -t members < <(tail -n +2 Member.csv)

# Get total number of members
total_members=${#members[@]}

# Initialize counter for processed members
processed_members=0

# For each member
for member in "${members[@]}"
do
    # Get UserID
    userID=$(echo "$member" | cut -d',' -f1)

    # Generate a random number of coupons (0-max_coupons)
    num_coupons=$((RANDOM % (max_coupons + 1)))

    # Get a shuffled list of ModelIDs
    mapfile -t modelIDs < <(tail -n +2 Model.csv | shuf | cut -d',' -f1)

    # For each coupon
    for ((i=0; i<$num_coupons; i++))
    do
        # Get a ModelID from the shuffled list
        modelID=${modelIDs[$i]}

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

    # Increment counter for processed members
    processed_members=$((processed_members + 1))

    # Calculate percentage of progress as a floating-point number and convert it to an integer
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_members / $total_members}")

    # Print percentage of progress
    printf "\rProgress: %d%%" $percent
done
echo
