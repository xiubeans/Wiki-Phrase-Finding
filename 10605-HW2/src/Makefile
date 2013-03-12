JFLAGS = -g
JC = javac
TESTFILE = RCV1.very_small_test.txt
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = NBSortMerger.java NBSortClassifier.java NBSortTrainer.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

.PHONY demo:
demo:
	cat  abstract.tiny.train | java -Xmx128m NBSortTrainer | sort | java -Xmx128m NBSortMerger | sort | java NBSortClassifier abstract.tiny.test
test:
	cat  abstract.tiny.train | java -Xmx128m NBSortTrainer | sort | java -Xmx128m NBSortMerger | sort | java NBSortClassifier $(TESTFILE)

