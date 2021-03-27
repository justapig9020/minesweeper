all: Minesweeper.class

run: Minesweeper.class
	@java Minesweeper

Minesweeper.class: Minesweeper.java
	javac Minesweeper.java

clean:
	rm -f *.class
