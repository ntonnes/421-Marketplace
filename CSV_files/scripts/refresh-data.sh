#!/bin/bash

./scripts/clear.sh

# Ask for number of customers, members, admins, and guests
echo
read -p "Enter number of customers (non-members): " x
read -p "Enter number of members: " y
read -p "Enter number of admins: " z
read -p "Enter number of guests: " w

# Run fill-user-tree.sh script
./scripts/fill-user-tree.sh $x $y $z $w

# Ask for number of brands
read -p "Enter number of brands: " b

# Run fill-brand-manages.sh script
./scripts/fill-brand-manages.sh $b

# Ask for number of models
read -p "Enter number of models: " m

# Run models.sh script
./scripts/models.sh $m

# Ask for maximum number of coupons per member
echo
read -p "Enter maximum number of coupons per member: " c

# Run coupons.sh script
./scripts/coupons.sh $c

# Run create-carts.sh script
echo
./scripts/create-carts.sh

# Ask for number of restocks
echo
read -p "Enter number of restocks: " r

# Run restocks.sh script
./scripts/restocks.sh $r

# Ask for number of guest cards and maximum number of cards per customer
echo
read -p "Enter number of cards without a UserID: " c1
read -p "Enter maximum number of cards per customer: " c2

# Run cards.sh script
./scripts/cards.sh $c1 $c2

# Ask for maximum number of products per model
echo
read -p "Enter maximum number of products per model: " p

# Run products.sh script
./scripts/products.sh $p

echo
echo "Data refreshed successfully."