#!/bin/bash

# Ask for number of customers, members, admins, and guests
echo
read -p "Enter number of customers: " x
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