(ns discord.option-spec
  (:require [discord.option :as sut]
            [speclj.core :refer :all]))

(describe "Discord Options"

  (context "optional"

    (it "string"
      (let [{:keys [required type name description]} (sut/->string "Hello" "World!")]
        (should= false required)
        (should= 3 type)
        (should= "Hello" name)
        (should= "World!" description)))

    (it "int"
      (let [{:keys [required type name description]} (sut/->int "foo" "bar")]
        (should= false required)
        (should= 4 type)
        (should= "foo" name)
        (should= "bar" description)))

    (it "bool"
      (let [{:keys [required type name description]} (sut/->bool "bool" "ean")]
        (should= false required)
        (should= 5 type)
        (should= "bool" name)
        (should= "ean" description)))

    (it "user"
      (let [{:keys [required type name description]} (sut/->user "user" "luser")]
        (should= false required)
        (should= 6 type)
        (should= "user" name)
        (should= "luser" description)))

    (it "channel"
      (let [{:keys [required type name description]} (sut/->channel "cartoon" "network")]
        (should= false required)
        (should= 7 type)
        (should= "cartoon" name)
        (should= "network" description)))

    (it "role"
      (let [{:keys [required type name description]} (sut/->role "hawaiian" "roll")]
        (should= false required)
        (should= 8 type)
        (should= "hawaiian" name)
        (should= "roll" description)))

    (it "mentionable"
      (let [{:keys [required type name description]} (sut/->mentionable "men" "chin")]
        (should= false required)
        (should= 9 type)
        (should= "men" name)
        (should= "chin" description)))

    (it "double"
      (let [{:keys [required type name description]} (sut/->double "two" "dos")]
        (should= false required)
        (should= 10 type)
        (should= "two" name)
        (should= "dos" description)))

    (it "attachment"
      (let [{:keys [required type name description]} (sut/->attachment "cats" "jpg")]
        (should= false required)
        (should= 11 type)
        (should= "cats" name)
        (should= "jpg" description)))
    )

  (context "required"
    (it "string"
      (let [{:keys [required type name description]} (sut/->string! "Hello" "World!")]
        (should= true required)
        (should= 3 type)
        (should= "Hello" name)
        (should= "World!" description)))

    (it "int"
      (let [{:keys [required type name description]} (sut/->int! "bar" "foo")]
        (should= true required)
        (should= 4 type)
        (should= "bar" name)
        (should= "foo" description)))

    (it "bool"
      (let [{:keys [required type name description]} (sut/->bool! "bool" "ean")]
        (should= true required)
        (should= 5 type)
        (should= "bool" name)
        (should= "ean" description)))

    (it "user"
      (let [{:keys [required type name description]} (sut/->user! "user" "luser")]
        (should= true required)
        (should= 6 type)
        (should= "user" name)
        (should= "luser" description)))

    (it "channel"
      (let [{:keys [required type name description]} (sut/->channel! "cartoon" "network")]
        (should= true required)
        (should= 7 type)
        (should= "cartoon" name)
        (should= "network" description)))

    (it "role"
      (let [{:keys [required type name description]} (sut/->role! "hawaiian" "roll")]
        (should= true required)
        (should= 8 type)
        (should= "hawaiian" name)
        (should= "roll" description)))

    (it "mentionable"
      (let [{:keys [required type name description]} (sut/->mentionable! "men" "chin")]
        (should= true required)
        (should= 9 type)
        (should= "men" name)
        (should= "chin" description)))

    (it "double"
      (let [{:keys [required type name description]} (sut/->double! "two" "dos")]
        (should= true required)
        (should= 10 type)
        (should= "two" name)
        (should= "dos" description)))

    (it "attachment"
      (let [{:keys [required type name description]} (sut/->attachment! "cats" "jpg")]
        (should= true required)
        (should= 11 type)
        (should= "cats" name)
        (should= "jpg" description)))
    )
  )
