(ns birthdays.birthdays
  (:require [clojure.data.json :as json]
            [postal.core :as postal]
            [birthdays.config :as config]
            [clj-time.core :as time]
            [clojure.walk :as walk]
            [clojure.tools.logging :as logging]
            [clojure.string :as string]))

(def n-sent (atom 0))

(defn- subject
  [occasion]
  (cond
    (= occasion :birthday) "Happy birthday!"
    (= occasion :wedding-anniversary) "Happy anniversary!"))

(defn- body
  [occasion name]
  (let [greeting (cond
                   (= occasion :birthday) (str "Happy birthday, " name "!")
                   (= occasion :anniversary) (str "Happy anniversary, " name "!"))]

    (str greeting "\n\n" (config/get-config :footer))))

(defn- send!
  [{:keys [emails name occasion]}]
  (let [sender-email (config/get-config :sender-email)
        gmail-password (config/get-config :gmail-password)]
    (try
      (postal/send-message
        {:host "smtp.gmail.com"
         :user sender-email
         :pass gmail-password
         :port 587
         :tls  true}
        {:from    sender-email
         :to      emails
         :cc      (if (config/get-config :cc-sender) sender-email nil)
         :subject (subject (keyword occasion))
         :body    (body (keyword occasion) name)})
      (swap! n-sent inc)
      (catch Exception e (str "Email to " (string/join ", " emails) " was not sent due to exception " (.getMessage e))
                         (logging/error (str e))))))

(defn- happening-today
  [occasions]
  (filter #(= (subs (get % "date") 5 10) (subs (str (time/today)) 5 10)) occasions))

(defn -main
  [& args]
  (config/load-config)
  (let [occasions (json/read-str (slurp (config/get-config :input-file)))
        today (happening-today occasions)]
    (run! send! (walk/keywordize-keys today))
    (logging/info (str @n-sent (if (= @n-sent 1) " email" " emails") " sent!"))))
