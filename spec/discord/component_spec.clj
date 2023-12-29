(ns discord.component-spec
  (:require [discord.components.core :as sut]
            [speclj.core :refer :all]))

(defmacro test-hiccup [name expected hiccup]
  `(it ~name
     (should= ~expected (sut/<-hiccup ~hiccup))))

(describe "Discord Hiccup Components"

  (context "Fragment"
    (test-hiccup "empty" [] [:<>])

    (test-hiccup "one component"
      [{:style 1 :type 2}]
      [:<> [:button]])

    (test-hiccup "two components"
      [{:style 1 :type 2}
       {:style 1 :type 4}]
      [:<>
       [:button]
       [:input]])

    (test-hiccup "contains another fragment"
      [{:style 1 :type 2}]
      [:<>
       [:<>
        [:button]]])

    (test-hiccup "many nested fragments containing a button"
      [{:style 1 :type 2}]
      [:<>
       [:<>
        [:<>
         [:<>
          [:<>
           [:button]]]]]])
    )


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
      [:tr [:tr]]))

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
      [:button {:custom-id "bar"}])

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
      [:button#one {:id "two" :custom-id "three"}])

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
      [:button {:url "hello.world" :href "goodbye.world"}]))

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
      {:type 4 :style 1 :min_length 3 :max_length 500}
      [:input {:min-length 3 :max-length 500}])

    (test-hiccup ":length option assigns both min and max length"
      {:type 4 :style 1 :min_length 55 :max_length 55}
      [:input {:length 55}])

    (test-hiccup "min and max length take precedence over length"
      {:type 4 :style 1 :min_length 2 :max_length 100}
      [:input {:length 55 :min-length 2 :max-length 100}])

    (test-hiccup "assigns min-length by length and max-length by its own property"
      {:type 4 :style 1 :min_length 55 :max_length 100}
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
      [:input {:style "blah"}]))

  (context "Select Menu"

    (context "Option"
      (test-hiccup "blank option" {} [:option])

      (test-hiccup "includes label, description, value, and default"
        {:label       "Some Option"
         :description "Pick me!"
         :value       "some"
         :default     true}
        [:option {:label       "Some Option"
                  :description "Pick me!"
                  :value       "some"
                  :default     true}])

      (test-hiccup "default accepts truthy values"
        {:default true}
        [:option {:default "blah"}])

      (test-hiccup "default of false removes the property"
        {}
        [:option {:default false}])

      (test-hiccup "selected is an alias for default"
        {:default true}
        [:option {:selected :yuh}])

      (test-hiccup "default is preferred over selected"
        {}
        [:option {:selected :yuh :default nil}])

      (test-hiccup "label may be provided in the body"
        {:label "I'm a label"}
        [:option "I'm a label"])

      (test-hiccup "label option is preferred over the body"
        {:label "I'm a preferred label"}
        [:option {:label "I'm a preferred label"} "I'm a label"])

      (test-hiccup "selected class toggles option as default"
        {:default true}
        [:option.selected])

      (test-hiccup "default class toggles option as default"
        {:default true}
        [:option.default]))

    (context "Text"
      (test-hiccup "has no options"
        {:type 3 :options []}
        [:select])

      (test-hiccup "includes a placeholder and custom id"
        {:type 3 :options [] :custom_id "my_id" :placeholder "Fake text"}
        [:select#my_id {:placeholder "Fake text"}])

      (test-hiccup "provides min and max values"
        {:type 3 :options [] :min_values 2 :max_values 4}
        [:select {:min-values 2 :max-values 4}])

      (test-hiccup ":values assigns both min and max values"
        {:type 3 :options [] :min_values 3 :max_values 3}
        [:select {:values 3}])

      (test-hiccup "min and max values overrides values property"
        {:type 3 :options [] :min_values 3 :max_values 3}
        [:select {:values 5 :min-values 3 :max-values 3}])

      (test-hiccup "can be disabled"
        {:type 3 :options [] :disabled true}
        [:select {:disabled "blah"}])

      (test-hiccup "contains one option in body"
        {:type 3 :options [{:label "Big" :value "big"}]}
        [:select
         [:option {:value "big"} "Big"]])

      (test-hiccup "contains two options in body"
        {:type 3 :options [{:label "Big" :value "big"}
                           {:label "Smol" :value "smol" :default true}]}
        [:select
         [:option {:value "big"} "Big"]
         [:option.selected {:value "smol"} "Smol"]])

      (test-hiccup "specifies options by attribute"
        {:type 3 :options [{:label "Big" :value "big"}
                           {:label "Smol" :value "smol" :default true}]}
        [:select {:options [{:label "Big" :value "big"}
                            {:label "Smol" :value "smol" :default ""}]}])

      (test-hiccup "specifies numeric type"
        {:type 3 :options []}
        [:select {:type 3}])

      (test-hiccup "specifies type of text"
        {:type 3 :options []}
        [:select {:type "text"}])

      (test-hiccup "specifies type of :text"
        {:type 3 :options []}
        [:select {:type :text}])

      (test-hiccup "specifies type by text class"
        {:type 3 :options []}
        [:select.text]))

    (context "User"
      (test-hiccup "empty"
        {:type 5}
        [:select {:type :user}])

      (test-hiccup "specifies type by string"
        {:type 5}
        [:select {:type "user"}])

      (test-hiccup "specifies type by number"
        {:type 5}
        [:select {:type 5}])

      (test-hiccup "includes custom_id, placeholder, min_values, max_values, and disabled properties"
        {:type        5
         :disabled    true
         :custom_id   "foo"
         :min_values  2
         :max_values  4
         :placeholder "Optional temporary text"}
        [:select#foo.disabled {:type        :user
                               :min-values  2
                               :max-values  4
                               :placeholder "Optional temporary text"}])

      (test-hiccup "selects type by user class"
        {:type 5}
        [:select.user]))

    (context "Role"
      (test-hiccup "empty"
        {:type 6}
        [:select {:type :role}])

      (test-hiccup "specifies type by string"
        {:type 6}
        [:select {:type "role"}])

      (test-hiccup "specifies type by number"
        {:type 6}
        [:select {:type 6}])

      (test-hiccup "includes custom_id, placeholder, min_values, max_values, and disabled properties"
        {:type        6
         :disabled    true
         :custom_id   "foo"
         :min_values  2
         :max_values  4
         :placeholder "Optional temporary text"}
        [:select#foo.disabled {:type        :role
                               :min-values  2
                               :max-values  4
                               :placeholder "Optional temporary text"}])

      (test-hiccup "selects type by role class"
        {:type 6}
        [:select.role]))

    (context "Mentionable"
      (test-hiccup "empty"
        {:type 7}
        [:select {:type :mentionable}])

      (test-hiccup "specifies type by string"
        {:type 7}
        [:select {:type "mentionable"}])

      (test-hiccup "specifies type by number"
        {:type 7}
        [:select {:type 7}])

      (test-hiccup "includes custom_id, placeholder, min_values, max_values, and disabled properties"
        {:type        7
         :disabled    true
         :custom_id   "foo"
         :min_values  2
         :max_values  4
         :placeholder "Optional temporary text"}
        [:select#foo.disabled {:type        :mentionable
                               :min-values  2
                               :max-values  4
                               :placeholder "Optional temporary text"}])

      (test-hiccup "selects type by mentionable class"
        {:type 7}
        [:select.mentionable]))

    (context "Channel"
      (test-hiccup "empty"
        {:type 8}
        [:select {:type :channel}])

      (test-hiccup "specifies type by string"
        {:type 8}
        [:select {:type "channel"}])

      (test-hiccup "specifies type by number"
        {:type 8}
        [:select {:type 8}])

      (test-hiccup "includes custom_id, placeholder, min_values, max_values, and disabled properties"
        {:type        8
         :disabled    true
         :custom_id   "foo"
         :min_values  2
         :max_values  4
         :placeholder "Optional temporary text"}
        [:select#foo.disabled {:type        :channel
                               :min-values  2
                               :max-values  4
                               :placeholder "Optional temporary text"}])

      (test-hiccup "selects type by channel class"
        {:type 8}
        [:select.channel])

      (test-hiccup "no channel types"
        {:type 8 :channel_types []}
        [:select.channel {:channel-types []}])

      (test-hiccup "numeric channel types"
        {:type 8 :channel_types [1 2 3]}
        [:select.channel {:channel-types [1 2 3]}])

      (test-hiccup "numeric, keyword, and string channel types"
        {:type 8 :channel_types [10 3 1]}
        [:select.channel {:channel-types [10 "group-dm" :dm]}])

      (test-hiccup "channels is an alias for channel-types"
        {:type 8 :channel_types [2 4 6]}
        [:select.channel {:channels [2 4 6]}])

      (test-hiccup "class names may be used to specify channel types"
        {:type 8 :channel_types [3 1 15]}
        [:select.channel.group-dm.dm.guild-forum])

      (test-hiccup "includes channels from class, channel-types, and channels attributes"
        {:type 8 :channel_types [0 2 1]}
        [:select.channel.guild-text {:channels [1] :channel-types [2]}])

      (test-hiccup "duplicates are excluded"
        {:type 8 :channel_types [1 3 4 2]}
        [:select.channel.dm {:channels [1 1 2 3] :channel-types [1 1 3 4]}])

      (test-hiccup "invalid entries are removed"
        {:type 8 :channel_types []}
        [:select.channel {:channels [:foo "bar" nil]}]))))
