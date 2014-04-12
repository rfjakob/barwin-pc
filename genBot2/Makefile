# Compile java code and build jar file
all:
	mkdir -p bin
	javac -cp javac -cp ../serialRMI/src/ -d bin *.java -d bin *.java
	cd bin; jar -cf genBot2.jar genBot2/*.class

clean:
	rm -Rf bin
