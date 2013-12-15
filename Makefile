# Compile java code and build a jar file
genBotWIFolder = ../genBotWI/
all:
	mkdir -p build
	javac -cp /usr/share/java/RXTXcomm.jar -d build src/serialRMI/*.java

#       Note: cd and command must be on one line (make spawns a new shell for each line)
	cd build; jar cf serialRMI.jar serialRMI/*.class

install: all
	cp -a build/serialRMI.jar ${genBotWIFolder}lib/serialRMI.jar

clean:
	rm -Rf build
