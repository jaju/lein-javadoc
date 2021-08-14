(ns leiningen.javadoc
  (:require [leiningen.core.main :refer [abort]]
            [leiningen.core.classpath :as lein-cp]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn getenv
  "Wrap System/getenv(String) for testing."
  [k]
  (System/getenv k))

(defn getprop
  "Wrap System/getProperty for testing."
  ([k] (System/getProperty k))
  ([k d] (System/getProperty k d)))

;;;; Obviate lein-jdk-tools

(defn canonical-path
  "Given a set of path components, yield the canonical path, or nil if
not found."
  [path-parts]
  (let [f (apply io/file path-parts)]
    (when (.exists (io/file f))
      (.getCanonicalPath f))))

(defn javadoc-bin
  "Yield the canonical path to the javadoc binary, or nil if not found."
  [jdk-home-path]
  (canonical-path [jdk-home-path ".." "bin" "javadoc"]))

;;;; lein-javadoc

(defn get-javadoc-opts
  "Create the map of javadoc options, using defaults where possible."
  [project]
  (let [javadoc-opts (:javadoc-opts project)]
    {:output-dir (get javadoc-opts :output-dir "javadoc/")
     :java-source-paths (get javadoc-opts :java-source-paths
                          (get project :java-source-paths))
     :package-names (get javadoc-opts :package-names)
     :additional-args (get javadoc-opts :additional-args)
     :exact-command-line (get javadoc-opts :exact-command-line)
     :jdk-home (get javadoc-opts :jdk-home)
     :javadoc-cmd (get javadoc-opts :javadoc-cmd)}))

(defn check-options
  "Check the javadoc options and print a few diagnostics/warnings. Returns true
   if the javadoc command can be run."
  [javadoc-opts]
  (let [exact-cmd-line? (:exact-command-line javadoc-opts)
        missing-package-names? (empty? (:package-names javadoc-opts))]
    (if exact-cmd-line?
      (println "lein javadoc warning: `:exact-command-line` is set, using user-chosen command line. All other options in the project configuration are being ignored."))
    (if missing-package-names?
      (println "lein javadoc error: Required configuration key `:package-names` is empty or missing."))
    (not (or missing-package-names?))))

(defn opts->args
  [javadoc-opts]
  (or (:exact-command-line javadoc-opts)
    (concat ["-d"
             (:output-dir javadoc-opts)
             "-sourcepath"
             (str/join ":"
               (:java-source-paths javadoc-opts))
             "-subpackages"
             (str/join ":"
               (:package-names javadoc-opts))]
      (:additional-args javadoc-opts))))

(defn javadoc-cmd-path
  "Determine a path for shelling out to javadoc."
  [javadoc-opts]
  (or (:javadoc-cmd javadoc-opts)
    (when-let [jh (:jdk-home javadoc-opts)]
      (javadoc-bin jh))
    (getenv "JAVADOC_CMD")
    "javadoc"))

(defn make-classpath
  [project javadoc-opts]
  (concat
    (lein-cp/get-classpath project)
    (:java-source-paths javadoc-opts)))

(defn run-javadoc
  [sh-args]
  (let [pb (.inheritIO (ProcessBuilder. (into-array String sh-args)))
        p (try
            (.start pb)
            (catch java.io.IOException e
              (abort (str "Failed to find " (first sh-args)
                       " command.\n"
                       (.getMessage e)))))]
    (.waitFor p)
    (let [exit (.exitValue p)]
      (when (pos? exit)
        (abort (str "javadoc exited with exit code " exit))))))

(defn javadoc
  "Run javadoc"
  [project & args]
  (let [javadoc-opts (get-javadoc-opts project)]
    (when (check-options javadoc-opts)
      (let [jd-args (opts->args javadoc-opts)
            cp (make-classpath project javadoc-opts)
            javadoc-cmd (javadoc-cmd-path javadoc-opts)
            sh-args (list* javadoc-cmd
                      "-cp" (str/join \: cp)
                      jd-args)]
        (run-javadoc sh-args)))))
