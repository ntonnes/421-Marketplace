#!/bin/bash

max_coupons=$1

# Initialize CSV file
echo "Creating 0-$max_coupons coupons for each member..."
echo "UserID,ModelID,Discount,PointCost,Expiration" > Coupon.csv

# Get member data from Member.csv
mapfile -t members < <(tail -n +2 Member.csv)

total_members=${#members[@]}
processed_members=0

# Assign coupons to members
for member in "${members[@]}"
do
    userID=$(echo "$member" | cut -d',' -f1)
    num_coupons=$((RANDOM % (max_coupons + 1)))

    # Get shuffled list of ModelIDs from Model.csv
    mapfile -t modelIDs < <(tail -n +2 Model.csv | shuf | cut -d',' -f1)

    # Generate random coupon data
    for ((i=0; i<$num_coupons; i++))
    do
        modelID=${modelIDs[$i]}
        discount="0.$((RANDOM % 100))"
        pointCost=$((RANDOM % 101))
        expiration=$(date -d "$(( RANDOM % (365 * 5) + 1 )) days" +'%Y-%m-%d')

        # Write coupon data to CSV
        echo "$userID,$modelID,$discount,$pointCost,$expiration" >> Coupon.csv
    done

    processed_members=$((processed_members + 1))

    # Print progress
    percent=$(awk "BEGIN {printf \"%.0f\", 100 * $processed_members / $total_members}")
    printf "\rProgress: %d%%" $percent
done

echo -e "\nGenerated 'Coupon.csv' with $(( $(wc -l < Coupon.csv) - 1 )) rows.\n"