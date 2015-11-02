(ns panpal.core
  (:import [java.io BufferedReader FileReader])
  (:require [clojure.pprint :as p]
            [clojure.string :as s])
  (:gen-class))

;; A 'word' is a string of lower-case letters: "word"
;; A 'sentence' is a vector of words: ["a" "vector" "of" "words"]

;; A 'palindrome' is a 'sentence' whose letters are the same read
;; forward and backward.  (def ted ...) defines 'ted' to be '...'.
;;
(def ted ["a" "man" "a" "plan" "a" "canal" "panama"])

;; A 'pangram' is a 'sentence' which contains all letters.
;;
(def fox ["quick" "brown" "fox" "jumps" "over" "the" "lazy" "dog"])

;; A 'palindromic pangram' or 'pangrammatic palindrome' ('panpal')
;; is a sentence that is both a pangram and a palindrome.

;; Use words from a WORD.LST file to make pangrammatic palindromes.
;; The panpals don't have to make sense, but look for short ones.

(def words
  "A lazy sequence of lines from the WORD.LST file."
  (line-seq (new java.io.BufferedReader
                 (new java.io.FileReader "WORD.LST"))))

(def letters-by-frequency
  "Letters in words from least frequent to most."
  (apply str (map first
                  (sort-by second
                           (frequencies (s/join words))))))

(def palindrome?
  "True if sentence is a palindrome.  False otherwise."
  (fn [sentence]
    (let [letters (s/join sentence)]
      (= letters (s/reverse letters)))))

(defn trie-add
  "Add prefixes from word to trie with terminal {:$ word}."
  [trie word]
  (assoc-in trie word (merge (get-in trie word) {:$ word})))

(comment "Use a trie to find all 2-word palindromes: 'truce curt'."

         (let [words ["bat" "bats" "bet" "batch" "banana" "band"]]
           (reduce trie-add {} words))

         {\b
          {\e {\t {:$ "bet"}},
           \a
           {\n {\d {:$ "band"},
                \a {\n {\a {:$ "banana"}}}},
            \t {:$ "bat",
                \s {:$ "bats"},
                \c {\h {:$ "batch"}}}}}}

         "Quickly find 'bats' to pair with 'tab'.")

(defn trie-match [trie word]
  "Sequence of words matching word in trie."
  (keep :$ (tree-seq map? vals (get-in trie word))))

(def pairs
  "All 2-word palindromes in words."
  (let [trie (reduce trie-add {} words)]
    (letfn [(heads [tail] (trie-match trie (s/reverse tail)))
            (flips [word] (map vector (heads word) (repeat word)))]
      (filter palindrome? (mapcat flips words)))))

(defn twin?
  "True if pal is a twin palindrome, such as 'avid diva'."
  [pal]
  (let [[avid diva] pal]
    (and avid diva (== (count avid) (count diva)))))

(defn score-pal
  "Golf score the palindrome pal on its letter coverage."
  [pal]
  (let [s (reduce str pal)
        m {:pal pal :twin? (twin? pal) :letters (set s)}
        score (/ (count s) (count (:letters m)) (if (:twin? m) 2 1))]
    (assoc m :score score)))

(def scores
  "All 1- and 2-word palindromes sorted by golf score."
  (let [singles (map vector (filter palindrome? words))]
    (sort-by :score (map score-pal (lazy-cat pairs singles)))))

(defn add-letter
  "A new palindrome around pal containing the letter c."
  [pal c]
  (letfn [(has-letter? [score] (contains? (:letters score) c))]
    (let [s (first (filter has-letter? scores))
          p (:pal s)]
      (vec (if (:twin? s)
             (cons (first p) (conj pal (second p)))
             (concat p pal p))))))

(defn pangramit
  "Wrap kernel palindrome pal until it is pangrammatic."
  [pal]
  (let [need (remove (set (s/join pal)) letters-by-frequency)]
    (if (empty? need) pal
        (recur (add-letter pal (first need))))))

(defn make-panpals
  "A vector of palindromic pangrams built center out to ends."
  []
  (loop [kernels (map :pal scores)
         panpals []]
    (if (empty? kernels) panpals
        (recur (rest kernels)
               (conj panpals (pangramit (first kernels)))))))

(defn score-fewest-letters
  "Score the palindromic pangram in panpals with the fewest letters."
  [panpals]
  (letfn [(score [pp] {:letters (count (s/join pp))
                       :words (count pp)
                       :panpal pp})]
    (let [count-letters (fn [pp] (count (s/join pp)))
          sorted (sort-by count-letters panpals)
          n (:letters (score (first sorted)))]
      (map score (take-while #(== n (:letters (score %))) sorted)))))

(defn -main
  [& args]
  (try (p/pprint (score-fewest-letters (time (make-panpals))))
       (catch Throwable x (println "Oops:" x))))

["Elapsed time: 2983.316 msecs"
 {:letters 83,:words 26,
  :panpal ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
           "suq" "us" "raj" "tack" "cat" "jar" "suq" "us"
           "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]}
 {:letters 83,:words 26,
  :panpal ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
           "suq" "us" "raj" "tuck" "cut" "jar" "suq" "us"
           "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]}]
