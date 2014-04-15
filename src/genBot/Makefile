# Compile java code and build jar file
all:
	mkdir -p build
	javac -cp ../serialRMI/src/ -d build src/genBot/*.java 
	cd build; jar -cf genBot.jar genBot/*.class; 
	mv build/genBot.jar ../../lib/
clean:
	rm -Rf build
