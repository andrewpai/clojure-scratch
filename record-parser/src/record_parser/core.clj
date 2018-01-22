(ns record-parser.core
  (:require [clojure.string :as st]))

(defn whitespace? [ch]
  (re-find #"[\s]" ch))

;; checks for some exception conditions with quotes
(defn quote-in-value? [parsedfield quotechar in-quote new-in-quote testchar]

  (let [in-field (> (count parsedfield) 0)]
    (or
      ;; can't have a quote mid-field
      (and in-field (and (not in-quote) new-in-quote))

      ;; can't have a value character after a quoted string's been closed
      (and in-field (= (subs parsedfield 0 1) quotechar) (not (whitespace? testchar))))))

;; looks for missing closing quote
(defn end-quote-missing? [charlist new-in-quote]
  (and new-in-quote (= (count charlist) 1)))

(defn charparse- 

  ;; called from the main parse function with external params
  ([charlist separator quotechar escapechar]
    (charparse- [] "" charlist separator quotechar escapechar false))

  ;; recursive call that carries the current output record, the field currently being built and the in-quote flag
  ([parsedrec parsedfield charlist separator quotechar escapechar in-quote]
    (if (empty? charlist)

      ;; when the input string is done, add the field being parsed to the output record and return it
      (conj parsedrec parsedfield)

      (let [fc (first charlist)
            is-quotechar (= fc quotechar)
            new-in-quote (= in-quote (not is-quotechar))
            field-sep (and (not new-in-quote) (= fc separator))]

        ;; handle quoting exceptions. 

        (if (quote-in-value? parsedfield quotechar in-quote new-in-quote fc)
          (throw (Exception. (str "Found quote character in middle of field " (inc (count parsedrec))))))

          ;; catch end-of-record while still in a quoted string
          (if (end-quote-missing? charlist new-in-quote)
            (throw (Exception. (str "Missing close quote.")))
          
            ;; good to go. if the current character is a field separator, add the current field value to the output record
            (let [newrec (if field-sep (conj parsedrec parsedfield) parsedrec)
                  newfield (if field-sep "" (if (or is-quotechar (and (= (count parsedfield) 0) (whitespace? fc)))
                                               parsedfield 
                                               (str parsedfield fc)))]

              (recur newrec newfield (rest charlist) separator quotechar escapechar new-in-quote)))))))

(defn parse [rec] (charparse- (st/split rec #"") "," "\"" "\\"))

