package javax0.geci.api;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public interface Source {

    /**
     * Return the named segment that the generator can write. Return {@code null} if there is no such segment
     * in the file.
     *
     * @param id the name of the segment as defined in the {@code id="..."} xml tag of the {@code <editor-fold ...>}
     * @return the segment or {@code null}.
     * @throws IOException in case there is no file or file is not readable
     */
    Segment open(String id) throws IOException;

    /**
     * Create a temporary segment. This segment is dangling and does not belong to the actual source and also it does
     * not have a name. The starting tab stop of the segment is zero. Extra padding is added to the lines when the
     * segment is appended to the final segment. As it implies such temporary segments can be used
     * to write code into this segment and later merge this segment into one segment that belongs to a source.
     *
     * @return a new anonymous segment
     */
    Segment temporary();

    /**
     * Open the "global" segment that is the whole source file. Usually used on sources that are created calling
     * {@link #newSource(String)}
     * <p>
     * If you invoke this method on a source that was "hand-made" then you will essentially delete the content of the
     * file. If you want to get the content of a source file you should call {@link #getLines()} on the source object
     * itself.
     * @return the new segment object.
     */
    Segment open();

    /**
     * Generators can use this method to read the whole content of a file. The content of the list should not be
     * modified and should be treated as immutable.
     *
     * @return the list of the strings that contain the lines of the source file.
     */
    List<String> getLines();

    /**
     * Get the absolute file name of this source.
     *
     * @return the absolute file name of the source
     */
    String getAbsoluteFile();

    /**
     * Create a new source that may or may not exist. This source is going to be generated and if it already existed
     * it will be overwritten, unless the already existing file has exactly the same content as the new one.
     *
     * @param fileName relative file name to the current source.
     * @return the new {@code Source} object.
     */
    Source newSource(String fileName);

    /**
     * Create a new source that may or may not exist. This source is going to be generated and if it already existed
     * it will be overwritten, unless the already existing file has exactly the same content as the new one.
     * Create the new source file in the directory that was used to open the specified source set.
     *
     * @param fileName relative file name to the current source.
     * @param set      identifies the source set with a name
     * @return the new {@code Source} object.
     */
    Source newSource(Source.Set set, String fileName);

    /**
     * Initialize a segment. This is needed in case it is possible that the code generator does not
     * write anything into the segment. In that case the segment may not even be opened and in that
     * case the segment is not touched and the old text, presumably garbage may be there. For example,
     * you have a setter/getter generated code that selects some of the fields only, say only the
     * 'protected' fields. During the development you change the last 'protected' field to private
     * and there remains no field for which setter and getter is to be generated. If the segment is
     * not init-ed, then the code generator would not touch the segment and it may contain the
     * setter and the getter for the last 'protected' by now 'private' field.
     * <p>
     * Technically calling init is similar to opening a segment, though init should be more lenient
     * to opening non-existent segments.
     *
     * @param id the identifier of the segment. May be {@code null}.
     * @throws IOException in case there is no file or file is not readable
     */
    void init(String id) throws IOException;

    /**
     * Get the name of the class that corresponds to this source. The class may not exist though.
     * <p>
     * When calculating the class name the directory structure is used and the name of the source file
     * chopping off the {@code .java} or other extension.
     *
     * @return the class name that was calculated from the file name
     */
    String getKlassName();

    /**
     * Get the calculated class simple name. That is the name of the class without the package name in front of it.
     *
     * @return the simple name of the class
     */
    String getKlassSimpleName();

    /**
     * Get the name of the class where the class corresponding to the source is or should be.
     *
     * @return the name of the package
     */
    String getPackageName();

    /**
     * Get the class that corresponds to this source. If the class does not exists, either because the
     * source file is not a Java source or because the file was not compiled or just for any reason then
     * the method returns {@code null}.
     *
     * @return the class object or {@code null}
     */
    Class<?> getKlass();

    /**
     * Set serves as an identifier class for a source set. This is used when a source is asked to open a new
     * source that does not exist yet in another source set. When a source set is not identified
     * calling {@link Geci#source(String...)} it will have a {@code new Set("")} object as identifier. When a
     * generator needs to name a set it has to be identified and a specific first argument has to be passed to the
     * {@link Geci#source(Set, String...)} usually naming the source set typically as {@code set("java")} or
     * {@code set("resources")}.
     */
    class Set {
        private final String name;

        private Set(String name) {
            if (name == null) throw new IllegalArgumentException("Name can not be null");
            this.name = name;
        }

        /**
         * Import this method as a static import into the generators and into the test code that invokes the code
         * generator.
         *
         * @param name identifying string of the source set
         * @return identifier object.
         */
        public static Set set(String name) {
            return new Set(name);
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) return true;
            if (!(that instanceof Set)) {
                return false;
            }
            Set set = (Set) that;
            return Objects.equals(name, set.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    class Maven {
        private String module = null;
        private final String rootModuleDir;

        private Maven() {
            rootModuleDir = null;
        }

        private Maven(final String root) {
            rootModuleDir = root;
        }

        private String[] source(String mainOrTest, String java) {
            if (module == null) {
                return new String[]{"./src/" + mainOrTest + "/" + java};
            } else {
                if (rootModuleDir == null) {
                    return new String[]{
                        "./" + module + "/src/" + mainOrTest + "/" + java,
                        "./src/" + mainOrTest + "/" + java
                    };
                } else {
                    return new String[]{
                        rootModuleDir + "/" + module + "/src/" + mainOrTest + "/" + java,
                        "./" + module + "/src/" + mainOrTest + "/" + java
                    };
                }
            }
        }

        public String[] mainSource() {
            return source("main", "java");
        }

        public String[] testSource() {
            return source("test", "java");
        }

        public String[] testResources() {
            return source("test", "resources");
        }

        public String[] mainResources() {
            return source("main", "resources");
        }

        public Maven module(String module) {
            this.module = module;
            return this;
        }
    }

    static Maven maven(final String root) {
        return new Maven(root);
    }

    static Maven maven() {
        return new Maven();
    }
}
