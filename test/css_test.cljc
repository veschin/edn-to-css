(ns css-test
  (:require [css]
            [clojure.test :as t]))

(t/deftest styles
  (t/testing "css->edn"
    (t/is (css/css->edn "color: red; padding: 1;")
          {:color   :red
           :padding :1}))
  (t/testing "->fns"
    (t/is (css/->fns {:translate [:50% :25%]})
          "translate(50%, 25%)"))
  (t/testing "type-cast"
    (t/is (css/type-cast :color) "color")
    (t/is (css/type-cast 'color) "color")
    (t/is (css/type-cast {:translate [:50% :25%]}) "translate(50%, 25%)")
    (t/is (css/type-cast [:a :b :c]) "a, b, c"))
  (t/testing "important"
    (t/is (css/important? "!color" "red")
          "!color: red;")
    (t/is (mapv css/prop<->value {:!color   :red
                                  :!padding :1})
          ["!color: red;" "!padding: 1;"]))
  (t/testing "tag<->props"
    (t/is (mapv css/tag<->props
                {:span {:color :white}
                 :div  {:width  :1
                        :nested {:.a {:color :red}
                                 :.b {:color :green}}}})
          ["span {color: white;}"
           "div {width: 1;}\ndiv .a {color: red;}\n div .b {color: green;}"]))
  (t/testing "->style"
    (t/is (css/->style {:span {:color :white}
                        :div  {:width  :1
                               :nested {:.a {:color :red}
                                        :.b {:color :green}}}})
          (str "span {color: white;}\ndiv {width: 1;}\n"
               "div .a {color: red;}\n div .b {color: green;}"))))
