all: ../../lib/serialRMI.jar ../../lib/librxtxSerial.so

# Create jar file (depends on class files)
../../lib/serialRMI.jar: build/serialRMI/*.class
#       Note: cd and command must be on one line (make spawns a new shell for each line)
	cd build; jar cf serialRMI.jar serialRMI/*.class
	mv build/serialRMI.jar ../../lib/

# Compile class files
build/serialRMI/*.class: src/serialRMI/*.java
	mkdir -p build
	javac -cp /usr/share/java/RXTXcomm.jar -d build src/serialRMI/*.java

# Create symlink to librxtxSerial.so
# Works for Fedora64 and Debian
../../lib/librxtxSerial.so:
	@if [ -e /usr/lib64/rxtx/librxtxSerial.so ]; \
	then ln -vsTf /usr/lib64/rxtx/librxtxSerial.so ../../lib/librxtxSerial.so; \
	elif [ -e /usr/lib/jni/librxtxSerial.so ]; \
	then ln -vsTf /usr/lib/jni/librxtxSerial.so ../../lib/librxtxSerial.so; \
	else echo "Could not find librxtxSerial.so"; exit 1; \
	fi

clean:
	rm -Rf build
	rm -f ../../lib/serialRMI.jar
	rm -f ../../lib/librxtxSerial.so
