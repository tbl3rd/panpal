(ns panpal.core
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as comb]
            [clojure.pprint :as pp]
            [clojure.set :as set]
            [clojure.string :as str])
  (:gen-class))
 
(defn palindrome?
  "True iff sentence is a palindrome."
  [sentence]
  (let [letters (reduce str sentence)]
    (= letters (str/reverse letters))))
 
(def ^{:doc "All the words in the WORD.LST dictionary."}
  words
  (line-seq (io/reader (io/input-stream "WORD.LST"))))
 
(def letters-by-frequency
  ^{:doc "Letters from least frequent to most: jqxzwkvfybhgmpudclotnraise"}
  (let [freqs (frequencies (mapcat seq words))]
    (reduce str (map first (sort-by second freqs)))))
 
(def ^{:doc "Set of words whose palindromes are also in the dictionary."}
  twins
  (let [sdrow (map str/reverse words)]
    (loop [twins (set/intersection (into #{} words) (into #{} sdrow))
           result #{}]
      (if (empty? twins) result
          (let [left (first twins)]
            (recur (disj twins left (str/reverse left))
                   (conj result left)))))))
 
(comment
  "Here you find that twins don't contain all the letters you need."
  "You try to build more palindromes but problem is too big."
 
  (def pairs (filter palindrome? (comb/combinations words 2)))
 
  "A prefix tree (trie) or two might help.")
 
(defn trie-add
  "Add prefixes of word w to trie with terminal {:$ w}."
  [trie w]
  (assoc-in trie w (merge (get-in trie w) {:$ w})))
 
(defn trie-seq
  "The sequence of all words in trie."
  [trie]
  (keep :$ (tree-seq map? vals trie)))
 
(defn trie-match [trie w]
  "Sequence of words matching w in trie."
  (keep :$ (tree-seq map? vals (get-in trie w))))
 
(def ^{:doc "The trie of all words in the dictionary."}
  trie (reduce trie-add {} words))
 
(def ^{:doc "The trie of all words in the dictionary reversed."}
  eirt (reduce trie-add {} (map str/reverse words)))
 
(def ^{:doc "Two-word palindromes in words."}
  pairs
  (letfn [(tails [head] (trie-match trie (str/reverse head)))
          (heads [tail] (map str/reverse (trie-match eirt tail)))]
    (filter palindrome?
            (mapcat (fn [w] (mapcat (fn [w h t] [[w h] [w t]])
                                    (repeat w) (heads w) (tails w)))
                    words))))
 
(comment "Now there are twins that are their own palindromes reversed,"
         "and there are also pairs which are all 2-word palindromes.")
 
(defn golf-score-2-word-palindrome
  "Golf score 2-word palindromes on their letter coverage."
  [pal]
  (let [ls (reduce str pal) lset (set ls) n (count ls)]
    {:pal pal :letters lset :count n :score (/ n (count lset))}))
 
(def ^{:doc "All 2-word palindromes sorted by golf score."}
  pair-scores
  (sort-by :score (map golf-score-2-word-palindrome pairs)))
 
(def ^{:doc "All twins sorted by golf score."}
  twin-scores
  (sort-by :score (map golf-score-2-word-palindrome twins)))
 
(defn add-letter
  "A new palindrome around pal containing the letter c."
  [pal c]
  (letfn [(has-letter? [score] (contains? (:letters score) c))]
    (let [word (:pal (first (filter has-letter? twin-scores)))]
      (vec (cons word (conj pal (str/reverse word)))))))
 
(defn remove-any-of
  "Remove from s any letters in cs."
  [s & cs]
  (let [re (re-pattern (str "[" (reduce str cs) "]"))]
    (clojure.string/replace s re "")))
 
(defn improve-kernel
  "Improve kernel palindrome until it is pangrammatic."
  [kernel]
  (let [have (reduce str (set (mapcat seq kernel)))
        need (remove-any-of letters-by-frequency have)]
    (if-let [c (first need)]
      (recur (add-letter kernel c))
      kernel)))
 
(def ^{:doc "Set of letters that are not in twins."}
  not-in-twins
  (set/difference (into #{} (mapcat seq words))
                  (into #{} (mapcat seq twins))))
 
(defn make-palindromic-pangrams
  "A vector of palindromic pangrams built by adding twins around pairs."
  []
  (letfn [(missing [letters] (set/intersection not-in-twins letters))
          (no-score? [score] (empty? (missing (:letters score))))]
    (let [kernels (remove no-score? pair-scores)]
      (loop [kernels (map :pal kernels) panpals []]
        (if-let [k (first kernels)]
          (recur (rest kernels) (conj panpals (improve-kernel k)))
          panpals)))))
 
(defn score-panpal-with-fewest-letters
  "Score the palindromic pangram in panpals with the fewest letters."
  [panpals]
  (let [pp (first (sort-by (fn [pp] (count (mapcat seq pp))) panpals))]
    {:letters (count (mapcat seq pp)) :words (count pp) :panpal pp}))
 
(defn -main
  [& args]
  (try
    (pp/pprint
     (score-panpal-with-fewest-letters (make-palindromic-pangrams)))
    (catch Throwable x
      (println "Oops:" x))))
 
{:letters 97,
 :words   26,
 :panpal  ["serac" "sub" "mat" "big" "yah" "reflow" "saved" "korat" "new"
           "zaps" "six" "raj" "ta" "qat" "jar" "xis" "spaz"
           "wen" "tarok" "devas" "wolfer" "hay" "gib" "tam" "bus" "cares"]}
;; (-main)
