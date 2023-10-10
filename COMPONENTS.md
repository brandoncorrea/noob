# Interaction Components

## Action Row Container for other components

````json
{
  "type": 1,
  "components": [
    {
      "type": 2,
      "label": "I'm a button!",
      "style": 1,
      "custom_id": "fancy_button"
    }
  ]
}
````

````clojure
[:tr
 [:button#fancy_button.primary "I'm a button!"]]
````

## Button

````json
{
  "type": 2,
  "style": 1,
  "label": "Optional Button Label",
  "custom_id": "optional_id",
  "url": "https://www.optional.com",
  "disabled": false
}
````

````clojure
[:button#primary_btn.primary "Primary Button with a custom_id"]
[:button.primary {:id "primary_btn"} "Primary Button with a custom_id"]
[:button.secondary {:disabled true} "Secondary Button"]
[:button.secondary.disabled "Secondary Button"]
[:button.success "Happy Button"]
[:button.danger "Sad Button"]
[:button.link {:href "https://www.google.com"} "Go to Google"]
````

## Text Input

````json
{
  "type": 4,
  "custom_id": "required_id",
  "style": 1,
  "label": "What is your favorite color?",
  "min_length": 0,
  "max_length": 4000,
  "required": false,
  "value": "Prefilled text",
  "placeholder": "Kinda prefilled text, but not really"
}
````

````clojure
[:input#required_id.short {:min-length 0
                           :max-length 4000
                           :required false
                           :value "Prefilled text"
                           :placeholder "Fill me in!"}
 "What is your favorite color?"]

[:input#big_textbox.short "What is your favorite color?"]
[:input#big_textbox.short.optional "What is your favorite color?"]
[:input#big_textbox.paragraph "What is your favorite color?"]
[:input#big_textbox.paragraph.optional "What is your favorite color?"]
````


## Select Menu for picking from defined text options

````json
{
  "type": 3,
  "custom_id": "required_id",
  "placeholder": "This is just temporary and optional",
  "min_values": 0,
  "max_values": 25,
  "disabled": true,
  "options": [
    {
      "label": "First Choice",
      "value": "1",
      "description": "I am an optional description",
      "default": false
    },
    {
      "label": "Second Choice",
      "value": "2",
      "description": "I am an optional description",
      "default": true
    }
  ]
}
````

````clojure
[:select#required_id
 {:placeholder "This is just temporary and optional"
  :min-values  0
  :max-values  25
  :disabled    true}
 [:option {:description "I am an optional description"
           :value       "1"}
  "First Choice"]
 [:option {:description "I am an optional description"
           :value       "2"
           :selected    true}
  "Second Choice"]]

[:select {:id      "some_id"
          :options [{:value "big" :label "Big"}
                    {:value "smol" :label "Smol"}]}]


[:select {:id "some_id" :type 3}
 [:option {:value "big"} "Big"]
 [:option {:value "smol"} "Smol"]]

[:select {:id "some_id" :type 3}
 [:option {:value "big"} "Big"]
 [:option {:value "smol"} "Smol"]]


[:select#some_id.text
 [:option {:value "big"} "Big"]
 [:option {:value "smol"} "Smol"]]

[:select#some_id
 [:option {:value    "big"
           :selected true} "Big"]
 [:option {:value "smol"} "Smol"]]

[:select#some_id.disabled
 [:option.selected {:value "big"} "Big"]
 [:option {:value "smol"} "Smol"]]
````

## User Select menu for users

````json
{
  "type": 5,
  "custom_id": "required_id",
  "placeholder": "Optional temporary text",
  "min_values": 0,
  "max_values": 25,
  "disabled": true
}
````

````clojure
[:select#user_select {:type        :user
                      :placeholder "Optional temporary text"
                      :min-values  0
                      :max-values  25
                      :disabled    true}]

[:select {:id "user_select" :type 5}]
[:select#user_select {:type 5}]
[:select#user_select {:type :user}]
[:select#user_select.user]
[:select#user_select.user.disabled]
````

## Role Select menu for roles

````json
{
  "type": 6,
  "custom_id": "required_id",
  "placeholder": "Optional temporary text",
  "min_values": 0,
  "max_values": 25,
  "disabled": true
}
````

````clojure
[:select#role_select {:type        :role
                      :placeholder "Optional temporary text"
                      :min-values  0
                      :max-values  25
                      :disabled    true}]

[:select {:id "role_select" :type 6}]
[:select#role_select {:type 6}]
[:select#role_select {:type :role}]
[:select#role_select.role]
[:select#mentionable_select.role.disabled]
````

## Mentionable Select menu for mentionables (users and roles)

````json
{
  "type": 7,
  "custom_id": "required_id",
  "placeholder": "Optional temporary text",
  "min_values": 0,
  "max_values": 25,
  "disabled": true
}
````

````clojure
[:select#mentionable_select {:type        :mentionable
                             :placeholder "Optional temporary text"
                             :min-values  0
                             :max-values  25
                             :disabled    true}]

[:select {:id "mentionable_select" :type 7}]
[:select#mentionable_select {:type 7}]
[:select#mentionable_select {:type :mentionable}]
[:select#mentionable_select.mentionable]
[:select#mentionable_select.mentionable.disabled]
````

## Channel Select menu for channels

````json
{
  "type": 8,
  "custom_id": "required_id",
  "channel_types": [
    
  ],
  "placeholder": "Optional Temporary Text",
  "min_values": 0,
  "max_values": 25,
  "disabled": true
}
````

````clojure
[:select#channel_select {:type          :channel
                         :placeholder   "Optional Temporary Text"
                         :min-values    0
                         :max-values    25
                         :disabled      true
                         :channel-types [:dm :guild-text 10 15]}]

[:select {:id "channel_select" :type 8}]
[:select#channel_select {:type :channel}]
[:select#channel_select.channel]
[:select#channel_select.channel.disabled]
````

### Channel Types

````clojure
{
 :guild-text          0
 :dm                  1
 :guild-voice         2
 :group-dm            3
 :guild-category      4
 :guild-announcement  5
 :announcement-thread 10
 :public-thread       11
 :private-thread      12
 :guild-stage-voice   13
 :guild-directory     14
 :guild-forum         15
 }
````
