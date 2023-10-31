(ns birthdays.config
  (:require [omniconf.core :as omni]
            [hiccup2.core :as hiccup]))

(defn load-config []
  (omni/enable-functions-as-defaults)
  (omni/define
    {:sender-email   {:description "The (Gmail) email address from which emails are sent."
                      :type        :string
                      :required    true}
     :gmail-password {:description "The Google App Password associated with :sender-email."
                      :type        :string
                      :required    true}
     :cc-sender      {:description "Should the sender also be copied on the email? Can serve as a useful reminder that a server sent someone an automated email on your behalf."
                      :type        :boolean
                      :required    false
                      :default     false}
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
