# Compile java code and build a jar file
all:
	mkdir -p build
	javac -cp /usr/share/java/RXTXcomm.jar -d build src/serialRMI/*.java
#       Note: cd and command must be on one line (make spawns a new shell for each line)
	cd build; jar cf serialRMI.jar serialRMI/*.class
	mv build/serialRMI.jar ../../lib/	
clean:
	rm -Rf build
