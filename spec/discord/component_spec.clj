(ns discord.component-spec
  (:require [discord.component :as sut]
            [speclj.core :refer :all]))

(defmacro test-hiccup [name expected hiccup]
  `(it ~name
     (should= ~expected (sut/<-hiccup ~hiccup))))

(describe "Discord Hiccup Components"

  (context "Action Row"
    (test-hiccup "empty row"
      {:type 1 :components []}
      [:tr])

    (test-hiccup "contains one button"
      {:type 1 :components [{:type 2 :style 1 :label "Hello!"}]}
      [:tr [:button "Hello!"]])

    (test-hiccup "contains two buttons"
      {:type       1
       :components [{:type 2 :style 1 :label "Hello!"}
                    {:type 2 :style 1 :label "Goodbye!"}]}
      [:tr
       [:button "Hello!"]
       [:button "Goodbye!"]])

    (test-hiccup "contains another row"
      {:type 1 :components [{:type 1 :components []}]}
      [:tr [:tr]])
    )

  (context "Buttons"
    (test-hiccup "blank" {:type 2 :style 1} [:button])

    (test-hiccup "labeled body"
      {:type 2 :style 1 :label "Hello, world!"}
      [:button "Hello, world!"])

    (test-hiccup "labeled body with options"
      {:type 2 :style 2 :label "Hello, world!"}
      [:button {:style 2} "Hello, world!"])

    (test-hiccup "label option takes precedence over body"
      {:type 2 :style 1 :label "Goodbye cruel world!"}
      [:button {:label "Goodbye cruel world!"} "Hello, world!"])

    (test-hiccup "with an id"
      {:type 2 :style 1 :custom_id "foo"}
      [:button {:id "foo"}])

    (test-hiccup "with a custom_id attribute"
      {:type 2 :style 1 :custom_id "bar"}
      [:button {:custom_id "bar"}])

    (test-hiccup "primary class"
      {:type 2 :style 1}
      [:button {:class "primary"}])

    (test-hiccup "secondary class"
      {:type 2 :style 2}
      [:button {:class "secondary"}])

    (test-hiccup "success class"
      {:type 2 :style 3}
      [:button {:class "success"}])

    (test-hiccup "danger class"
      {:type 2 :style 4}
      [:button {:class "danger"}])

    (test-hiccup "link class"
      {:type 2 :style 5}
      [:button {:class "link"}])

    (test-hiccup "fake 'classes' are not allowed"
      {:type 2 :style 1}
      [:button {:class "blah"}])

    (test-hiccup "disabled class"
      {:type 2 :style 1 :disabled true}
      [:button {:class "disabled"}])

    (test-hiccup "disabled secondary class"
      {:type 2 :style 2 :disabled true}
      [:button {:class "disabled secondary"}])

    (test-hiccup "disabled option is truthy"
      {:type 2 :style 1 :disabled true}
      [:button {:disabled "foo"}])

    (test-hiccup "disabled option is falsy"
      {:type 2 :style 1}
      [:button {:disabled nil}])

    (test-hiccup "danger style"
      {:type 2 :style 4}
      [:button {:style "danger"}])

    (test-hiccup "fake 'styles' are allowed"
      {:type 2 :style "fake"}
      [:button {:style "fake"}])

    (test-hiccup "disabled style resolves to disabled and does not mark component as disabled"
      {:type 2 :style "disabled"}
      [:button {:style "disabled"}])

    (test-hiccup "keyword styles resolve to strings"
      {:type 2 :style "disabled"}
      [:button {:style :disabled}])

    (test-hiccup "keyword styles refer to their numeric counterparts"
      {:type 2 :style 2}
      [:button {:style :secondary}])

    (test-hiccup "can assign numeric styles"
      {:type 2 :style 2}
      [:button {:style 2}])

    (test-hiccup "can assign numeric styles"
      {:type 2 :style 2}
      [:button {:style 2}])

    (test-hiccup "href becomes url"
      {:type 2 :style 1 :url "example.com"}
      [:button {:href "example.com"}])

    (test-hiccup "shortcuts custom_id using CSS selector"
      {:type 2 :style 1 :custom_id "my_btn"}
      [:button#my_btn])

    (test-hiccup "contains all of :id, :custom_id, and CSS id selector"
      {:type 2 :style 1 :custom_id "three two one"}
      [:button#one {:id "two" :custom_id "three"}])

    (test-hiccup "contains id and class selector"
      {:type 2 :style 2 :custom_id "special_id"}
      [:button#special_id.secondary])

    (test-hiccup "contains many ids and classes"
      {:type 2 :style 2 :custom_id "one two three" :disabled true}
      [:button#one#two.secondary#three.disabled])

    (test-hiccup "multiple classes in a row"
      {:type 2 :style 2 :disabled true}
      [:button.secondary.disabled])

    (test-hiccup "prefers url over href"
      {:type 2 :style 1 :url "hello.world"}
      [:button {:url "hello.world" :href "goodbye.world"}])
    )

  (context "Text Input"
    (test-hiccup "empty"
      {:type 4 :style 1}
      [:input])

    (test-hiccup "has a label in the body"
      {:type 4 :style 1 :label "What is your favorite color?"}
      [:input "What is your favorite color?"])

    (test-hiccup "has a label in as an option"
      {:type 4 :style 1 :label "What is your favorite color?"}
      [:input {:label "What is your favorite color?"}])

    (test-hiccup "label option takes precedence"
      {:type 4 :style 1 :label "What is your favorite color?"}
      [:input {:label "What is your favorite color?"} "Blue!"])

    (test-hiccup "min and max length"
      {:type 4 :style 1 :min-length 3 :max-length 500}
      [:input {:min-length 3 :max-length 500}])

    (test-hiccup ":length option assigns both min and max length"
      {:type 4 :style 1 :min-length 55 :max-length 55}
      [:input {:length 55}])

    (test-hiccup "min and max length take precedence over length"
      {:type 4 :style 1 :min-length 2 :max-length 100}
      [:input {:length 55 :min-length 2 :max-length 100}])

    (test-hiccup "assigns min-length by length and max-length by its own property"
      {:type 4 :style 1 :min-length 55 :max-length 100}
      [:input {:length 55 :max-length 100}])

    (test-hiccup "contains value and placeholder"
      {:type 4 :style 1 :value "my-val" :placeholder "Enter something!"}
      [:input {:value "my-val" :placeholder "Enter something!"}])

    (test-hiccup "not required"
      {:type 4 :style 1 :required false}
      [:input {:required false}])

    (test-hiccup "required is falsy"
      {:type 4 :style 1 :required false}
      [:input {:required nil}])

    (test-hiccup "required is truthy"
      {:type 4 :style 1}
      [:input {:required "blah"}])

    (test-hiccup "not required via optional class"
      {:type 4 :style 1 :required false}
      [:input.optional])

    (test-hiccup "required option takes precedence over optional class"
      {:type 4 :style 1}
      [:input.optional {:required true}])

    (test-hiccup "includes a custom id"
      {:type 4 :style 1 :custom_id "inputty"}
      [:input#inputty])

    (test-hiccup "short class"
      {:type 4 :style 1}
      [:input.short])

    (test-hiccup "paragraph class"
      {:type 4 :style 2}
      [:input.paragraph])

    (test-hiccup "numeric style"
      {:type 4 :style 3}
      [:input {:style 3}])

    (test-hiccup "string style resolves to numeric style"
      {:type 4 :style 2}
      [:input {:style "paragraph"}])

    (test-hiccup "keyword style resolves to numeric style"
      {:type 4 :style 2}
      [:input {:style :paragraph}])

    (test-hiccup "fake styles are allowed"
      {:type 4 :style "blah"}
      [:input {:style "blah"}])
    )

  )
