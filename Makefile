# Makefile for running GraphFun project
# enter 'make' to compile project
# enter 'make run' to run it
#
JCC = javac
JVM = java
DIR = src/
JFLAGS = -cp $(DIR)

default: GraphDisplay

run: $(DIR)GraphDisplay.class
	$(JVM) $(JFLAGS) GraphDisplay

GraphDisplay: $(DIR)GraphDisplay.java
	$(JCC) $(JFLAGS) $(DIR)GraphDisplay.java

clean:
	$(RM) $(DIR)*.class
