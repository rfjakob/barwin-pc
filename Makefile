# Compile java code and build jar file

all:
	mkdir -p bin
	javac -cp ../serialRMI/src/serialRMI.jar:/usr/share/java/guava.jar -d bin *.java
	cd bin; jar -cf genBot2.jar genBot2/*.class

clean:
	rm -Rf bin
