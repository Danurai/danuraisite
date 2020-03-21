(ns danuraisite.rklotrdata)

(def hobbits [
  {:plyr 1 :name "Frodo"  :clr "gold"   :corruption 0 :perm "" :ability "" :p-fn #{} :a-fn #{} :ringbearer true}
  {:plyr 2 :name "Sam"    :clr "red"    :corruption 0 :perm "" :ability "" :p-fn #{} :a-fn #{}}
  {:plyr 3 :name "Pippin" :clr "green"  :corruption 0 :perm "" :ability "" :p-fn #{} :a-fn #{}}
  {:plyr 4 :name "Merry"  :clr "blue"   :corruption 0 :perm "" :ability "" :p-fn #{} :a-fn #{}}
  {:plyr 5 :name "Fatty"  :clr "orange" :corruption 0 :perm "" :ability "" :p-fn #{} :a-fn #{}}
])

(def hobbit-cards
  (map-indexed #(assoc %2 :id %1)
    (reduce concat (for [ty [:fellowship :fight :hide :travel :wild] col [:white :lightgrey]]
      (repeat (if (= col :white) 7 5) (hash-map :icons [ty] :clr (if (= :wild ty) :white col) :deck :hobbit :name "Hobbit" :loc :deck))))))
     
(def feature-cards [  
  {:deck :rivendell :clr "yellow" :name "Athelas" :text "One player: Ignore any effects of missing Life tokens once only"}
  {:deck :rivendell :clr "yellow" :name "Miruvor" :text "One player: May pass 1 card to another player"}
  {:deck :rivendell :clr "yellow" :name "Staff"   :text "Ignore one tile showing a sundial and three items"}
  {:deck :rivendell :clr "yellow" :name "Mithril" :text "One player: Ignore effects after one die roll"}
  {:deck :rivendell :clr "grey"   :name "Sting"   :icons[:wild]}
  {:deck :rivendell :clr "grey"   :name "Glamdring" :icons [:wild :wild]}
  {:deck :rivendell :clr "grey"   :name "And\u00faril" :icons [:fight :fight]}
  {:deck :rivendell :clr "white"  :name "Gandalf" :icons [:travel :travel]}
  {:deck :rivendell :clr "white"  :name "Gimli"   :icons [:fight :fight]}
  {:deck :rivendell :clr "white"  :name "Legolas" :icons [:hide :hide]}
  {:deck :rivendell :clr "white"  :name "Aragorn" :icons [:wild :wild]}
  {:deck :rivendell :clr "white"  :name "Boromir" :icons [:fight :fight]}
])
    
(def events [
  {:name "Travel" :icons [:travel] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."} 
  {:name "Travel" :icons [:travel] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Travel" :icons [:travel] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Hiding" :icons [:hide] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Hiding" :icons [:hide] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Hiding" :icons [:hide] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Friendship" :icons [:friendship] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Friendship" :icons [:friendship] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Friendship" :icons [:friendship] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Fighting" :icons [:fight] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Fighting" :icons [:fight] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Fighting" :icons [:fight] :description "Advance the corresponding Activity by one space and carry out the Activity space you land on. If there is no such activity, or if the Activity is complete, advance the marker on another Activity. You must always advance one Activity."}
  {:name "Corruption" :icons [:ring :corrupt :corrupt] :description "The Ring-bearer's hobbit must move one step towards the darkness on the corruption line. Then reveal the next Event."}
  {:name "Corruption" :icons [:ring :corrupt :corrupt] :description "The Ring-bearer's hobbit must move one step towards the darkness on the corruption line. Then reveal the next Event."}
  {:name "Event or Discard 3 Cards" :icons [:event :card :card :card] :description "If you join together with the other players to discard 3 cards of any type between you then nothing happens. If not, the next event occurs. Then reveal the next Event." }
  {:name "Event or Discard Card Life Shield" :icons [:event :card :life :shield]} :description "If you join together with the other players to discard 1 card, 1 life token and 1 shield between you then nothing happens. If not, the next event occurs. Then reveal the next Event."
  {:name "Sauron or Corruption" :icons [:sauron :corrupt :corrupt] :description "One player must volunteer to advance their hobbit by two steps into the darkness on the Corruption line, or else Sauron moves one step towards the Hobbits. Then reveal the next Event."}
  {:name "Event" :icons [:event] :description "The next Event now occurs. After the Event, reveal the next Event."}
])

  
 ; Gandalf - Each player receive 6 hobbit cards
 ; Preparations - Ring Bearer may [roll] and reveal 4 Hobbit cards face-up for distribution
 ; Nazgul appears - One player discard 2 Travel or adv. Sauron
  