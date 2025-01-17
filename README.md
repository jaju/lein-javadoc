# lein-javadoc

Use Leiningen to build the javadoc for the java source code in your projects.
Forked from - [davidsantiago/lein-javadoc](https://github.com/davidsantiago/lein-javadoc)

## How it differs from the original?
The newer JDKs (after 8) don't explicitly have `tools.jar` available - it is bundled differently.
But JDKs do supply the `javadoc` binary. We use the `javadoc` instead.

## Usage

Put `[org.msync/lein-javadoc "0.4.0-SNAPSHOT"]` into the `:plugins` vector of your project.clj.

For this plugin to do anything, you need to add a map of configuration
options to the `:javadoc-opts` key of your project map (or a profile
if you prefer). The map can have the following keys:

- `:package-names` *(Required)*: This key should be a vector of
  strings containing the names of the Java packages that should be
  included in the Javadoc. Since this cannot be deduced from a project
  regularly, it is required.
- `:output-dir` *(Default: "javadoc/")*: This key should have a string
  containing the path to the directory the Javadoc output will be
  written to.
- `:java-source-paths` *(Default: The value of `:java-source-paths` in
  the Leiningen project)*: This key is a vector of strings containing
  the paths to the project directories containing the Java sources
  that will have Javadoc run on them. This value defaults to the same
  key from the project itself, which is presumably set to something
  meaningful if you want to run a Javadoc task. There is probably not
  much need to set this one, unless you have a very specific desire to
  Javadoc a set of source code different somehow from the source code
  you want to compile.
- `:additional-args`: This key should have a vector of strings, as if
  they had been parsed off of the command line, for passing to Javadoc
  in addition to the usual options automatically set by this
  task. This task only directly supports convenient usage of a small
  number of the flags and options that the Javadoc tool supports. If
  you wish to use any of the ones not directly supported, you can set
  them here. Also, if you feel a particularly useful flag should be
  supported by this task, go ahead and send a note or (even better) a
  pull request.
- `:exact-command-line`: This key is a vector of strings, as if they
  had been parsed off of the command line, for passing to Javadoc as
  the *only* options it will see. If this key is set, all other flags
  and options are ignored when the Javadoc tool is invoked. You will
  also be warned, to head off potential frustration. This option
  exists as a safety valve, in case this task does not currently
  support some combination of configuration options you really need.
- `:jdk-home`: This key is a string indicating the path to the JDK
  home, used for determining the location of the `javadoc` command.
  This should include the `jre` directory, as in 
  `"/usr/lib/jvm/jdk-8-oracle-x64/jre"`.
- `:javadoc-cmd`: This key is a string indicating the path to the `javadoc`
  command. If not supplied, defaults to these in order:
    - `../bin/javadoc` relative to `:jdk-home`, if present;
    - value of `JAVADOC_CMD` in the environment;
    - the constant `javadoc`.

Also note that you must have the JDK installed for this task to work,
as Javadoc is a part of the JDK.

Once the plugin is configured for your project, you can invoke the
`javadoc` task to write the javadoc output to the configured directory.

    $ lein javadoc

To use an alternative `javadoc` program, use `JAVADOC_CMD` with lein:

    $ JAVADOC_CMD=/usr/lib/jvm/java-1.7.0-openjdk-amd64/bin/javadoc lein javadoc

By default this will have the effect of using the javadoc binary from the specified JDK.

## License

Copyright © 2013 David Santiago

Other contributors:

- Tim McCormack
- Ravindra Jaju

Distributed under the Eclipse Public License, the same as Clojure.