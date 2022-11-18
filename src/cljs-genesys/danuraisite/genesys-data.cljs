(ns danuraisite.genesys-data)
;;{:xpspent 115, :name "Merch Korall", :species {:name "Human", :stats [2 2 2 2 2 2], :basexp 110, :health [{:name "Soak", :base 0, :stat "Br", :rank 2, :mod 0} {:name "Wounds", :base 10, :stat "Br", :rank 12, :mod 0} {:name "Strain", :base 10, :stat "Will", :rank 12, :mod 0}], :free_skills ["any" "any"]}, :free_skills ["any" "any"], :career {:name "Bounty Hunter", :skills ["Athletics" "Perception" "Piloting - Planetary" "Piloting - Space" "Skullduggery" "Vigilance" "Brawl" "Ranged - Heavy"], :spex [{:name "Assassin", :skills ["Melee" "Ranged - Heavy" "Skullduggery" "Stealth"], :talent-tree []} {:name "Gadgeteer", :skills ["Coercion" "Mechanics" "Brawl" "Ranged - Light"], :talent-tree [{:id 0, :name "Brace", :ap "A", :cost 5, :trained false} {:id 1, :name "Toughened", :ap "P", :cost 5, :trained false} {:id 2, :name "Intimidating", :ap "A", :cost 5, :trained false} {:id 3, :name "Defensive Stance", :ap "A", :cost 5, :trained false} {:id 4, :target 5, :name "Spare Clip", :ap "P", :cost 10, :trained false} {:id 5, :target 1, :name "Jury Rigged", :ap "P", :cost 10, :trained false} {:id 6, :target 5, :name "Point Blank", :ap "P", :cost 10, :trained false} {:id 7, :target 3, :name "Disorient", :ap "P", :cost 10, :trained false} {:id 8, :target 9, :name "Toughened", :ap "P", :cost 15, :trained false} {:id 9, :target 5, :name "Armor Master", :ap "P", :cost 15, :trained false} {:id 10, :target 9, :name "Natural Enforcer", :ap "A", :cost 15, :trained false} {:id 11, :target 7, :name "Stunning Blow", :ap "A", :cost 15, :trained false} {:id 12, :target 13, :name "Jury Rigged", :ap "P", :cost 20, :trained false} {:id 13, :target 9, :name "Tinkerer", :ap "P", :cost 20, :trained false} {:id 14, :target 13, :name "Deadly Accuracy", :ap "P", :cost 20, :trained false} {:id 15, :target 11, :name "Improved Stunning Blow", :ap "A", :cost 20, :trained false} {:id 16, :target 17, :name "Intimidating", :ap "A", :cost 25, :trained false} {:id 17, :target 13, :name "Dedication", :ap "p", :cost 25, :trained false} {:id 18, :target 17, :name "Improved Armor Master", :ap "p", :cost 25, :trained false} {:id 19, :target 15, :name "Crippling Blow", :ap "A", :cost 25, :trained false}]} {:name "Survivalist", :skills ["Xenology" "Perception" "Resilience" "Survival"], :talent-tree []}]}, :talents "Toughened (5) Jury Rigged (10) Armor Master (15)", :skills ({:name "Astrogation", :stat "Int", :free_rank false, :rank 0, :type "General"} {:name "Athletics", :stat "Br", :free_rank true, :rank 1, :type "General", :career "C"} {:name "Charm", :stat "Pr", :free_rank false, :rank 0, :type "General"} {:name "Coercion", :stat "Will", :free_rank false, :rank 0, :type "General", :career "X"} {:name "Computers", :stat "Int", :free_rank false, :rank 0, :type "General"} {:name "Cool", :stat "Pr", :free_rank false, :rank 0, :type "General"} {:name "Coordination", :stat "Ag", :free_rank false, :rank 0, :type "General"} {:name "Deception", :stat "Cun", :free_rank false, :rank 0, :type "General"} {:name "Discipline", :stat "Will", :free_rank false, :rank 0, :type "General"} {:name "Leadership", :stat "Pr", :free_rank false, :rank 0, :type "General"} {:name "Mechanics", :stat "Int", :free_rank false, :rank 0, :type "General", :career "X"} {:name "Medicine", :stat "Int", :free_rank true, :rank 1, :type "General"} {:name "Negotiation", :stat "Pr", :free_rank false, :rank 0, :type "General"} {:name "Perception", :stat "Cun", :free_rank false, :rank 1, :type "General", :career "C"} {:name "Piloting - Planetary", :stat "Ag", :free_rank true, :rank 1, :type "General", :career "X"} {:name "Piloting - Space", :stat "Ag", :free_rank false, :rank 0, :type "General", :career "C"} {:name "Resilience", :stat "Br", :free_rank false, :rank 0, :type "General"} {:name "Skullduggery", :stat "Cun", :free_rank false, :rank 0, :type "General", :career "C"} {:name "Stealth", :stat "Ag", :free_rank false, :rank 0, :type "General"} {:name "Streetwise", :stat "Cun", :free_rank false, :rank 0, :type "General"} {:name "Survival", :stat "Cun", :free_rank false, :rank 0, :type "General"} {:name "Vigilance", :stat "Will", :free_rank true, :rank 1, :type "General", :career "C"} {:name "Brawl", :stat "Br", :free_rank true, :rank 1, :type "Combat", :career "C"} {:name "Gunnery", :stat "Ag", :free_rank false, :rank 0, :type "Combat"} {:name "Melee", :stat "Br", :free_rank false, :rank 0, :type "Combat"} {:name "Ranged - Light", :stat "Ag", :free_rank true, :rank 1, :type "Combat", :career "X"} {:name "Ranged - Heavy", :stat "Ag", :free_rank true, :rank 2, :type "Combat", :career "C"} {:name "Core Worlds", :stat "Int", :free_rank false, :rank 0, :type "Knowledge"} {:name "Education", :stat "Int", :free_rank false, :rank 0, :type "Knowledge"} {:name "Lore", :stat "Int", :free_rank false, :rank 0, :type "Knowledge"} {:name "Outer Rim", :stat "Int", :free_rank false, :rank 0, :type "Knowledge"} {:name "Underworld", :stat "Int", :free_rank true, :rank 1, :type "Knowledge"} {:name "Xenology", :stat "Int", :free_rank false, :rank 0, :type "Knowledge"} {:name "Other", :stat "Int", :free_rank false, :rank 0, :type "Knowledge"}), :health ({:name "Soak", :base 0, :stat "Br", :rank 3, :mod 0} {:name "Wounds", :base 10, :stat "Br", :rank 15, :mod 2} {:name "Strain", :base 10, :stat "Will", :rank 12, :mod 0}), :xp 110, :spex {:name "Gadgeteer", :skills ["Coercion" "Mechanics" "Brawl" "Ranged - Light"], :talent-tree [{:id 0, :name "Brace", :ap "A", :cost 5, :trained false} {:id 1, :name "Toughened", :ap "P", :cost 5, :trained false} {:id 2, :name "Intimidating", :ap "A", :cost 5, :trained false} {:id 3, :name "Defensive Stance", :ap "A", :cost 5, :trained false} {:id 4, :target 5, :name "Spare Clip", :ap "P", :cost 10, :trained false} {:id 5, :target 1, :name "Jury Rigged", :ap "P", :cost 10, :trained false} {:id 6, :target 5, :name "Point Blank", :ap "P", :cost 10, :trained false} {:id 7, :target 3, :name "Disorient", :ap "P", :cost 10, :trained false} {:id 8, :target 9, :name "Toughened", :ap "P", :cost 15, :trained false} {:id 9, :target 5, :name "Armor Master", :ap "P", :cost 15, :trained false} {:id 10, :target 9, :name "Natural Enforcer", :ap "A", :cost 15, :trained false} {:id 11, :target 7, :name "Stunning Blow", :ap "A", :cost 15, :trained false} {:id 12, :target 13, :name "Jury Rigged", :ap "P", :cost 20, :trained false} {:id 13, :target 9, :name "Tinkerer", :ap "P", :cost 20, :trained false} {:id 14, :target 13, :name "Deadly Accuracy", :ap "P", :cost 20, :trained false} {:id 15, :target 11, :name "Improved Stunning Blow", :ap "A", :cost 20, :trained false} {:id 16, :target 17, :name "Intimidating", :ap "A", :cost 25, :trained false} {:id 17, :target 13, :name "Dedication", :ap "p", :cost 25, :trained false} {:id 18, :target 17, :name "Improved Armor Master", :ap "p", :cost 25, :trained false} {:id 19, :target 15, :name "Crippling Blow", :ap "A", :cost 25, :trained false}]}, :stats ({:idx 0, :name "Brawn", :abbr "Br", :rank 3, :base 2} {:idx 1, :name "Agility", :abbr "Ag", :rank 3, :base 2} {:idx 2, :name "Intellect", :abbr "Int", :rank 2, :base 2} {:idx 3, :name "Cunning", :abbr "Cun", :rank 2, :base 2} {:idx 4, :name "Willpower", :abbr "Will", :rank 2, :base 2} {:idx 5, :name "Presence", :abbr "Pr", :rank 2, :base 2})}
(def stats [
	{:idx 0 :name "Brawn" :abbr "Br" :rank 0 :base 0}
	{:idx 1 :name "Agility" :abbr "Ag" :rank 0 :base 0}
	{:idx 2 :name "Intellect" :abbr "Int" :rank 0 :base 0}
	{:idx 3 :name "Cunning" :abbr "Cun" :rank 0 :base 0}
	{:idx 4 :name "Willpower" :abbr "Will" :rank 0 :base 0}
	{:idx 5 :name "Presence" :abbr "Pr" :rank 0 :base 0}])
(def species  [
	{:name "Droid"   :stats [1 1 1 1 1 1] :basexp 175 :health [{:name "Soak" :base 0 :stat "Br" :rank 1 :mod 0} {:name "Wounds" :base 10 :stat "Br" :rank 11 :mod 0} {:name "Strain" :base 10 :stat "Will" :rank 11 :mod 0}] :free_skills []}
	{:name "Human"   :stats [2 2 2 2 2 2] :basexp 110 :health [{:name "Soak" :base 0 :stat "Br" :rank 2 :mod 0} {:name "Wounds" :base 10 :stat "Br" :rank 12 :mod 0} {:name "Strain" :base 10 :stat "Will" :rank 12 :mod 0}] :free_skills ["any" "any"]}
	{:name "Twi'Lek" :stats [1 2 2 2 2 3] :basexp 100 :health [{:name "Soak" :base 0 :stat "Br" :rank 1 :mod 0} {:name "Wounds" :base 10 :stat "Br" :rank 11 :mod 0} {:name "Strain" :base 11 :stat "Will" :rank 13 :mod 0}] :free_skills ["Charm|Deception"]}])
(def skills [
	{:name "Astrogation" :stat "Int" :free_rank false :rank 0 :type "General"}
	{:name "Athletics" :stat "Br" :free_rank false :rank 0 :type "General"}
	{:name "Charm" :stat "Pr" :free_rank false :rank 0 :type "General"}
	{:name "Coercion" :stat "Will" :free_rank false :rank 0 :type "General"}
	{:name "Computers" :stat "Int" :free_rank false :rank 0 :type "General"}
	{:name "Cool" :stat "Pr" :free_rank false :rank 0 :type "General"}
	{:name "Coordination" :stat "Ag" :free_rank false :rank 0 :type "General"}
	{:name "Deception" :stat "Cun" :free_rank false :rank 0 :type "General"}
	{:name "Discipline" :stat "Will" :free_rank false :rank 0 :type "General"}
	{:name "Leadership" :stat "Pr" :free_rank false :rank 0 :type "General"}
	{:name "Mechanics" :stat "Int" :free_rank false :rank 0 :type "General"}
	{:name "Medicine" :stat "Int" :free_rank false :rank 0 :type "General"}
	{:name "Negotiation" :stat "Pr" :free_rank false :rank 0 :type "General"}
	{:name "Perception" :stat "Cun" :free_rank false :rank 0 :type "General"}
	{:name "Piloting - Planetary" :stat "Ag" :free_rank false :rank 0 :type "General"}
	{:name "Piloting - Space" :stat "Ag" :free_rank false :rank 0 :type "General"}
	{:name "Resilience" :stat "Br" :free_rank false :rank 0 :type "General"}
	{:name "Skullduggery" :stat "Cun" :free_rank false :rank 0 :type "General"}
	{:name "Stealth" :stat "Ag" :free_rank false :rank 0 :type "General"}
	{:name "Streetwise" :stat "Cun" :free_rank false :rank 0 :type "General"}
	{:name "Survival" :stat "Cun" :free_rank false :rank 0 :type "General"}
	{:name "Vigilance" :stat "Will" :free_rank false :rank 0 :type "General"}
	{:name "Brawl" :stat "Br" :free_rank false :rank 0 :type "Combat"}
	{:name "Gunnery" :stat "Ag" :free_rank false :rank 0 :type "Combat"}
	{:name "Melee" :stat "Br" :free_rank false :rank 0 :type "Combat"}
	{:name "Ranged - Light" :stat "Ag" :free_rank false :rank 0 :type "Combat"}
	{:name "Ranged - Heavy" :stat "Ag" :free_rank false :rank 0 :type "Combat"}
	{:name "Core Worlds" :stat "Int" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Education" :stat "Int" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Lore" :stat "Int" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Outer Rim" :stat "Int" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Underworld" :stat "Int" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Xenology" :stat "Int" :free_rank false :rank 0 :type "Knowledge"}
	{:name "Other" :stat "Int" :free_rank false :rank 0 :type "Knowledge"}])
(def careers [
  {
    :name "Bounty Hunter" 
    :skills ["Athletics" "Perception" "Piloting - Planetary" "Piloting - Space" "Skullduggery" "Vigilance" "Brawl" "Ranged - Heavy"]
    :spex [
      {:name "Assassin" :skills ["Melee" "Ranged - Heavy" "Skullduggery" "Stealth"] :talent-tree []}
      {:name "Gadgeteer" :skills ["Coercion" "Mechanics" "Brawl" "Ranged - Light"] :talent-tree [
				{:id 0  :name "Brace" :ap "A" :cost 5 :trained false} 
				{:id 1  :name "Toughened" :ap "P" :cost 5 :trained false}
				{:id 2  :name "Intimidating" :ap "A" :cost 5 :trained false}
				{:id 3  :name "Defensive Stance" :ap "A" :cost 5 :trained false}
				{:id 4  :target 5  :name "Spare Clip" :ap "P" :cost 10 :trained false}
				{:id 5  :target 1  :name "Jury Rigged" :ap "P" :cost 10 :trained false}
				{:id 6  :target 5  :name "Point Blank" :ap "P" :cost 10 :trained false}
				{:id 7  :target 3  :name "Disorient" :ap "P" :cost 10 :trained false}
				{:id 8  :target 9  :name "Toughened" :ap "P" :cost 15 :trained false}
				{:id 9  :target 5  :name "Armor Master" :ap "P" :cost 15 :trained false}
				{:id 10 :target 9  :name "Natural Enforcer" :ap "A" :cost 15 :trained false}
				{:id 11 :target 7  :name "Stunning Blow" :ap "A" :cost 15 :trained false}
				{:id 12 :target 13 :name "Jury Rigged" :ap "P" :cost 20 :trained false}
				{:id 13 :target 9  :name "Tinkerer" :ap "P" :cost 20 :trained false}
				{:id 14 :target 13 :name "Deadly Accuracy" :ap "P" :cost 20 :trained false}
				{:id 15 :target 11 :name "Improved Stunning Blow" :ap "A" :cost 20 :trained false}
				{:id 16 :target 17 :name "Intimidating" :ap "A" :cost 25 :trained false}
				{:id 17 :target 13 :name "Dedication" :ap "p" :cost 25 :trained false}
				{:id 18 :target 17 :name "Improved Armor Master" :ap "p" :cost 25 :trained false}
				{:id 19 :target 15 :name "Crippling Blow" :ap "A" :cost 25 :trained false}
				]}
      {:name "Survivalist" :skills ["Xenology" "Perception" "Resilience" "Survival"] :talent-tree []}]
  }
  {
    :name "Explorer" 
    :skills ["Astrogation" "Cool" "Lore" "Outer Rim" "Xenology" "Perception" "Piloting - Space" "Survival"]
    :spex [
      {:name "Fringer" :skills ["Astrogation" "Coordination" "Negotiation" "Streetwise"] :talent-tree []}
      {:name "Scout" :skills ["Athletics" "Medicine" "Piloting - Planetary" "Survival"] :talent-tree []}
      {:name "Trader" :skills ["Deception" "Core Worlds" "Underworld" "Negotiation"] :talent-tree []}]
  }])
