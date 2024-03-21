
# LINT_FLAG  = -Xlint:deprecation

    Classes = $(patsubst %.java, %.class, $(Srcs))
    Srcs    = $(shell echo *java)
    Targ    = Jarm

    Jar     = $(Targ).jar

# --------------------------------------------------------------------
%.class : %.java
		javac $(LINT_FLAG) $<

% : %.class
		java $@ $(Args)

# --------------------------------------------------------------------
run : $(Classes)
		java $(Targ)

all : $(Classes)

jar : all
		jar cmvf          MANIFEST.MF $(Targ).jar *.class

# --------------------------------------------------------------------
neat :
		rm -f *~ *.class *.out *.xgr

clean : neat
		rm -f *.jar
