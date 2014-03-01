# Makefile for running GraphFun project
# enter 'make' to compile project
# enter 'make run' to run it
#
JCC = javac

JFLAGS = -g

default: GraphDisplay

run: GraphDisplay.class
	java GraphDisplay

GraphDisplay: GraphDisplay.java
	$(JCC) $(JFLAGS) GraphDisplay.java

clean: 
	$(RM) *.class
