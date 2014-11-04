filename=.env
for line in `cat "$filename"`
do
	export $line
done
gradle test --info
