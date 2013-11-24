# Compile java code and build a jar file

all:
	mkdir -p bin
	javac -cp /usr/share/java/RXTXcomm.jar -d bin src/serialRMI/*.java

#       Note: cd and command must be on one line (make spawns a new shell for each line)
	cd bin; jar cf serialRMI.jar serialRMI/*.class

clean:
	rm -Rf bin
