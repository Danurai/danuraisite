(ns danuraisite.ktdata)

(def data [{
    :name "Craftworlds"
    :killteamname "Craftworld"
    :fireteamcount 2
    :labels ["Craftworld" "Aeldari" "Asuryani" "<CRAFTWORLD>"]
    :fireteams [{
            :name "Guardian Defender"
            :archetype ["Security" "Recon"]
        },{
            :name "Storm Guardian"
            :archetype ["Seek and Destroy" "Recon"]
        },{
            :name "Ranger"
            :archetype ["Seek and Destroy" "Recon"]
        },{
            :name "Dire Avenger"
            :archetypes ["Seek and Destroy" "Security" "Recon"]
            :labels ["Dire Avenger"]
            :operativecount 4
            :operatives [{
                    :role "Dire Avenger (Warrior)"
                    :info "Dire Avengers are experts at wielding their Avenger catapults, which unleash a flurry of monomolecular shuriken with every burst of fire. They have an uncanny knack of knowing when to launch lightning-fast assaults or fall back to draw the enemy into a killing zone."
                    :stats {
                            :m [3 2]
                            :apl 2
                            :ga 1
                            :df 3
                            :sv 4
                            :w 8
                        }
                    :weapons [{
                            :type "ranged"
                            :name "Avenger shuriken catapult"
                            :a 4
                            :bsws 3
                            :d [3 4]
                            :sa ["Balanced"]
                            :i ["Rending"]
                        }, {
                            :type "combat"
                            :name "Fists"
                            :a 3
                            :bsws 3
                            :d [2 3]
                            :sa []
                            :i []
                        }]
                    :abilities [{
                            :name "Defence Tactics"
                            :text "Each time this operative performs an *Overwatch* action, for that action's shooting attack, do not worsen the Ballistic Skill characteristic of its ranged weapons as a result of performing an *Overwatch* action."
                        }]
                    :uniqueactions []
                    :labels ["Dire Avenger" "Warrior"]
                    :skills {
                            :combat false
                            :defense false
                            :ranged true
                            :move true
                        }
                }, {
                    :role "Dire Avenger Exarch"
                    :info "Dire Avenger Exarchs are masters of their Aspect shrines, deadly warriors wholly consumed by the practice of war. Whether they go to the field carrying a long diresword or a pair of shuriken catapults, few can stand against them and live."
                    :leader true
                    :stats {
                            :m [3 2]
                            :apl 2
                            :ga 1
                            :df 3
                            :sv 4
                            :w 9
                        }
                    :weapons [{
                            :type "ranged"
                            :name "Avenger shuriken catapult"
                            :a 4
                            :bsws 2
                            :d [3 4]
                            :sa ["Balanced"]
                            :i ["Rending"]
                        }, {
                            :type "ranged"
                            :name "Shuriken pistol"
                            :a 4
                            :bsws 2
                            :d [3 4]
                            :sa ["Rng 6"]
                            :i ["Rending"]
                        }, {
                            :type "ranged"
                            :name "Twin avenger shuriken catapult"
                            :a 4
                            :bsws 3
                            :d [3 4]
                            :sa ["Relentless"]
                            :i ["Rending"]
                        }, {
                            :type "combat"
                            :name "Diresword"
                            :a 4
                            :bsws 2
                            :d [4 5]
                            :sa ["Lethal 5+"]
                            :i ["Rending"]
                        }, {
                            :type "combat"
                            :name "Fists"
                            :a 3
                            :bsws 2
                            :d [2 3]
                            :sa []
                            :i []
                        }, {
                            :type "combat"
                            :name "Power weapon"
                            :a 4
                            :bsws 2
                            :d [4 6]
                            :sa ["Lethal 5+"]
                            :i []
                        }]
                    :abilities [{
                            :name "Defence Tactics"
                            :text "Each time this operative performs an *Overwatch* action, for that action's shooting attack, do not worsen the Ballistic Skill characteristic of its ranged weapons as a result of performing an *Overwatch* action."
                        }, {
                            :name "Shimmershield"
                            :text "If this operative is equiped with a shimmershield, while a friendly *DIRE AVENGER* operative is within [2] of it, that operative has a 5+ invulnerable save."
                        }]
                    :uniqueactions [{
                            :name "Shuriken Storm"
                            :cost 2
                            :text "Make two shooting attacks with this operatives twin avenger shuriken catapult. When making those shooting attacks, ignore the weapon's Relentless special rule. This action is treated as a *Shoot* action. This operative can only perform this action if it is equipped with a twin avenger shuriken catapult"
                        }]
                    :labels ["Dire Avenger" "Exarch"]
                    :skills {
                        :combat true
                        :defense false
                        :ranged true
                        :move true
                        }
                }]
    }]
    :ploys {
      :strategic [{
        :faction "CRAFTWORLD"
        :name "Fleet"
        :cp 1
        :text "Until the end of the Turning Point, each time a friendly [CRAFTWORLD] operative performs a *Fall Back* or *Normal Move* action, it can perform a free *dash* action with that action."
        },{
        :faction "CRAFTWORLD"
        :name "Forewarned"
        :cp 1
        :text "Until the end of the Turning Point, each time a shooting attack is made against a ready friendly [CRAFTWORLD] operative, in the Roll Defence Dice step of that shooting attack, you can re-roll one of your defence dice."
        },{
        :faction "CRAFTWORLD"
        :name "Supreme Disdain"
        :cp 1
        :text "Until the end of the Turning Point, each time a friendly [CRAFTWORLD] operative fights in combat, in the Roll Attack Dice step of that combat, if your opponent discards more attack dice as failed hits than you do, you can change one of your retained normal hits to a critical hit."
        },{
        :faction "CRAFTWORLD"
        :name "Hidden Paths"
        :cp 1
        :text "Each friendly [RANGER] operative that has a Conceal order, is within [1] of Light or Heavy terrain and is more than [6] from enemy operatives can immediately perform a free *Dash* action but must finish that move within [1] of Light or Heavy terrain."
        }]
      :tactical [{
        :faction "CRAFTWORLD"
        :name "Matchless Agility"
        :cp 1
        :text "Use this Tactical Ploy when a friendly [CRAFTWORLD] operative is activated. Until the end of that operative's activation\n- it cannot perform a *Shoot* or *Fight* action\n- If it performs a *Dash* action, it can move an additional [2] for that action."
        },{
        :faction "CRAFTWORLD"
        :name "First of the Aspects"
        :cp 1
        :text "Use this Tactical Ploy during a friendly [DIRE AVENGER] operative's activation. Add 1 to it's API."
        }]
  }
}])