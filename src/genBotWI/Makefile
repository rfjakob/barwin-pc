all:
#	Takes long to start even if nothing need to be recompiled,
#	so we only do it if the "target" build folder does not exist
#	yet (fresh clone). Play recompiles automatically if needed.
	test -d target || ../../lib/play/play compile

clean:
	rm -Rf target
