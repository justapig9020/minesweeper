all: Minesweeper.class

run: Minesweeper.class
	@java Minesweeper

Minesweeper.class: Minesweeper.java Field.java
	javac Minesweeper.java

clean:
	rm -f *.class
