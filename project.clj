(defproject org.msync/lein-javadoc "0.4.0-SNAPSHOT"
  :description "Run javadoc for the java source in your lein project."
  :url "http://github.com/jaju/lein-javadoc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true

  ;; For testing
  :javadoc-opts {
    :package-names ["lein_javadoc.test"]
    :java-source-paths ["test/javasrc"]}

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  )
