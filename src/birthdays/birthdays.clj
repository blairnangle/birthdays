(ns birthdays.birthdays
  (:require [clojure.data.json :as json]
            [postal.core :as postal]
            [omniconf.core :as cfg]
            [clj-time.core :as time]
            [clojure.walk :as walk]))

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

    (str greeting "\n\n" (cfg/get :footer))))

(defn- send!
  [{:keys [emails name occasion]}]
  (let [sender-email (cfg/get :sender-email)
        gmail-password (cfg/get :gmail-password)]
    (postal/send-message
      {:host "smtp.gmail.com"
       :user sender-email
       :pass gmail-password
       :port 587
       :tls  true}
      {:from    sender-email
       :to      emails
       :cc      (if (cfg/get :cc-sender) sender-email nil)
       :subject (subject (keyword occasion))
       :body    (body (keyword occasion) name)})))

(defn- happening-today
  [occasions]
  (filter #(= (subs (get % "date") 5 10) (subs (str (time/today)) 5 10)) occasions))

(defn -main
  [& args]
  (let [occasions (json/read-str (slurp (cfg/get :input-file)))
        today (happening-today occasions)]
    (cfg/populate-from-env)
    (cfg/verify)
    (run! send! (walk/keywordize-keys today))))
