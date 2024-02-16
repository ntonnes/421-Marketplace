#!/bin/bash
./scripts/clear.sh

echo
echo "Creating users..."
./scripts/users.sh 25 25 25 25
echo "Generated 'Customer.csv' with $(( $(wc -l < Customer.csv) - 1 )) rows."
echo "Generated 'Member.csv' with $(( $(wc -l < Member.csv) - 1 )) rows."
echo "Generated 'Admin.csv' with $(( $(wc -l < Admin.csv) - 1 )) rows."
echo "Generated 'Guest.csv' with $(( $(wc -l < Guest.csv) - 1 )) rows."
echo "Generated 'User.csv' with $(( $(wc -l < User.csv) - 1 )) rows."

echo
echo "Creating categories.."
./scripts/categories.sh 20
echo "Generated 'Category.csv' with $(( $(wc -l < Category.csv) - 1 )) categories."

echo
echo "Creating brand pages.."
./scripts/brands.sh 20
echo "Generated 'BrandPage.csv' with $(( $(wc -l < BrandPage.csv) - 1 )) rows."
echo "Generated 'Manages.csv' with $(( $(wc -l < Manages.csv) - 1 )) rows."

echo
echo "Creating models.."
./scripts/models.sh 30 3
echo "Generated 'Model.csv' with $(( $(wc -l < Model.csv) - 1 )) rows."
echo "Generated 'Belongs.csv' with $(( $(wc -l < Belongs.csv) - 1 )) rows."

echo
echo "Creating coupons.."
./scripts/coupons.sh 5
echo "Generated 'Coupon.csv' with $(( $(wc -l < Coupon.csv) - 1 )) rows."

echo
echo "Creating carts.."
./scripts/carts.sh
echo "Generated 'InCart.csv' with $(( $(wc -l < InCart.csv) - 1 )) rows."

echo
echo "Creating restocks.."
./scripts/restocks.sh 30
echo "Generated 'Restock.csv' with $(( $(wc -l < Restock.csv) - 1 )) rows."

echo
echo "Creating cards.."
./scripts/cards.sh 30 4
echo "Generated 'Card.csv' with $(( $(wc -l < Card.csv) - 1 )) rows."

echo
echo "Creating products.."
./scripts/products.sh 15
echo "Generated 'Product.csv' with $(( $(wc -l < Product.csv) - 1 )) rows."

echo
echo "Data refreshed successfully."