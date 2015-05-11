filename=.env
for line in `cat "$filename"`
do
	export $line
done
java -cp repo/com/github/sendgrid-java-example/0.0.3/sendgrid-java-example-0.0.3-jar.jar com.github.sendgridjp.JavaMailTextExample
