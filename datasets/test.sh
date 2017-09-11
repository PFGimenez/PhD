cd $1
set -i s/\ /,/g data
shuf data -o data_shuf
nb = 'wc -l data_shuf'
let nb = nb/2
split data_shuf -l nb
mv xaa set0_exemples.csv
mv xab set1_exemples.csv
cd ..
