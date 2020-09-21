JAVAC=/usr/bin/javac
.SUFFIXES: .java .class

SRCDIR=src/
BINDIR=bin/
 

.PHONY: docs

all:
	javac -d bin $(SRCDIR)*.java

clean:
	rm ${BINDIR}*.class

runMed:
	java -cp bin/ Flow medsample_in.txt

runLarge:
	java -cp bin/ Flow largesample_in.txt

docs: 
	javadoc -d docs/ src/*.java

cleandocs:
	rm -r docs/*	