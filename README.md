# Usage example

```clojure
(css/->style
 {:h3    {:font-family    :inherit
          :font-size      :2.5vh
          :color          :red
          :!margin-bottom :2vh}
  :label {:color       :#000
          :font-weight :600}
  :.row  {:display         :flex
          :flex-direction  :row
          :flex-wrap       :nowrap
          :min-height      :4vh
          :height          :4vh
          :padding         :1vh
          :align-items     :center
          :margin          :0.5vh
          :justify-content :space-between}})
```

## Install

```edn
{:deps {veschin/css-to-edn {:git/url "https://github.com/veschin/edn-to-css.git"
                            :sha     "69b87d54bf60077bbacc6c9720968baf7c021a91"}}}
```

## Require

```clojure
(ns _
 (:require css))
```


## Possible usage

```clojure
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
        :div {:width  :1
              :nested {:.a {:color :red}
                       :.b {:color :green}}}})
    (str "span {color: white;}\ndiv {width: 1;}\n"
         "div .a {color: red;}\n div .b {color: green;}")))
```
