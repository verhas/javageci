package javax0.geci.api;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public interface Source {

    /**
     * This method can be used to specify that the directories are standard maven directories. When the
     * project is multi-module and the test code invoking Geci is not in the same module as the code needing to be
     * processed by the generator then declaration of sources should use this method. The argument should be '{@code ..}'
     * or whatever the directory of the root module is relative to the directory of the module where the test is.
     *
     * @param root the directory to the root module where the top level {@code pom.xml} containing the
     *             <pre>
     *                                                 {@code <modules>
     *                                                     <module>...</module>
     *                                                   </modules>}
     *                                                 </pre>
     *             declaration is.
     * @return a new Maven source directory configuration object.
     */
    static Maven maven(final String root) {
        return new Maven(root);
    }

    /**
     * Simple method to specify that the sources are in a maven project and they follow the standard directory
     * structure.
     *
     * @return a new Maven source directory configuration object.
     */
    static Maven maven() {
        return new Maven();
    }

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
     *
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
            if (name == null)
                throw new IllegalArgumentException("Name can not be null");
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

    class NamedSourceSet {
        final Set set;
        final String[] directories;

        public NamedSourceSet(Set set, String[] directories) {
            this.set = set;
            this.directories = directories;
        }
    }

    /**
     * Class to build up the directory structures that correspond to the Maven directory structure
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
         * Return the array of directories where the Java sources could be found.
         * <p>
         * If there is no maven module defined then there is only one directory,
         * <pre>
         * '{@code ./src/(main|test)/(java|resources)}'
         * </pre>
         * wehere the sources can be. This is sufficient. The current working directory is the project root
         * when the tests are started either interactively in an IDE or from the command line using the
         * command {@code mvn}.
         * </p>
         * <p>
         * If there is a maven module defined then execution of the tests and thus the generators can be started
         * with different current working directory. Practice shows that the current working directory is the
         * project root when you start the code generation along with the test using the {@code mvn} command. On the
         * other hand when the tests are executed from the IDE then the current working directory is the root
         * directory of the module root where the test belongs to.
         * </p>
         * <p>
         * When the test executing the code generation is in the same module as the code that is the target of the
         * code generation then the directories are
         * <pre>
         *     {@code ./module/src/(main|test)/(java|resources)
         *     }
         * </pre>
         * or
         * <pre>
         *     {@code ./src/(main|test)/(java|resources)
         *     }
         * </pre>
         * </p>
         * <p>
         * When the test is not in the same module as the code that needs the generational support then the
         * directories should be
         * code generation then the directories are
         * <pre>
         *     {@code $root/module/src/(main|test)/(java|resources)
         *     }
         * </pre>
         * or
         * <pre>
         *     {@code ./module/src/(main|test)/(java|resources)
         *     }
         * </pre>
         * where {@code $root} is where the root module is. In this case the test should specify the source
         * directories calling the static method {@link Source#maven(String)} specifying the value for
         * {@code $root} instead of simply calling {@link Source#maven()}. The specified {@code $root} is
         * usually simply '{@code ..}'.
         * </p>
         * This method calculates these directories and returns the arrays.
         *
         * @param mainOrTest      is either '{@code main}' or '{@code test}'
         * @param javaOrResources is either '{@code java}' or '{@code resources}'
         * @return the array of directory names where the generator has to look for the sources
         */
        private NamedSourceSet source(String name, String mainOrTest, String javaOrResources) {
            if (module == null) {
                return new NamedSourceSet(Set.set(name), new String[]{"./src/" + mainOrTest + "/" + javaOrResources});
            } else {
                if (rootModuleDir == null) {
                    return new NamedSourceSet(Set.set(name), new String[]{
                            "./" + module + "/src/" + mainOrTest + "/" + javaOrResources,
                            "./src/" + mainOrTest + "/" + javaOrResources
                    });
                } else {
                    return new NamedSourceSet(Set.set(name), new String[]{
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
            return source(Geci.TEST_RESOURCES,"test", "resources");
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
