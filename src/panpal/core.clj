(ns panpal.core
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as comb]
            [clojure.pprint :as pp]
            [clojure.set :as set]
            [clojure.string :as string])
  (:gen-class))

(defn palindrome?
  "True if sentence is a palindrome.  False otherwise."
  [sentence]
  (let [letters (reduce str sentence)]
    (= letters (string/reverse letters))))

(def ^{:doc "All the words in the WORD.LST dictionary."}
  words
  (line-seq (io/reader (io/input-stream "WORD.LST"))))

(def letters-by-frequency
  ^{:doc "Letters from least frequent to most: jqxzwkvfybhgmpudclotnraise"}
  (let [freqs (frequencies (mapcat seq words))]
    (reduce str (map first (sort-by second freqs)))))

(def set-of-all-letters
  ^{:doc "The set of all letters in the alphabet."}
  (set letters-by-frequency))

(def singles
  ^{:doc "Single words that are themselves palindromes."}
  (filter palindrome? words))

(defn twin?
  "True if pal is a twin palindrome such as [\"avid\" \"diva\"]."
  [pal]
  (and (== 2 (count pal))
       (let [[left right] pal]
         (== (count left) (count right)))))

(defn trie-add
  "Add prefixes of word w to trie with terminal {:$ w}."
  [trie w]
  (assoc-in trie w (merge (get-in trie w) {:$ w})))

(comment "What is a trie?"

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

(defn trie-seq
  "The sequence of all words in trie."
  [trie]
  (keep :$ (tree-seq map? vals trie)))

(defn trie-match [trie w]
  "Sequence of words matching w in trie."
  (keep :$ (tree-seq map? vals (get-in trie w))))

(def ^{:doc "The trie of all words in the dictionary."}
  trie (reduce trie-add {} words))


(def ^{:doc "Two-word palindromes in words."}
  pairs
  (letfn [(heads [tail] (trie-match trie (string/reverse tail)))
          (flips [word] (map vector (heads word) (repeat word)))]
    (filter palindrome? (mapcat flips words))))

(defn golf-score-palindrome
  "Golf score palindrome pal on its letter coverage."
  [pal]
  (let [s (reduce str pal)
        total (count s)
        letters (set s)]
    {:pal     pal
     :letters letters
     :count   total
     :score   (/ total (count letters) (if (twin? pal) 2 1))}))

(def ^{:doc "All 2-word palindromes sorted by golf score."}
  scores
  (sort-by :score
           (map golf-score-palindrome
                (lazy-cat (map vector singles) pairs))))


(defn add-letter
  "A new palindrome around pal containing the letter c."
  [pal c]
  (letfn [(has-letter? [score] (contains? (:letters score) c))]
    (let [more (:pal (first (filter has-letter? scores)))]
      (println :add-letter {:more more :pal pal :c c})
      (vec (if (twin? more)
             (cons (first more) (conj pal (second more)))
             (concat more pal more))))))

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
    (println :improve-kernel {:kernel kernel :have have :need need})
    (if-let [c (first need)]
      (recur (add-letter kernel c))
      kernel)))

(defn make-palindromic-pangrams
  "A vector of palindromic pangrams built center out to ends."
  []
  (loop [kernels (map :pal scores) panpals []]
    ;; (println :make-palindromic-pangrams {:kernels kernels :panpals panpals})
    (if-let [k (first kernels)]
      (let [improved (improve-kernel k)]
        (if (not (palindrome? improved))
          (do (println :make-palindromic-pangrams {:improved improved})
              (throw (Exception. "OH NO!"))))
        (recur (rest kernels) (conj panpals improved)))
      panpals)))

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

;; (time (-main))

{:letters 83,
 :words 26,
 :panpal ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
          "suq" "us" "raj" "tack" "cat" "jar" "suq" "us"
          "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]}

;; "Elapsed time: 1326206.181 msecs"
