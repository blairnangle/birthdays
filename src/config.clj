(ns config
  (:require [omniconf.core :as omni]
            [hiccup2.core :as hiccup]))

(defn load-config []
  (omni/enable-functions-as-defaults)
  (omni/define
    {:email-provider {:description "The email provider."
                      :type        :string
                      :required    true
                      :one-of      ["gmail" "zoho"]}
     :sender-email   {:description "The email address from which emails are sent."
                      :type        :string
                      :required    true}
     :password       {:description "The password for :sender-email. This is likely to be an app-specific password."
                      :type        :string
                      :required    true}
     :cc             {:description "Comma-separated (string) list of emails to cc (e.g., \"person1@mail.com,person2@mail.com\")"
                      :type        :string
                      :required    false}
     :footer         {:description "Comes at the end of each email."
                      :type        :string
                      :required    false
                      :default     #(str (hiccup/html
                                           [:p
                                            "This is an automated message. There is no option to unsubscribe. You can view the code that powered this message "
                                            [:a {:href "https://github.com/blairnangle/birthdays"} "here"]
                                            "."]))}
     :input-file     {:description "Database of occasions to read."
                      :type        :string
                      :required    false
                      :default     "occasions.json"}})
  (omni/populate-from-env)
  (omni/verify {:silent true}))

(defn get-config [& config-keys]
  (apply omni/get config-keys))
