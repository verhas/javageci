package javax0.geci.api;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A {@code Source} represents a source file in the project that the
 * generator can modify. To do that there are methods that support the
 * modification. Also there are static methods that support the
 * definition of where the source files are.
 *
 * <p>The source file contains lines. There are certain parts in a
 * source file that are represented as segments. For example in the Java
 * code the lines that are between
 * <pre>
 *     {@code
 *     //<editor-fold id="segmentIdentifier">
 *     //</editor-fold>
 *     }
 * </pre>
 * <p>
 * contain a segment (excluding the surrounding lines that signal the
 * start and the end of the segment. The {@code Source} object can be
 * asked to provide a {@link Segment} object that represents those
 * lines. Segments can not be read. They are there to be written and
 * when a segment is written the lines written to the segment will
 * replace the original lines. Every segment has an identifier, in the
 * example above this is {@code segmentIdentifier}.
 */
public interface Source {

    /**
     * This method can be used to specify that the directories are
     * standard maven directories. When the project is multi-module and
     * the test code invoking Geci is not in the same module as the code
     * needing to be processed by the generator then declaration of
     * sources should use this method. The argument should be '{@code
     * ..}' or whatever the directory of the root module is relative to
     * the directory of the module where the test is.
     *
     * @param root the directory to the root module where the top level
     *             {@code pom.xml} containing the
     *             <pre>
     *             {@code <modules>
     *                 <module>...</module>
     *               </modules>}
     *             </pre>
     *             declaration is.
     * @return a new Maven source directory configuration object.
     */
    static Maven maven(final String root) {
        return new Maven(root);
    }

    /**
     * Simple method to specify that the sources are in a maven project
     * and they follow the standard directory structure.
     *
     * @return a new Maven source directory configuration object.
     */
    static Maven maven() {
        return new Maven();
    }

    /**
     * <p>Return the named segment that the generator can write. Return
     * {@code null} if there is no such segment in the file.</p>
     *
     * <p>The global segment cannot be opened while the lines of the segments
     * are borrowed. See {@link #borrows()} and {@link #returns(List)}</p>
     *
     * @param id the name of the segment as defined in the
     *           {@code id="..."} xml tag of the
     *           {@code <editor-fold ...>}
     * @return the segment or {@code null}.
     * @throws IOException in case there is no file or file is not
     *                     readable
     */
    Segment open(String id) throws IOException;

    /**
     * Segments start and ends signalled with specific lines. In some
     * cases the code generator allows the source object to invent a
     * nonexistent segment. In that case this is called the default
     * segment. When the default segment is handled the segment start
     * and end pre and postfixes are inserted into the segment.
     *
     * <p>Because this is a kind of dangerous feature this is only
     * performed when the generator allows it explicitly.
     *
     * <p>There is no method to set it false, but the framework resets
     * this flag before invocation of every generator {@link
     * Generator#process(Source)}.
     */
    void allowDefaultSegment();

    /**
     * Same as {@link #open(String)} but if the segment cannot be found then it
     * throws a Geci exception instead of returning {@code null}
     *
     * @param id the name of the segment as defined in the {@code id="..."} xml tag of the {@code <editor-fold ...>}
     * @return the segment and never {@code null}
     * @throws IOException in case there is no file or file is not readable
     */
    Segment safeOpen(String id) throws IOException;

    /**
     * Create a temporary segment. This segment is dangling and does not
     * belong to the actual source and also it does not have a name. The
     * starting tab stop of the segment is zero. Extra padding is added
     * to the lines when the segment is appended to the final segment.
     * As it implies such temporary segments can be used to write code
     * into this segment and later merge this segment into one segment
     * that belongs to a source.
     *
     * @return a new anonymous segment
     */
    Segment temporary();

    /**
     * <p>Open the "global" segment that is the whole source file. Usually used on sources that are created calling
     * {@link #newSource(String)}</p>
     * <p>If you invoke this method on a source that was "hand-made" then you will essentially delete the content of the
     * file. If you want to get the content of a source file you should call {@link #getLines()} on the source object
     * itself.</p>
     * <p>The global segment cannot be opened while the lines of the segments
     * are borrowed. See {@link #borrows()} and {@link #returns(List)}</p>
     *
     * @return the new segment object.
     */
    Segment open();

    /**
     * Get all the segment names that are defined in the source
     *
     * @return the set of the names of the segments
     */
    java.util.Set<String> segmentNames();

    /**
     * <p>Generators can use this method to read the whole content of a file. The content of the list should not be
     * modified and should be treated as immutable.</p>
     * <p>The lines cannot be read while they are borrowed. See {@link #borrows()} and {@link #returns(List)}</p>
     *
     * @return the list of the strings that contain the lines of the source file.
     */
    List<String> getLines();

    /**
     * <p>A generator may decide to use the lines of the source as they
     * are without the help of the Source object. To signal this
     * intention it should borrow the lines from the Source. While the
     * lines are borrowed the Source cannot be modified in any way
     * calling API that modifies the Source object.</p>
     *
     * <p>When the generator is ready with the modifications then it has
     * to return the possibly modified line list calling {@link
     * #returns(List)}</p>
     *
     * <p>A generator cannot call {@code borrows()} if the lines were
     * already borrowed.</p>
     *
     * @return the list of the lines
     */
    default List<String> borrows() {
        return getLines();
    }

    /**
     * <p>When a generator borrowed the source line (see {@link #borrows()} from the Source then it also has to
     * returns the lines before it finishes its work.</p>
     * <p>It is possible to tell the Source object that the lines were not modified. In this case the argument
     * has to be {@code null}</p>
     *
     * @param lines the list of the lines, or {@code null} in case the lines, as they are in the Source object are OK
     */
    void returns(List<String> lines);

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
     * Initialize a segment. This is needed in case when the code generator does not
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
     * <p>
     * Note: The seemingly weird decision to use 'K' in the name of the method provides some javax0.geci.consistency. The method
     * {@link #getKlass()} cannot be named {@code getClass()} because that would override the method of the same name
     * in the class {@code Object}. Based on that all the methods that are returning the name or simple name of the
     * "klass" uses the letter 'K' even though these two methods could be named with the letter 'C'.
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
     * Get a logger that the generator can use to send messages to the
     * logging system. It is recommended to use this logger in the
     * generator and not the system or other loggers as this logger may
     * be implemented in a way that it collects the messages of the
     * different generators and send it to the log target together
     * putting the messages that are related to the same source
     * together.
     *
     * @return the logger for the generator
     */
    Logger getLogger();

    /**
     * <p>Set serves as an identifier class for a source set. Essentially {@code Set} is nothing else, but a string
     * encapsulated in the class. The constructor and the methods help the definition and the generation of this
     * identifying string.</p>
     *
     *
     * <p>This class is used, for example, when a source is asked to open a new source that does not exist yet and the new
     * source should be created in the directory tree of a different source set. In this case the generator is calling
     * {@link #newSource(Set, String)} specifying the set creating a new {@code Set()} object with the name of the
     * source set.</p>
     *
     * <p>When a source set is not identified, for example it is specified calling the method {@link
     * Geci#source(String...)} that only specifies the alternative directories where the source files can be, then the
     * source set will have a {@code new Set("xxx")} object as identifier. The identifier {@code "xxx"} in this case is
     * a decimal number string and it is randomly created.</p>
     *
     * <p>When a generator needs to name a set it has to be identified and a specific first argument has to be passed to
     * the {@link Geci#source(Set, String...)} usually naming the source set typically as {@code set("java")} or {@code
     * set("resources")}.</p>
     */
    class Set {
        private String name;
        private final boolean autoName;

        /**
         * <p>Create a new source set object.</p>
         *
         * <p> The name can be {@code null}. In that case the name will be created automatically creating a random
         * number. The caller should also specify that the name that was passed in the first argument is something that
         * comes from configuration from the user code or it was automatically generated. In the latter case the second
         * argument has to be {@code true}.</p>
         *
         * <p>It is an error to state that the name was created automatically {@code autoName==true} and providing
         * a {@code null} value as a {@code name}. Such a situation is evidently a programming error in the caller
         * and as such should fail fast.</p>
         *
         * <p>Note that this constructor is {@code private} but there are {@code public} {@code static} methods in the
         * class that expose this functionality to the outside word. Also note that the class itself is package access,
         * which means that code out of the package should treat instances as opaque ID classes only to pass the
         * reference as parameter to classes in this package.</p>
         *
         * @param name     the name of the set or {@code null} in case there was no name specified. In this case a
         *                 random name (a decimal number) will be assigned to the set as a string identifier.
         * @param autoName signals if the name was created automatically. It is NOT a request to name the set
         *                 automatically. That is done in case {@code name == null}.
         */
        private Set(String name, boolean autoName) {
            if (name == null && autoName) {
                throw new GeciException("When the name for a set is not specified it cannot be 'autoName'");
            }
            if (name == null) {
                name = randomUniqueName();
            }
            this.name = name;
            this.autoName = autoName;
        }

        /**
         * Create a random identifier. For the shake of simplicity and to avoid identifier collision the ID string
         * created is the {@link Object#hashCode()} value in decimal format of this object.
         *
         * @return the random identifier string
         */
        private String randomUniqueName() {
            return "" + super.hashCode();
        }

        /**
         * When a set was automatically named, like {@code mainSource} then it may happen that there are more than
         * one sets with that name. It will cause collision in the directory map. In that case we try to rename the
         * set. If the name was specified by the user, from top level and not automatically named then it is an error.
         * The programmer using the package must not name two different sets with the same name. On the other hand when
         * the sets were getting their default name, then it is a safe routine to rename them.
         */
        public void tryRename() {
            if (autoName) {
                name = randomUniqueName();
            }
        }

        /**
         * <p>Create a {@code Source} set identifying object with the given name.</p>
         *
         * <p>Import this method as a static import into the generators and into the test code that invokes the code
         * generator.</p>
         *
         * @param name identifying string of the source set
         * @return a new identifier object.
         */
        public static Set set(String name) {
            return new Set(name, false);
        }

        /**
         * Crete a new set with the name {@code name}. The argument
         * {@code autoName} signals that the name is a default name and
         * it is not directly specified by the user. This is like {@code
         * mainSource} or {@code testResource}. In this case it may
         * happen that there are multiple sets with the same name
         * especially if multiple modules are specified as sources in a
         * multi-module project. If that happens when the application
         * tries to save the source into the map indexed by the source
         * set objects it renames the set with a "random" name.
         *
         * <p>Note that the "random" name is also used when the user
         * defines a set without name. In that case the argument {@code
         * name} is {@code null} and {@code autoName} has to be {@code
         * false}.
         *
         * @param name     the name of the set
         * @param autoName signals if the name was set automatically
         * @return the new set
         */
        public static Set set(String name, boolean autoName) {
            return new Set(name, autoName);
        }

        public static Set set() {
            return set(null);
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

    class NamedSourceSet {
        final public Set set;
        final public String[] directories;

        public NamedSourceSet(Set set, String[] directories) {
            this.set = set;
            this.directories = directories;
        }
    }

    /**
     * This class provides predicates that can be used as an argument
     * to the methods {@link Geci#source(Predicate, String...)} and
     * {@link Geci#source(Set, Predicate, String...)}.
     */
    class Predicates {

        /**
         * Creates the default predicate that tests  {@code true} if the
         * directory exists and is a directory.
         *
         * @return the predicate.
         */
        public static Predicate<String> exists() {
            return file -> new File(file).isDirectory();
        }

        /**
         * Returns a predicate that tests {@code true} if the file
         * exists, it is a directory and a file with the anchor name
         * can be found in the directory. The anchor name may contain
         * leading directory names that make it relative to the tested
         * directory.
         *
         * @param anchor the anchor file.
         * @return the predicate
         */
        public static Predicate<String> hasTheFile(String anchor) {
            return exists().and(file -> new File(file + anchor).exists());
        }

        /**
         * Returns a predicate that tests {@code true} if the file
         * exists, it is a directory and a one of the files with the
         * anchor names can be found in the directory. The anchor name
         * may contain leading directory names that make it relative to
         * the tested directory.
         *
         * @param anchors the anchor files one of which should be there
         *                in the directory.
         * @return the predicate
         */
        public static Predicate<String> hasOneOfTheFiles(String... anchors) {
            return exists().and(file ->
                                    Arrays.stream(anchors).anyMatch(anchor ->
                                                                        new File(file + anchor).exists()));
        }

        /**
         * Returns a predicate that tests {@code true} if the file
         * exists, it is a directory and a all of the files with the
         * anchor names can be found in the directory. The anchor name
         * may contain leading directory names that make it relative to
         * the tested directory.
         *
         * @param anchors the anchor files which all should be there in
         *                the directory.
         * @return the predicate
         */
        public static Predicate<String> hasAllTheFiles(String... anchors) {
            return exists().and(file ->
                                    Arrays.stream(anchors).allMatch(anchor ->
                                                                        new File(file + anchor).exists()));
        }
    }

    /**
     * Class to build up the directory structures that correspond to the
     * Maven directory structure
     */
    class Maven {
        private final String rootModuleDir;
        private String module = null;

        private Maven() {
            rootModuleDir = null;
        }

        private Maven(final String root) {
            rootModuleDir = root;
        }

        /**
         * Return the array of directories where the Java sources could
         * be found.
         *
         * <p> If there is no maven module defined then there is only
         * one directory,
         *
         * <pre>
         * '{@code ./src/(main|test)/(java|resources)}'
         * </pre>
         * <p>
         * where the sources can be. This is sufficient. The current
         * working directory is the project root when the tests are
         * started either interactively in an IDE or from the command
         * line using the command {@code mvn}.
         *
         * <p> If there is a maven module defined then execution of the
         * tests and thus the generators can be started with different
         * current working directories. Practice shows that the current
         * working directory is the project root when you start the code
         * generation along with the test using the {@code mvn} command.
         * On the other hand when the tests are executed from the IDE
         * then the current working directory is the root directory of
         * the module root where the test belongs to.
         *
         * <p> When the test executing the code generation is in the
         * same module as the code that is the target of the code
         * generation then the directories are
         *
         * <pre>
         *     {@code ./module/src/(main|test)/(java|resources)
         *     }
         * </pre>
         * <p>
         * or
         *
         * <pre>
         *     {@code ./src/(main|test)/(java|resources)
         *     }
         * </pre>
         *
         * <p> When the test is not in the same module as the code that
         * needs the generational support then the directories should be
         * code generation then the directories are
         *
         * <pre>
         *     {@code $root/module/src/(main|test)/(java|resources)
         *     }
         * </pre>
         * <p>
         * or
         *
         * <pre>
         *     {@code ./module/src/(main|test)/(java|resources)
         *     }
         * </pre>
         * <p>
         * where {@code $root} is where the root module is. In this case
         * the algorithm will also try {@code ..} as {@code $root},
         * which works if the module structure is only two levels.
         *
         * <p> In other cases when the structure level is more than two
         * the test should specify the source directories calling the
         * static method {@link Source#maven(String)} specifying the
         * value for {@code $root} instead of simply calling {@link
         * Source#maven()}. The specified {@code $root} is usually
         * simply '{@code ../..}' in case of three levels of maven
         * modules.
         *
         * <p> This method calculates these directories and returns the
         * arrays.
         *
         * @param mainOrTest      is either '{@code main}' or '{@code test}'
         * @param javaOrResources is either '{@code java}' or '{@code resources}'
         * @return the array of directory names where the generator has
         * to look for the sources
         */
        private NamedSourceSet source(String name, String mainOrTest, String javaOrResources) {
            if (module == null) {
                return new NamedSourceSet(Set.set(name, true), new String[]{"./src/" + mainOrTest + "/" + javaOrResources});
            } else {
                if (rootModuleDir == null) {
                    return new NamedSourceSet(Set.set(name, true), new String[]{
                        "./" + module + "/src/" + mainOrTest + "/" + javaOrResources,
                        "../" + module + "/src/" + mainOrTest + "/" + javaOrResources,
                        "./src/" + mainOrTest + "/" + javaOrResources
                    });
                } else {
                    return new NamedSourceSet(Set.set(name, true), new String[]{
                        rootModuleDir + "/" + module + "/src/" + mainOrTest + "/" + javaOrResources,
                        "./" + module + "/src/" + mainOrTest + "/" + javaOrResources
                    });
                }
            }
        }


        /**
         * See also {@link #source(String, String, String)}
         *
         * @return the array of directories where the main java sources could be found.
         */
        public NamedSourceSet mainSource() {
            return source(Geci.MAIN_SOURCE, "main", "java");
        }

        /**
         * See also {@link #source(String, String, String)}
         *
         * @return the array of directories where the test java sources could be found.
         */
        public NamedSourceSet testSource() {
            return source(Geci.TEST_SOURCE, "test", "java");
        }

        /**
         * See also {@link #source(String, String, String)}
         *
         * @return the array of directories where the test resource files could be found.
         */
        public NamedSourceSet testResources() {
            return source(Geci.TEST_RESOURCES, "test", "resources");
        }

        /**
         * See also {@link #source(String, String, String)}
         *
         * @return the array of directories where the main resource files could be found.
         */
        public NamedSourceSet mainResources() {
            return source(Geci.MAIN_RESOURCES, "main", "resources");
        }

        /**
         * Specify the maven module where the sources needing the code generation are.
         *
         * @param module the name of the maven module
         * @return {@code this}
         */
        public Maven module(String module) {
            this.module = module;
            return this;
        }
    }
}
