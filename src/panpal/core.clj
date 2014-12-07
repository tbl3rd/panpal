;; Everything from ';' to the end of a line is a Clojure comment.

;; A letter is one of "abcdefghijklmnopqrstuvwxyz".  Examples: \q \j \z

;; A word is a Clojure (Java) string of letters.  Example: "word"

;; A sentence is a sequence of words.  Example: ["sequence" "of" "words"]
;; [...] denotes a Clojure vector, which is like a Java array,
;; #{...} denotes a set, and {...} denotes a map.

;; In Java you call a method like this: object.method(arg1, arg2, arg3);
;; In Clojure, a call looks like this: (. object method arg1 arg2 arg3)
;; Or like this: (.method object arg1 arg1 arg3)

;; A palindrome is a sentence that when reduced to a string of letters
;; is the same string when reversed.
;;     Example: ["a" "man" "a" "plan" "a" "canal" "panama"]
;;     Reduced:  "amanaplanacanalpanama"

;; A pangram is a sentence that contains every letter.  For example:
;; ["the" "quick" "brown" "fox" "jumps" "over" "a" "lazy" "dog"]

;; A palindromic pangram is a pangram that is also a palindrome.
;; I don't know any good examples though.

;; Use words from ITA's WORD.LST file.  All words are lower-case.
;; No single-letter words, so none of the above sentences work ...

                                        ;-{

;; This says the following code is in the namespace "panpal.core".
;;
;; It also says we need two Java classes and four other Clojure
;; namespaces and that when we say 'io/reader', we really mean
;; 'clojure.java.io/reader', and so on.
;;
;; You get 'clojure.core' and 'java.lang' by default.
;;
(ns panpal.core
  (:import [java.io BufferedInputStream BufferedReader])
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as comb]
            [clojure.set]
            [clojure.string]))

;; (def name value) is how Clojure defines names in namespaces.
;;
(def alphabet "abcdefghijklmnopqrstuvwxyz")

;; (set ...) turns its argument into a Clojure set collection.
;;
(def letters (set alphabet))

;; (fn [arg ...] expression ...) is how Clojure makes functions.
;;
(def sentence->string (fn [sentence] (reduce str sentence)))

;; (defn name [arg ...] expression ...) is just shorthand for
;; (def name (fn [arg ...] expression ...)) and allows a doc string.
;;
(defn sentence->set
  "The set of letters in sentence."
  [sentence]
  (set (sentence->string sentence)))

;; 'clojure.set/difference' says find the name 'difference' in the
;; namespace 'clojure.set'.
;;
(defn missing-letters
  "Set of letters missing from sentence."
  [sentence]
  (clojure.set/difference letters (sentence->set sentence)))

;; It is customary to name predicates with a '?'.
;; (clojure.core/empty? ...) is true if sequence ... is empty.
;; Strings and all Clojure containers are sequences.
;;
(defn pangram?
  "True if sentence contains all letters.  False otherwise."
  [sentence]
  (empty? (missing-letters sentence)))

;; (let [binding ...] expression ...) introduces local names.
;; 'letters' is local to 'palindrome?' and not in the 'panpal.core'
;; namespace.
;;
(defn palindrome?
  "True if sentence is a palindrome.  False otherwise."
  [sentence]
  (let [letters (sentence->string sentence)]
    (= letters (clojure.string/reverse letters))))

;; (and ...) is a Clojure conditional.  Its value is the first false
;; argument.  In Clojure conditionals, everything tests true except
;; 'false' and 'nil'.  Clojure's 'false' is just Java's 'false'.
;; The same is true of 'true'.  Clojure's 'nil' is Java's 'null'.
;;
(defn palindromic-pangram?
  "True if sentence is a palindromic pangram.  False otherwise."
  [sentence]
  (and (palindrome? sentence) (pangram? sentence)))

;; Let's test it.
;;
(def napoleon ["able" "was" "i" "ere" "i" "saw" "elba"])
(def roosevelt ["a" "man" "a" "plan" "a" "canal" "panama"])
(def fox ["the" "quick" "brown" "fox" "jumps" "over" "a" "lazy" "dog"])


;; The (comment ...) form ignores ... and has the value 'nil'.
;;
(comment "What are the values of the following Clojure expressions?"

         napoleon

         roosevelt

         fox

         (pangram? roosevelt)

         (pangram? fox)

         (palidrome? fox)

         (palindrome? napoleon)

         (palindromic-pangram? roosevelt)

         (palindromic-pangram? fox)

         palindromic-pangram?

         "Anything else you want to evaluate now?")


;; (line-seq jibr) is a lazy sequence of lines from jibr, which must
;; be a java.io.BufferedReader.  Yes, those are the Java IO standard
;; library classes and that is Java's 'new'.
;;
(def words
  (line-seq (new java.io.BufferedReader
                 (new java.io.FileReader "WORD.LST"))))

;; This is the same code as above.  (FileReader. ...) is just more
;; Clojure shorthand for (new FileReader ...).
;;
(def words
  (line-seq (java.io.BufferedReader.
             (java.io.FileReader. "WORD.LST"))))

;; But the use of Java's IO library is so common that Clojure wraps it
;; in the 'clojure.java.io' namespace for more idiomatic use.
;;
;; 'io/reader' says: Look up the symbol 'reader' in the namespace
;; bound to 'io' which we said was 'clojure.java.io' in the (ns ...)
;; form at the top of the file.
;;
(def words
  (line-seq (io/reader (io/input-stream "WORD.LST"))))


;; Clojure sequences are "lazy".  Take only what you need.
;;
(take 7 words)

;; (time expression) prints the time it takes to evaluate expression.
;; Its value is the value of the expression.
;;
(time (take 23 words))

;; (count ...) counts the elements in ... so it must read all of it.
;;
(time (count words))


(comment
  "What are the values of the following expressions?"

  words

  (palindromic-pangram? (take 7 words))

  (time (palindromic-pangram? (take 7 words)))

  "But we need still more Clojure to solve this problem.")


;; (filter pred? ...) is the sequence for which pred? is true.
;;
(filter zero? [23 0 42 0 8 0 4 0 16 0 15])
(filter palindrome? [["gods" "dog"] ["malayalam"] ["aw" "no" "way"]])
(filter palindrome? [napoleon fox])
(filter pangram? [roosevelt fox])

;; (subsets ...) is all the subsets of a sequence.
;;
(comb/subsets (take 2 words))

;; (first ...) is the first value in a sequence.
;; (second ...) is the second value in a sequence.
;;
(first (comb/subsets (take 2 words)))
(second (comb/subsets (take 2 words)))

;; (apply f s ...) calls f with arguments collected from s ...
;;
(+ 0 1 2 3)
(apply + [0 1 2 3])

;; (map f s ...) is the sequence produced by applying f to the
;; elements of the sequences s ...
;;
(map + [0 1 2 3] (reverse [0 1 2 3]))

;; (concat s ...) is the sequence combining the sequences s ...
;;
(apply concat [[0 1 2] [3 4 5]])

;; (mapcat f s ...) is shorthand for (apply concat (map f s ...))
;;
(mapcat reverse [[2 1 0] [5 4 3]])

;; (permutations ...) is the permutations of the sequence ...
;; Use (mapcat ...) to collect them into a sequence of sentences.
;;
(mapcat comb/permutations (comb/subsets (take 2 words)))

;; The resulting sequence of sentences grows very fast.
;; Use (clojure.pprint/pprint ...) to show the structure better.
;;
(clojure.pprint/pprint
 (mapcat comb/permutations (comb/subsets (take 4 words))))

;; (sort ...) sorts the sequence ...
;;
(sort [23 42 8 4 16 15])
(sort ["twenty-three" "forty-two" "eight" "four" "sixteen" "fifteen"])

;; (sort-by f ...) sorts the sequence ... by the value of (f x) for
;; each x in the sequence.  Here we use (count ...).
;;
(sort-by
 count
 ["twenty-three" "forty-two" "eight" "four" "sixteen" "fifteen"])


(comment
  "We are done!"

  "This is the answer assuming there is a palindromic pangram
   without a repeated word."

  (first
   (sort-by count
            (filter palindromic-pangram?
                    (mapcat comb/permutations
                            (comb/subsets words)))))

  "Wait!  That finds the one with the fewest words.")

(comment
  "Replace count to find the panpal with the fewest letters ..."

  (first
   (sort-by (fn [sentence] (reduce + (map count sentence)))
            (filter palindromic-pangram?
                    (mapcat comb/permutations
                            (comb/subsets words)))))

  "NOW we are done.")

;; BTW: Here's a video to watch while you wait.
;;
"http://www.theninthwatch.com/feed/"
