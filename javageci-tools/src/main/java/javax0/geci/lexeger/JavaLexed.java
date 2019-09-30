package javax0.geci.lexeger;

import javax0.geci.api.Source;
import javax0.geci.javacomparator.LexicalElement;
import javax0.geci.javacomparator.lex.Lexer;

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

    /**
     * <p>Closes the object.</p>
     *
     * <p>Closing the {@code JavaLexed} object will convert the current
     * lexical elements list into lines of source code and will inject
     * the lines into the source object from which the {@code JavaLexed}
     * object was created.</p>
     *
     * <p>Closing is essentially the end of the use of the {@code
     * JavaLexed} object. If a {@code JavaLexed} is closed you cannot do
     * anything with the object any more. Calling any of the methods on
     * an already closed {@code JavaLexed} object will throw {@link
     * IllegalArgumentException}.</p>
     *
     * <p>The method is defined in {@link AutoCloseable} thus it is
     * possible, and it is also recommended to use the constructor in
     * the head of a try-with-resources block, perform the operations in
     * the try block and let the method {@code close()} be invoked
     * automatically.</p>
     */
    @Override
    public void close() {
        assertOpen();
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

    private void assertStartEndOrder(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException(start + " start value cannot be larger than " + end + " end value.");
        }
    }

    private void assertOpen() {
        if (!isOpen) {
            throw new IllegalArgumentException("JavaLexed must not be used after it was closed.");
        }
    }

    /**
     * Get the lexical elements as an iterable so that a {@code for}
     * loop can iterate through all the elements.
     *
     * @return the iterable that can be used in a foreach construct.
     */
    public Iterable<LexicalElement> lexicalElements() {
        assertOpen();
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
            assertOpen();
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
            assertOpen();
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
     *
     * @param i the index to the element to fetch
     * @return the element
     */
    public LexicalElement get(int i) {
        assertOpen();
        return lexicalElements.get(i);
    }

    /**
     * Remove the i-th element from the list of the lexical elements and
     * return the removed object.
     *
     * @param i the index to the element to be removed
     * @return the removed object
     */
    public LexicalElement remove(int i) {
        assertOpen();
        return lexicalElements.remove(i);
    }

    /**
     * Remove the elements from the list of lexical elements starting
     * with the index {@code fromIndex} (inclusive) till {@code toIndex}
     * (exclusive).
     *
     * @param start the index of the first element to be removed
     * @param end   the index of the element before which the elements
     *              will be removed. The element at the position {@code
     *              toIndex} will not be removed.
     */
    public void removeRange(int start, int end) {
        assertStartEndOrder(start, end);
        assertOpen();
        for (int i = end - 1; i >= start; i--) {
            lexicalElements.remove(i);
        }
    }

    /**
     * <p>Replace the part of the lexeme list that was matched by the
     * match result with the elements of the lists.</p>
     *
     * <p>The first argument is the match result as returned by the
     * methods {@link LexMatcher#find()}, {@link LexMatcher#find(int)}
     * or {@link LexMatcher#matchesAt(int)}. If the result is no match
     * then there will be no replacement and the original list will be
     * intact.</p>
     *
     * @param result the result of a previous match
     * @param lists  the replacement lists that will get into the place
     *               of the elements of the original lexeme list that
     *               were matched
     * @return the index of the first lexical elements after the
     * replaced part. If the result was a no-match then the return value
     * is -1.
     */
    public int replace(MatchResult result, List<LexicalElement>... lists) {
        assertOpen();
        if (result.matches) {
            return replace(result.start, result.end, lists);
        }
        return -1;
    }

    /**
     * <p>Replace the lexical elements between {@code start} and {@code
     * end} with the lexical elements of the lists.</p>
     *
     * @param start the index of the first element of the rage that is
     *              to be replaced
     * @param end   the index of the first element AFTER the range that
     *              is to be replaced
     * @param lists the array of lists that will be inserted into the
     *              place of the replaced part
     * @return the index of the element after the replaced part
     */
    public int replace(int start, int end, List<LexicalElement>... lists) {
        assertOpen();
        assertStartEndOrder(start, end);
        removeRange(start, end);
        int j = start;
        for (final var list : lists) {
            for (final var element : list) {
                add(j++, element);
            }
        }
        return j;
    }

    /**
     * <p>Insert a single lexical emelent into the list of lexical
     * elements at the given position.</p>
     *
     * @param index is the position in the list where the new element
     *              will be added/inserted.
     * @param le    the new lexical element to be inserted
     */
    public void add(int index, LexicalElement le) {
        assertOpen();
        lexicalElements.add(index, le);
    }

    /**
     *
     * @return the number of lexical elements that are currently in the
     * list of lexical elements.
     */
    public int size() {
        assertOpen();
        return lexicalElements.size();
    }
}
