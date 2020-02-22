(ns danuraisite.scoredata)

(def sidata {
  :spirits [{
		:name "A Spread of Rampant Green"
    :setname "core"
		:setup "1 Presence in highest Wetlands 1 Presence in Jungle without Dahan"
		:passive "In SS may destroy Presence to prevent Build/Ravage. During Growth may place destroyed Presence (costs 1 Energy if Blight card has flipped)."
		:growth ["ALWAYS Place Presence at range 2 in J/W PLUS ONE OF…" "Reclaim cards gain a card" "Place presence at range 1 +1 card play this turn" "Gain a card +3 Energy"]
		:energy ["0" "1" "Plant" "2" "2" "Plant" "3"]
		:cards ["1" "1" "2" "2" "3" "4"]
		:innate [{
			:name "Creepers Tear Into Mortars"
			:speed "Slow"
			:range "0"
			:effects [{
				:effect "1 Damage to a Building"
				:moon 1
				:plant 2
			} {
				:effect "Repeat"
				:moon 2
				:plant 3
			} {
				:effect "Repeat"
				:moon 3
				:plant 4
			}]
		} {
			:name "All-Enveloping Green"
			:speed "Fast"
			:range "1"
			:effects [{
				:effect "Defend 2"
				:water 1
				:plant 3
			} {
				:effect "Instead Defend 4"
				:water 2
				:plant 4
			} {
				:effect "Also remove a Blight"
				:water 3
				:earth 1
				:plant 5
			}]
		}]
	} {
		:name "Bringer of Dreams and Nightmares"
    :setname "core"
		:setup "2 Presence in highest Sands"
		:passive "If Powers would destroy instead push and make 0/2/5 fear for Exp/Town/City instead"
		:growth ["Reclaim cards Gain a card" "Reclaim one card Place Presence at range 0" "Gain a card Place Presence at range 1" "Place presence with Dahan/Inv at range 4 +2 Energy"]
		:energy ["2" "Ai" "3" "Mo" "4" "Any" "5"]
		:cards ["2" "2" "2" "3" "3" "Any"]
		:innate [{
			:name "Spirits May Yet Dream"
			:speed "Fast"
			:range "Any Spirit"
			:effects [{
				:effect "Turn any facedown Fear card face up"
				:moon 2
				:air 2
			} {
				:effect "Target spirit gains an element they have at least 1 of"
				:moon 3
			}]
		} {
			:name "Night Terrors"
			:speed "Slow"
			:range "Land with Invaders"
			:effects [{
				:effect "1 Fear"
				:moon 1
				:air 1
			} {
				:effect "+1 Fear"
				:moon 2
				:air 1
			} {
				:effect "+1 Fear"
				:moon 3
				:air 2
			}]
		}]
	} {
		:name "Heart of the Wildfire"
    :setname "promo1"
		:setup "3 Presence and 2 Blight in highest Sands"
		:passive "Whenever you place presence deal X damage in the land to invaders X = revealed fire on presence tracks; If you deal 2+ add a blight push all beasts and any number of dahan; If multiple presence are added at once this only triggers once; Your presence are not removed by blight caused by player card effects"
		:growth ["Reclaim cards gain a card 1 energy" "Gain a card place presence at range 3" "Place a presence at range 1 gain energy equal to 2+X X = revealed fire/plant on presence tracks"]
		:energy ["0" "F" "1" "2" "F+P" "3"]
		:cards ["1" "F" "2" "3" "F" "4"]
		:innate [{
			:name "Firestorm"
			:speed "Fast"
			:range "0 with Blight"
			:effects [{
				:effect "1 Damage per 2 Fire"
				:plant 1
			} {
				:effect "Instead 1 Damage per Fire"
				:plant 3
			} {
				:effect "You may divide this damage as you choose among lands with your presence and blight"
				:fire 4
				:air 2
			} {
				:effect "In lands with blight and your presence push all dahan destroy all invaders and beasts add blight"
				:fire 7
			}]
		} {
			:name "The Burned Land Regrows"
			:speed "Slow"
			:range "0"
			:effects [{
				:effect "If target land has 2+ blight remove a blight"
				:fire 4
				:plant 1
			} {
				:effect "Instead remove 1 blight"
				:fire 4
				:plant 2
			} {
				:effect "Remove an additional blight"
				:fire 5
				:earth 2
				:plant 2
			}]
		}]
	} {
		:name "Lightning's Swift Strike"
    :setname "core"
		:setup "2 Presence in highest Sands"
		:passive "Make a number of slow powers equal to your Air into fast powers"
		:growth ["Reclaim Gain a card +1 energy" "Place Presence Range 2 Place Presence Range 0" "Place Presence Range 1 +2 Energy"]
		:energy ["1" "1" "2" "2" "3" "4" "4" "5"]
		:cards ["2" "3" "4" "5" "6"]
		:innate [{
			:name "Thundering Destruction"
			:speed "Slow"
			:range "1 SS"
			:effects [{
				:effect "Destroy a Town"
				:fire 3
				:air 2
			} {
				:effect "Instead destroy a City"
				:fire 4
				:air 3
			} {
				:effect "Also Destroy a Town/City"
				:fire 5
				:water 1
				:air 4
			} {
				:effect "Also Destroy a Town/City"
				:fire 5
				:water 2
				:air 5
			}]
		}]
	} {
		:name "Ocean's Hungry Grasp"
    :setname "core"
		:setup "1 Presence in the Ocean (connected to all coasts) and 1 Presence in any Coastal land"
		:passive "Oceans on boards with your Presence are coastal wetlands for powers and blight. Invaders and Dahan drown. May exchange # Players drowned health for 1 Energy."
		:growth ["Reclaim cards gain a card Gather 1 Presence into Ocean +2 energy" "Place 1 Presence in an Ocean 1 Presence in an Ocean +1 Energy" "Gain a card place 1 Presence at range 1 in Coastal land Push 1 Presence from each Ocean"]
		:energy ["0" "Mo" "Wa" "1" "Ea" "Wa" "2"]
		:cards ["1" "2" "2" "3" "4" "5"]
		:innate [{
			:name "Pound Ships to Splinters"
			:speed "Fast"
			:range "0 Coastal"
			:effects [{
				:effect "1 Fear"
				:moon 1
				:water 2
				:air 1
			} {
				:effect "+1 Fear"
				:moon 2
				:water 3
				:air 1
			} {
				:effect "+2 Fear"
				:moon 3
				:water 4
				:air 2
			}]
		} {
			:name "Ocean Breaks the Shore"
			:speed "Slow"
			:range "0 Coastal"
			:effects [{
				:effect "Drown 1 Town"
				:water 2
				:earth 1
			} {
				:effect "Instead you may Drown 1 City"
				:water 3
				:earth 2
			} {
				:effect "Also Drown 1 Town or City"
				:water 4
				:earth 3
			}]
		}]
	} {
		:name "River Surges in Sunlight"
    :setname "core"
		:setup "1 Presence in highest Wetlands"
		:passive "Presence in Wetlands are considered Sacred Site"
		:growth ["Reclaim cards gain a card +1 energy" "Place presence at range 1 place presence at range 1" "Gain a card place presence at range 2"]
		:energy ["1" "2" "2" "3" "4" "4" "5"]
		:cards ["1" "2" "2" "3" "Reclaim One" "4" "5"]
		:innate [{
			:name "Massive Flooding"
			:speed "Slow"
			:range "1 SS"
			:effects [{
				:effect "Push 1 Explorer/Town"
				:sun 1
				:water 2
			} {
				:effect "Instead 2 damage then push 3 Explorers/Towns"
				:sun 2
				:water 3
			} {
				:effect "Instead 2 damage to each Invader"
				:sun 3
				:water 4
				:earth 1
			}]
		}]
	} {
		:name "Serpent Slumbering Beneath the Island"
    :setname "promo1"
		:setup "1 Presence in Land 5; Deep Slumber Track 5 7 8 10 11 12 13"
		:passive "Deep Slumber Track 5 7 8 10 11 12 13"
		:growth ["PICK TWO OF… Reclaim cards Move a Presence Range 1" "Gain a Card +1 Energy" "+4 Energy" "Place Presence in land without Blight Range 3"]
		:energy ["1" "Fi" "Any" "R1" "*Ea*" "6" "Any" "12"]
		:cards ["1" "Mo" "2" "Wa" "*Ea*" "4" "5R1"]
		:innate [{
			:name "Serpent Wakes in Power"
			:speed "Slow"
			:range "Self"
			:effects [{
				:effect "Gain 1 Energy; other spirits with absorbed Presence gain 1 Energy"
				:fire 2
				:water 1
				:plant 1
			} {
				:effect "Place Presence Range 1; others with 2 absorbed Presence do likewise"
				:water 2
				:earth 3
				:plant 2
			} {
				:effect "Gain a Major Power without forgetting. Others with 3 absorbed Presence do likewise"
				:fire 3
				:water 3
				:earth 3
				:plant 3
			}]
		} {
			:name "Serpent Rouses in Anger"
			:speed "Slow"
			:range "0"
			:effects [{
				:effect "For each Fi+Ea you have 1 damage to a building"
				:fire 1
				:earth 1
			} {
				:effect "For each 2 Mo+2 Ea you have 2 fear and you may push 1 Town"
				:moon 2
				:earth 2
			} {
				:effect "-7 Energy In every land X damage. X = your presence in/adj to that land"
				:moon 5
				:fire 6
				:earth 6
			}]
		}]
	} {
		:name "Shadows Flicker Like Flame"
    :setname "core"
		:setup "2 Presence in highest Jungle 1 Presence in land #5"
		:passive "May pay 1 Energy to target lands with Dahan ignoring range"
		:growth ["Reclaim cards gain a card" "Gain a card place presence at range 1" "Place presence at range 3 +3 Energy"]
		:energy ["0" "1" "3" "4" "5" "6"]
		:cards ["1" "2" "3" "3" "4" "5"]
		:innate [{
			:name "Darkness Swallows the Unwary"
			:speed "Fast"
			:range "1 SS"
			:effects [{
				:effect "Gather 1 Explorer"
				:moon 2
				:fire 1
			} {
				:effect "Destroy up to 2 Explorers 1 Fear per destroyed"
				:moon 3
				:fire 2
			} {
				:effect "3 damage 1 Fear per Invader destroyed"
				:moon 4
				:fire 3
				:air 2
			}]
		}]
	} {
		:name "Thunderspeaker"
    :setname "core"
		:setup "1 Presence in each land with 2 Dahan (2 Presence total)"
		:passive "Your presence may move with Dahan; after placing Blight from Ravage destroy a Presence within Range 1 for each destroyed Dahan"
		:growth ["Reclaim cards Gain two cards" "Place two Presence in land with Dahan (Range 2 Range 1)" "Place a Presence Range 1 gain 4 Energy"]
		:energy ["1" "Ai" "2" "Fi" "Su" "3"]
		:cards ["1" "2" "2" "3" "R1" "3" "4"]
		:innate [{
			:name "Gather the Warriors"
			:speed "Slow"
			:range "1"
			:effects [{
				:effect "This power may be Fast"
				:air 4
			} {
				:effect "Gather up to 1 Dahan per Air; Push up to 1 Dahan per Sun"
			}]
		} {
			:name "Lead the Furious Assault"
			:speed "Slow"
			:range "0"
			:effects [{
				:effect "This power may be Fast"
				:air 4
			} {
				:effect "Destroy 1 Town for every 2 Dahan"
				:sun 2
				:fire 1
			} {
				:effect "Destroy 1 City for every 3 Dahan"
				:sun 4
				:fire 3
			}]
		}]
	} {
		:name "Vital Strength of the Earth"
    :setname "core"
		:setup "2 Presence in highest Mountain 1 in highest Jungle"
		:passive "Defend 3 in all SS"
		:growth ["Reclaim Place Presence Range 2" "Gain a card Place Presence Range 0" "Place Presence Range 1 +2 Energy"]
		:energy ["2" "3" "4" "6" "7" "8"]
		:cards ["1" "1" "2" "2" "3" "4"]
		:innate [{
			:name "Gift of Strength"
			:speed "Fast"
			:range "Any Spirit"
			:effects [{
				:effect "Repeat 1 Power card with Cost 1 or less"
				:sun 1
				:earth 2
				:plant 2
			} {
				:effect "Instead Cost 3 or less"
				:sun 2
				:earth 3
				:plant 2
			} {
				:effect "Instead Cost 6 or less"
				:sun 2
				:earth 4
				:plant 3
			}]
		}]
	}]
  :scenarios [{
      :name "none"
      :setname "core"
      :difficulty 0
    }{
      :name "Guard the Isle's Heart"
      :setname "core"
      :difficulty 0
      :description "The Invaders have found the center of the island's power, and if they lay down roots there, the heart of the island will be shattered. You must act decisively in order to save your home, and in your haste to mount a defense, you reach out for whatever source of power you can find. With your newfound strength, fight back the Invaders!"
      :easier {:spirits ["A Spread of Rampant Green"]}
      :harder {:spirits ["Bringer of Dreams and Nightmares"] :adversaries ["The Kingdom of Brandenburg-Prussia"]}
    }{
      :name "Rituals of Terror"
      :setname "core"
      :difficulty 3
      :description "The Invaders seem resigned to a land of hostility and pain. What must their homeland be like, to inspire this dogged determination to stay despite the deaths of their neighbours, the portents, and omens the Spirits blaze across the sky?"
      :easier {:spirits ["Thunderspeaker"]}
      :harder {:players 1}
    }{
      :name "Blitz"
      :setname "core"
      :difficulty 0
      :description "Perhaps the Spirits of the Island are not so slow after all. But still, the Invaders are faster."
      :harder {:spirits ["Serpent Slumbering Beneath the Island"] :adversaries ["The Kingdom of Brandenburg-Prussia"]}
    }{
      :name "Dahan Insurrection"
      :setname "core"
      :difficulty 4
      :description "Citing strategic importance, the Invaders' government refuses to back down in the face of nebulous fears and rumors of the supernatural. They send waves of personnel to support the colony \"in response to Dahan belligerence\"."
      :easier {:spirits ["Thunderspeaker" "River Surges in Sunlight"]}
      :harder {:adversaries ["The Kingdom of England"]}
    }]
  :adversaries [{
      :name "none"
      :setname "core"
    }{
      :name "The Kingdom of Brandenburg-Prussia"
      :setname "core"
      :escalation "Land Rush: On each board with town/city, add 1 town to a land without town."
      :difficultylevels [1 2 4 6 7 9 10]
    }{
      :name "The Kingdom of Sweden"
      :setname "core"
      :escalation "Swayed by the Invaders: After Invaders Explore into each land this Phase, if that land has at least as many Invaders as Dahan, replace 1 Dahan with 1 town."
      :difficultylevels [1 3 4 6 7 9 10]
    }{
      :name "The Kingdom of England"
      :setname "core"
      :escalation "Building Boom: On each board with town/city, Build in the land with the most town/city."
      :difficultylevels [1 2 3 5 6 7 8]
    }]
  :boards ["a" "b" "c" "d"]
})

; Add Difficulties - R Eric Reuss:
; https://boardgamegeek.com/thread/1830894/scoring-when-you-combine-adversary-and-scenario