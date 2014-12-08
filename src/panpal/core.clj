(ns panpal.core
  (:require [clojure.java.io :as io]
            [clojure.pprint  :as pprint]
            [clojure.string  :as string])
  (:gen-class))


;; A 'word' is a string of lowercase letters: "word"
;; A 'sentence' is a vector of words: ["a" "vector" "of" "words"]


(defn palindrome?
  "True if sentence is a palindrome.  False otherwise."
  [sentence]
  (let [letters (string/join sentence)]
    (= letters (string/reverse letters))))


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

(defn trie-match [trie w]
  "Sequence of words matching w in trie."
  (keep :$ (tree-seq map? vals (get-in trie w))))

(defn find-2-word-palindromes
  "The 2-word palindromes in words."
  [words]
  (let [trie (reduce trie-add {} words)]
    (letfn [(heads [tail] (trie-match trie (string/reverse tail)))
            (flips [word] (map vector (heads word) (repeat word)))]
      (filter palindrome? (mapcat flips words)))))


(defn twin?
  "True if pal is a twin palindrome, such as 'avid diva'."
  [pal]
  (let [[avid diva] pal]
    (and avid diva (== (count avid) (count diva)))))

(defn score-a-kernel
  "Golf score the palindrome kernel on its letter coverage."
  [kernel]
  (let [twin (twin? kernel)
        s (reduce str kernel)
        letters (set s)]
    {:pal     kernel
     :twin?   twin
     :letters letters
     :score   (/ (count s) (count letters) (if twin 2 1))}))

(defn score-kernels
  "All 1- and 2-word palindromes sorted by their kernel scores."
  [words]
  (let [singles (map vector (filter palindrome? words))
        pairs (find-2-word-palindromes words)]
    (sort-by :score (map score-a-kernel (lazy-cat singles pairs)))))

(defn add-letter
  "A palindrome around pal containing the letter c with scores."
  [scores pal c]
  (letfn [(has-letter? [score] (contains? (:letters score) c))]
    (let [kernel (first (filter has-letter? scores))
          more (:pal kernel)]
      (vec (if (:twin? kernel)
             (cons (first more) (conj pal (second more)))
             (concat more pal more))))))

(defn make-pangramit
  "Use scores and lbf to make kernel palindrome pal pangrammatic."
  [scores lbf]
  (fn [pal]
    (let [need (remove (set (string/join pal)) lbf)]
      (if (empty? need) pal
          (recur (add-letter scores pal (first need)))))))

(defn letters-by-frequency
  "Least frequent to most in words: jqxzwkvfybhgmpudclotnraise"
  [words]
  (reduce str (map first
                   (sort-by second
                            (frequencies (string/join words))))))

(defn make-palindromic-pangrams
  "A vector of palindromic pangrams built from words."
  [words]
  (let [scores (score-kernels words)
        pangramit (make-pangramit scores (letters-by-frequency words))]
    (loop [kernels (map :pal scores)
           panpals []]
      (if (empty? kernels) panpals
          (recur (rest kernels)
                 (conj panpals (pangramit (first kernels))))))))

(defn score-panpal-with-fewest-letters
  "Score the palindromic pangram in panpals with the fewest letters."
  [panpals]
  (letfn [(score [pp] {:letters (count (string/join pp))
                       :words (count pp)
                       :panpal pp})]
    (let [count-letters (fn [pp] (count (string/join pp)))
          sorted (sort-by count-letters panpals)
          n (:letters (score (first sorted)))]
      (map score (take-while #(== n (:letters (score %))) sorted)))))

(defn -main
  [& args]
  (try
    (let [word-lst (first args)]
      (pprint/pprint
       (score-panpal-with-fewest-letters
        (make-palindromic-pangrams
         (line-seq (io/reader (io/input-stream word-lst)))))))
    (catch Throwable x
      (println "Oops:" x))))

;; (time (-main "WORD.LST")) --==>> "Elapsed time: 8806.504 msecs"
;;
[{:letters 83,
  :words 26,
  :panpal ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
           "suq" "us" "raj" "tack" "cat" "jar" "suq" "us"
           "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]}
 {:letters 83,
  :words 26,
  :panpal ["ma" "regna" "ha" "ya" "fila" "diva" "swob" "zaps" "xis"
           "suq" "us" "raj" "tuck" "cut" "jar" "suq" "us"
           "six" "spaz" "bows" "avid" "alif" "ay" "ah" "anger" "am"]}]
