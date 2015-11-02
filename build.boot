#!/usr/bin/env boot

;; See: https://github.com/boot-clj/boot/wiki/Boot-Environment
;;
(set-env!
 :resource-paths #{"src"}
 :target-path "target"
 :dependencies '[[org.clojure/clojure "1.7.0"]])

(require '[panpal.core])

(deftask build
  "Build this."
  []
  (comp
   (aot :namespace '#{panpal.core})
   (pom :project 'panpal :version "0.1.0")
   (uber)
   (jar :main 'panpal.core
        :manifest
        {"Description" "Solve the palindromic pangram problem"
         "Url" "https://github.com/tbl3rd/panpal.git"})))

(deftask run
  "Run this program."
  []
  (with-pre-wrap fileset
    (panpal.core/-main)
    fileset))

(defn -main
  "Run this program as a script."
  [& args]
  (apply panpal.core/-main args))
