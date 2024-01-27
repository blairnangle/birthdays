(ns birthdays
  (:require [clojure.data.json :as json]
            [postal.core :as postal]
            [config :as config]
            [clj-time.core :as time]
            [clojure.walk :as walk]
            [clojure.tools.logging :as logging]
            [clojure.string :as string]
            [hiccup2.core :as hiccup]))

(def n-sent (atom 0))

(defn- greeting
  [occasion name]
  (cond
    (= occasion :birthday) (str "Happy birthday, " name "!")
    (= occasion :anniversary) (str "Happy anniversary, " name "!")))

(defn- body
  [greeting]
  (str
    (hiccup/html
      [:div {:class "email"}
       [:p greeting]])
    (config/get-config :footer)))

(defn- choose-outgoing-mail-server [provider]
  (cond
    (= "gmail" provider) "smtp.gmail.com"
    (= "zoho" provider) "smtp.zoho.eu"))

(defn- send!
  [{:keys [emails name occasion]}]
  (let [sender-email (config/get-config :sender-email)
        password (config/get-config :password)
        greeting (greeting (keyword occasion) name)
        email {:from    sender-email
               :to      emails
               :subject greeting
               :body    [{:type    "text/html; charset=utf-8"
                          :content (body greeting)}]
               }]
    (try
      (postal/send-message
        {:host (choose-outgoing-mail-server (config/get-config :email-provider))
         :user sender-email
         :pass password
         :port 587
         :tls  true
         }
        (if
          (and
            (config/get-config :cc)

            ; handle the empty-string case because this is what `${{ secrets.CC }}` will evaluate to in GitHub Actions if a `CC` secret is not set
            (not= "" (config/get-config :cc)))
          (assoc email :cc (string/split (config/get-config :cc) #","))
          email))
      (swap! n-sent inc)
      (catch Exception e (str "Email to " (string/join ", " emails) " was not sent due to exception " (.getMessage e))
                         (logging/error (str e))))))

(defn- happening-today
  [occasions]
  (let [today (subs (str (time/today)) 5 10)]
    (filter #(= (subs (get % "date") 5 10) today) occasions)))

(defn -main
  [& _args]
  (config/load-config)
  (let [occasions (json/read-str (slurp (config/get-config :input-file)))
        today (happening-today occasions)]
    (run! send! (walk/keywordize-keys today))
    (logging/info (str @n-sent (if (= @n-sent 1) " email" " emails") " sent!"))))
