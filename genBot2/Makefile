# Compile java code and build jar file
genBotWIFolder = /opt/roboexotica/genBotWI/
all:
	mkdir -p bin
	javac -cp ${genBotWIFolder}lib/serialRMI.jar -d bin *.java
	cd bin; jar -cf genBot2.jar genBot2/*.class

install: all
	cp bin/genBot2.jar ${genBotWIFolder}lib/

clean:
	rm -Rf bin
