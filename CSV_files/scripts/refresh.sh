#!/bin/bash

# Clear previous data
./scripts/clear.sh

# Prompt for data set size
while true; do
    read -p "Would you like to generate a small, medium, or large data set (s/m/l)? " size
    if [[ "$size" == "s" ]]; then
        x=1; break
    elif [[ "$size" == "m" ]]; then
        x=2; break
    elif [[ "$size" == "l" ]]; then
        x=5; break
    else
        echo "Invalid input. Please enter 's', 'm', or 'l'."
    fi
done
echo -e "Generating a size $size data set...\n"

# Generate users
# Args: number of each type of user
n=$(( x*5 ))
./scripts/users.sh $n $n $n $n

# Generate categories
# Args: number of categories
n=$(( x*5 ))
./scripts/categories.sh $n

# Generate brands
# Args: number of brands, maximum number of categories per brand
n=$(( x*5 ))
m=$(( x*2 ))
if (( m > 6 )); then m=6; fi
./scripts/brands.sh $n $m

# Generate models
# Args: number of models, maximum number of categories per model
n=$(( x*6 ))
m=$(( x+1 ))
./scripts/models.sh $n $m

# Generate coupons
# Args: max coupons per user
n=$(( x*2 ))
./scripts/coupons.sh $n

# Generate carts
# Arguments: number of carts, maximum number of items per cart
n=$(( x+1 ))
m=$(( x+1 ))
./scripts/carts.sh $n $m

# Generate restocks
# Args: number of restocks, maximum number of items per restock
n=$(( x*6 ))
m=$(( x*4 ))
./scripts/restocks.sh $n $m

# Generate cards
# Args: number of cards, maximum number of items per card
n=$(( x*6 ))
m=$(( x*2 ))
./scripts/cards.sh $n $m

# Generate products
# Args: number of products
n=$(( x*5 ))
./scripts/products.sh $n $m

# Generate orders
# Args: number of orders
n=$(( x*10 ))
./scripts/orders.sh $n

# Generate shipments
# Args: number of shipments
n=$(( x*8 ))
./scripts/shipments.sh $n $m

# Generate purchases
# Args: percentage of products to assign
./scripts/purchases.sh 40

# Generate reviews
# Args: None
./scripts/reviews.sh

echo "Data refreshed successfully."