(ns record-parser.t-core
  (:require [midje.sweet :refer :all]
            [record-parser.core :as rp]))

(fact "Unquoted records"
    (rp/parse "abc") => [ "abc" ]
    (rp/parse "abc,def") => [ "abc" "def" ]
    (rp/parse "abc,,def") => [ "abc" "" "def" ]
    (rp/parse ",abc,def") => [ "" "abc" "def" ]
    (rp/parse "abc,def,") => [ "abc" "def" "" ])

(fact "Quoted records"
    (rp/parse "\"abc\"") => [ "abc" ]
    (rp/parse "abc,\"def\"") => [ "abc" "def" ]
    (rp/parse "\"abc\",def") => [ "abc" "def" ]
    (rp/parse "\"abc\",\"\",def") => [ "abc" "" "def" ])

;(fact "Quote exceptions"
;  (rp/parse "a\"b") =throws=> (Exception.)
;  (rp/parse "\"ab") =throws=> (Exception.)
;  (rp/parse "\"abc\"abc") =throws=> (Exception.))

