# Copyright 2014, Gurobi Optimization, Inc.

PLATFORM = linux64
INC      = ../../include/
JSRC     = ../LP/src/paper
CLASSDIR = -classpath /opt/gurobi605/linux64/lib/gurobi.jar:.
JFLAG    = -d . $(CLASSDIR)

LpPaper.class: $(JSRC)/LpPaper.java
	javac $(JFLAG) $(JSRC)/LpPaper.java

LpPaper: LpPaper.class
	java $(CLASSDIR): paper/LpPaper