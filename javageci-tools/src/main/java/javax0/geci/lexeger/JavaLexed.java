package javax0.geci.lexeger;

import javax0.geci.api.Source;
import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>A JavaLexed object contains a Java source file as a list of
 * lexemes. It provides different methods to modify the lexical
 * structure of the source file.</p>
 *
 * <p>A JavaLexed object can be created from a Source object borrowing
 * the source lines from the source. It can also be saved back to a
 * Source object returning the borrowed content.</p>
 */
public class JavaLexed implements AutoCloseable {
    private final Source source;
    private final ArrayList<LexicalElement> lexicalElements;
    private boolean isOpen = true;

    public JavaLexed(Source source) {
        this.source = source;
        lexicalElements =
            new ArrayList<>(
                List.of(
                    new Lexer()
                        .spaceSensitive()
                        .commentSensitive()
                        .apply(source.borrows())));
    }

    @Override
    public void close() {
        isOpen = false;
        final var lines = new ArrayList<String>();
        final var currentLine = new StringBuilder();
        for (final var lex : lexicalElements) {
            var lexeme = lex.getFullLexeme();
            while (lexeme.contains("\n")) {
                final var nlpos = lexeme.indexOf("\n");
                final var start = lexeme.substring(0, nlpos);
                currentLine.append(start);
                lines.add(currentLine.toString());
                currentLine.delete(0, currentLine.length());
                lexeme = lexeme.substring(nlpos + 1);
            }
            currentLine.append(lexeme);
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        source.returns(lines);
    }

    public Iterable<LexicalElement> lexicalElements() {
        return new LexicalIterable();
    }

    private class LexicalIterable implements Iterable<LexicalElement> {
        @Override
        public Iterator<LexicalElement> iterator() {
            return new LexicalIterator();
        }
    }

    private class LexicalIterator implements Iterator<LexicalElement> {
        private int index = 0;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return index < lexicalElements.size();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public LexicalElement next() throws NoSuchElementException {
            if (index >= lexicalElements.size()) {
                throw new NoSuchElementException("The JavaLexed source contains only " +
                                                     lexicalElements.size() +
                                                     " lexemes and there was a query for the " +
                                                     index + "-th element.");
            }
            return lexicalElements.get(index++);
        }
    }

    /**
     * Get the i-th element from the list of the lexical elements.
     * @param i the index to the element to fetch
     * @return the element
     */
    public LexicalElement get(int i) {
        return lexicalElements.get(i);
    }

    /**
     * Remove the i-th element from the list of the lexical elements and
     * return the removed object.
     * @param i the index to the element to be removed
     * @return the removed object
     */
    public LexicalElement remove(int i) {
        return lexicalElements.remove(i);
    }

    /**
     * Remove the elements from the list of lexical elements starting
     * with the index {@code fromIndex} (inclusive) till {@code toIndex}
     * (exclusive).
     *
     * @param fromIndex the index of the first element to be removed
     * @param toIndex the index of the element before which the elements
     *                will be removed. The element at the position
     *                {@code toIndex} will not be removed.
     */
    public void removeRange(int fromIndex, int toIndex) {
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            lexicalElements.remove(i);
        }
    }

    public void add(int index, LexicalElement le) {
        lexicalElements.add(index, le);
    }


    public int size(){
        return lexicalElements.size();
    }
}
