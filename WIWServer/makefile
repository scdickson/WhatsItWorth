JFLAGS = -classpath .:mysql-connector-java-5.1.17-bin.jar:xchart-2.2.1.jar:commons-codec-1.8.jar
JC = javac
JVM = java
MAIN = WIWServer
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES = \
	  CurrencyConverter.java \
	  DBConnection.java \
	  itemCache.java \
	  MTGStockScraper.java \
	  TCGScraper.java \
	  Type.java \
	  WIWConstants.java \
	  WIWServer.java \
	  ItemServer.java \
	  CurrencyGraphScraper.java

default: classes
classes: $(CLASSES:.java=.class)
clean:
	$(RM) *.class
