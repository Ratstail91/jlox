#source
JAVASRC=$(wildcard *.java)

#class files
OBJDIR=obj
OBJ+=$(JAVASRC:.java=.class)

#output
OUTDIR=../out
OUT=src.jar

#targets
all: $(OBJDIR) $(OBJDIR)/$(OBJ)
	jar cfm $(OUTDIR)/$(OUT) manifest.txt -C $(OBJDIR) $(OBJ)

$(OBJ): | $(OBJDIR)

$(OBJDIR):
	mkdir $(OBJDIR)

$(OBJDIR)/%.class: %.java
	javac -d $(OBJDIR) $<

clean:
ifeq ($(OS),Windows_NT)
	del /S /Q *.o *.a *.exe $(OUTDIR)\*.class
#	rmdir /S /Q $(OUTDIR)
else ifeq ($(shell uname), Linux)
	find . -type f -name '*.class' -exec rm -f -r -v {} \;
	find . -type f -name '*.jar' -exec rm -f -r -v {} \;
#	rm $(OUTDIR)/* -f
	find . -empty -type d -delete
endif

rebuild: clean all
