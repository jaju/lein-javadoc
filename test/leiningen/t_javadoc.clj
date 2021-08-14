(ns leiningen.t-javadoc
  (:require [leiningen.javadoc :as jd]
            [clojure.test :refer :all]
            [clojure.string :as str]))

(deftest java-cmd-fallbacks
  ;; Command from option; JDK home from option
  ;; JAVADOC_CMD from environment; expected output.
  (are [opt-cmd opt-home home-exists? env    output]
       (= (with-redefs [jd/getenv #(if (= %& ["JAVADOC_CMD"])
                                     env
                                     "UNEXPECTED")
                        jd/canonical-path #(when home-exists?
                                             (str/join "/" %))]
            (jd/javadoc-cmd-path {:javadoc-cmd opt-cmd
                               :jdk-home opt-home}))
          output)
       ;; Prefer :java-cmd
       "comj"   "HOME"    true        "envj" "comj"
       ;; Fall back to :jdk-home
       nil     "HOME"    true         "envj" "HOME/../bin/javadoc"
       ;; Fall back to JAVADOC_CMD
       nil     "HOME"    false        "envj" "envj"
       nil     nil       nil          "envj" "envj"
       ;; Finally fall back to constant "javadoc"
       nil     nil       nil          nil    "javadoc"))

(deftest get-javadoc-opts
  (testing "empty -- defaulting"
    (is (= (jd/get-javadoc-opts {:javadoc-opts {}})
           (jd/get-javadoc-opts {})
           {:output-dir "javadoc/"
            :java-source-paths nil
            :package-names nil
            :additional-args nil
            :exact-command-line nil
            :jdk-home nil
            :javadoc-cmd nil})))
  (testing "full + unrecognized"
    (is (= (jd/get-javadoc-opts {:java-source-paths ["jsp"]
                                 :javadoc-opts
                                 {:output-dir "od"
                                  :package-names ["pn"]
                                  :additional-args ["aa"]
                                  :exact-command-line ["ecl"]
                                  :jdk-home "jh"
                                  :javadoc-cmd "jc"
                                  ;; confirm that unrecognized things
                                  ;; are not included
                                  :bogus-extra "be"}})
           {:output-dir "od"
            :java-source-paths ["jsp"]
            :package-names ["pn"]
            :additional-args ["aa"]
            :exact-command-line ["ecl"]
            :jdk-home "jh"
            :javadoc-cmd "jc"}))))
