# In the end, we want the jar file
all: ../../lib/genBot.jar

# Create jar file (depends on class files)
../../lib/genBot.jar: build/genBot/*.class
	cd build; jar -cf genBot.jar genBot/*.class; 
	mv build/genBot.jar ../../lib/

# Compile class files (depend on java files)
build/genBot/*.class: src/genBot/*.java
	mkdir -p build
	javac -cp ../serialRMI/src/ -d build src/genBot/*.java 
	
clean:
	rm -Rf build
	rm -f ../../lib/genBot.jar
