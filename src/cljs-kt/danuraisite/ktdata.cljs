(ns danuraisite.ktdata)

(def data [{
    :id "craftworlds"
    :name "Craftworlds"
    :killteamname "Craftworld"
    :fireteamcount 2
    :labels ["Craftworld" "Aeldari" "Asuryani" "<CRAFTWORLD>"]
    :fireteams [{
        :name "Guardian Defender"
        :archetypes ["Security" "Recon"]
        :oplimit 5
        :operatives[{
            :role "Warrior"
            :base true
            :info "Guardians are agile citizen-soldiers..."
            :stats {:m [3 2] :apl 2 :ga 1 :df 3 :sv 4 :w 8}
            :weapons [{
                :type "ranged"
                :name "Shuriken catapult"
                :a 4
                :bsws 3
                :d [3 4]
                :sa []
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
            :skills {:combat false :defense false :ranged true :move true}
          },{
            :role "Heavy Gunner"
            :ktlimit 1
            :info "Guardian Defenders are also expected to man heavy weapons platforms..."
            :stats {:m [3 2] :apl 2 :ga 1 :df 3 :sv 4 :w 8}
            :weapons [{
                :type "ranged"
                :name "shuriken catapult"
                :a 4
                :bsws 3
                :d [3 4]
                :sa []
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
            :uniqueactions [{
                :name "Control Platform"
                :cost 1
                :text "Select one ready friendly [[guardian defender heavy weapon platform]] operative within [3] of this operative. Perform a free *Normal Move*, *Fall Back* or *Shoot* action with that operative, then change it's order token to activated. This operative cannot perform this action while within Engagement Range of enemy operatives."
              }]
            :skills {:combat false :defense false :ranged true :move true}
          },{
            :role "Heavy Weapon Platform"
            :equipment {:options ["Aeldari Missile Launcher" "Bright Lance" "Scatter Laser" "Shuriken Cannon" "Starcannon"]}
            :reqrole "Heavy Gunner"
            :ktlimit 1
            :info ""
            :stats {:m [3 2] :apl 2 :ga 1 :df 3 :sv 4 :w 8}
            :weapons [{
                :type "ranged"
                :name ""
                :a 4
                :bsws 3
                :d [3 4]
                :sa []
                :i []
              }, {
                :type "combat"
                :name "Fists"
                :a 3
                :bsws 3
                :d [2 3]
                :sa []
                :i []
              }]
            :skills {:combat false :defense false :ranged true :move true}
          },{
            :role "Leader"
            :leader true
            :info ""
            :stats {:m [3 2] :apl 2 :ga 1 :df 3 :sv 4 :w 8}
            :weapons [{
                :type "ranged"
                :name ""
                :a 4
                :bsws 3
                :d [3 4]
                :sa []
                :i []
              }, {
                :type "combat"
                :name "Fists"
                :a 3
                :bsws 3
                :d [2 3]
                :sa []
                :i []
              }]
            :skills {:combat false :defense false :ranged true :move true}

        }]
      },{
        :name "Storm Guardian"
        :archetypes ["Seek and Destroy" "Recon"]
        :oplimit 5
        :operatives [{
            :role "Warrior"
            :base true
            :info "In the most desperate of circumstances, the strategic needs of the craftworld will call for a group of its citizen-soldiers to deploy as Storm Guardians - warrior specifically armed to fight in the maelstrom of close combat."
            :stats {:m [3 2] :apl 2 :ga 1 :df 3 :sv 4 :w 8}
            :weapons [{
                :type "ranged"
                :name "Shuriken pistol"
                :a 4
                :bsws 3
                :d [3 4]
                :sa ["Rng [6]"]
                :i ["Rending"]
              }, {
                :type "combat"
                :name "Storm Guardian Blades"
                :a 4
                :bsws 3
                :d [3 4]
              }]
            :skills {:combat true :defense false :ranged false :move true}
          },{
            :role "Gunner"
            :ftlimit 1
            :equipment {
              :base ["Fists"]
              :options ["Flamer" "Fusion Gun"]}
            :info "Storm Guardians often bolster their short-ranged firepower with fusion guns and flamers, allowing their teams to better adapt to the ever-changing realities of the battlefield. They ensure the team's survival in the face of the most dire or numerous enemies."
            :stats {:m [3 2] :apl 2 :ga 1 :df 3 :sv 4 :w 8}
            :weapons [{
                :type "ranged"
                :name "Flamer"
                :a 5
                :bsws 2
                :d [2 2]
                :sa ["Rng [6]" "Torrent [2]"]
                :i []
              }, {
                :type "ranged"
                :name "Fusion Gun"
                :a 4
                :bsws 3
                :d [6 3]
                :sa ["Rng [6]" "AP2"]
                :i ["MW4"]
              }, {
                :type "combat"
                :name "Fists"
                :a 3
                :bsws 3
                :d [2 3]
              }]
            :skills {:combat false :defense false :ranged true :move true}
          },{
            :role "Leader"
            :leader true
            :info "Storm Guardians fight with all the grace and skill of their kind, guided by experienced leaders."
            :stats {:m [3 2] :apl 2 :ga 1 :df 3 :sv 4 :w 9}
            :weapons [{
                :type "ranged"
                :name "Shuriken pistol"
                :a 4
                :bsws 2
                :d [3 4]
                :sa ["Rng [6]"]
                :i ["Rending"]
              }, {
                :type "combat"
                :name "Storm Guardian Blades"
                :a 4
                :bsws 2
                :d [3 4]
              }]
            :skills {:combat true :defense false :ranged false :move true}
        }]
      },{
        :name "Ranger"
        :archetypes ["Seek and Destroy" "Recon"]
        :oplimit 4
        :operatives [{
            :role "Warrior"
            :base true
            :info "Clad in long camoflaging cloaks, Rangers are unapparelled scouts and expert marksmen."
            :stats {:m [3 2] :apl 2 :ga 1 :df 3 :sv 5 :w 8}
            :weapons [{
                :type "ranged"
                :name "Ranger Long Rifle"
                :a 4
                :bsws 2
                :d [3 3]
                :sa ["Heavy" "Silent"]
                :i ["MW1"]
              }, {
                :type "ranged"
                :name "Shuriken Pistol"
                :a 4
                :bsws 3
                :d [3 4]
                :sa ["Rng [6]"]
                :i ["Rending"]
              },{
                :type "combat"
                :name "Fists"
                :a 3
                :bsws 3
                :d [2 3]
              }]
            :abilities [{
              :name "Camo Cloak"
              :text "Each time a shooting attack is made against this operative, in the Roll Defence Dice step of that shooting attack, before rolling your defence dice, if it is in Cover, one additional dice can be retained as a successful normal save as a result of Cover."
            }]
            :skills {:combat false :defense false :ranged true :move true}
          },{
            :role "Leader"
            :leader true
            :info "Rangers are outcasts, who have left their craftworld by choice or necessity."
            :stats {:m [3 2] :apl 2 :ga 1 :df 3 :sv 5 :w 9}
            :weapons [{
                :type "ranged"
                :name "Ranger Long Rifle"
                :a 4
                :bsws 2
                :d [3 3]
                :sa ["Heavy" "Silent" "Balanced"]
                :i ["MW1"]
              }, {
                :type "ranged"
                :name "Shuriken Pistol"
                :a 4
                :bsws 2
                :d [3 4]
                :sa ["Rng [6]"]
                :i ["Rending"]
              },{
                :type "combat"
                :name "Fists"
                :a 3
                :bsws 2
                :d [2 3]
            }]
            :abilities [{
              :name "Camo Cloak"
              :text "Each time a shooting attack is made against this operative, in the Roll Defence Dice step of that shooting attack, before rolling your defence dice, if it is in Cover, one additional dice can be retained as a successful normal save as a result of Cover."
            }]
            :skills {:combat false :defense false :ranged true :move true}
        }]
      },{
        :name "Dire Avenger"
        :archetypes ["Seek and Destroy" "Security" "Recon"]
        :labels ["Dire Avenger"]
        :oplimit 4
        :operatives [{
            :role "Warrior"
            :base true
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
            :role "Exarch"
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
              :equipment {
                :options ["Twin Avenger Shuriken Catapult" ["Avenger Shuriken Catapult" "Fists"] {:oneoptionfromeach [["Diresword" "Power Weapon"] ["Shimmershield" "Shuriken Pistol"]]}]}
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
                :sa ["Rng [6]"]
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
          :text "Until the end of the Turning Point, each time a friendly [[CRAFTWORLD]] operative performs a *Fall Back* or *Normal Move* action, it can perform a free *dash* action with that action."
        },{
          :faction "CRAFTWORLD"
          :name "Forewarned"
          :cp 1
          :text "Until the end of the Turning Point, each time a shooting attack is made against a ready friendly [[CRAFTWORLD]] operative, in the Roll Defence Dice step of that shooting attack, you can re-roll one of your defence dice."
        },{
          :faction "CRAFTWORLD"
          :name "Supreme Disdain"
          :cp 1
          :text "Until the end of the Turning Point, each time a friendly [[CRAFTWORLD]] operative fights in combat, in the Roll Attack Dice step of that combat, if your opponent discards more attack dice as failed hits than you do, you can change one of your retained normal hits to a critical hit."
        },{
          :faction "CRAFTWORLD"
          :name "Hidden Paths"
          :cp 1
          :text "Each friendly [[RANGER]] operative that has a Conceal order, is within [1] of Light or Heavy terrain and is more than [6] from enemy operatives can immediately perform a free *Dash* action but must finish that move within [1] of Light or Heavy terrain."
        }]
      :tactical [{
          :faction "CRAFTWORLD"
          :name "Matchless Agility"
          :cp 1
          :text "Use this Tactical Ploy when a friendly [[CRAFTWORLD]] operative is activated. Until the end of that operative's activation\n- it cannot perform a *Shoot* or *Fight* action\n- If it performs a *Dash* action, it can move an additional [2] for that action."
        },{
          :faction "CRAFTWORLD"
          :name "First of the Aspects"
          :cp 1
          :text "Use this Tactical Ploy during a friendly [[DIRE AVENGER]] operative's activation. Add 1 to it's APL."
        }]
      }
    :equipmentexclusions ["Heavy Weapon Plaform"] 
    :equipment [{
        :name "Weaponised Panoply"
        :cost 1
        :restriction ["Dire Avenger"]
        :type "combat"
        :weapon {
          :name "Weaponised Panoply"
          :a 3
          :bsws 3
          :d [3 4]
        }
      },{
        :name "Avenger Shrine Banner"
        :cost 2
        :restriction ["Dire Avenger Exarch"]
        :type "ability"
        :ability {
          :name "Avenger Shrine Banner"
          :text "While a friendly [[DIRE AVENGER]] operative is visible to and within [3] of this operative, when determining control of an objective marker, treat that friendly operative's APL as being 1 higher. Note that this is not a modifier."
        }
      },{
        :name "Ranger Scope"
        :cost 3
        :text "Select one ranger long rifle the operative is equipped with. That weapon gains the Lethal 5+ special rule for the battle."
      },{
        :name "Pathfinder Cloak"
        :cost 2
        :restriction ["Ranger"]
        :type "ability"
        :ability {
          :name "Pathfinder Cloak"
          :text "While this operative has a Conceal order, it is always treated as having a Conceal order, regardless of any other rules (e.g. Vantage Point)."
        }
      },{
        :name "Wraithbone Talisman"
        :cost 3
        :type "ability"
        :ability {
          :name "Wraitbone Talisman"
          :text "Once per battle, when this operative is fighting in combat, making a shooting attack, or ashooting attack is being made against it, you can use the Commans Re-roll Tactical Ploy without spending any CP."
        }
      },{
        :name "Celestial Shield"
        :restriction ["Guardian Defender" "Storm Guardian"]
        :type "ability"
        :ability {
          :name "Celestial Shield"
          :text "Once per battle, in the Roll Defence Dice phase of a shooting attack made against this operative, you can use this ability. If you do, for that shooting attack, this operative has a 4+ invulnerable save."
        }
      },{
        :name "Plasma Grenade"
        :cost 2
        :type "ranged"
        :weapon {
          :name "Plasma grenade"
          :a 3
          :bsws 3
          :d [3 4]
          :sa ["Rng [6]" "Blast [1]" "Limited" "Indirect"]
        }
      }]
  },{
    :id "drukhari"
    :name "Drukhari"
    :killteamname "Commorrite"
    :labels ["Drukhari" "Commorrite"]
    :fireteamcount 2
    :fireteams [{
        :name "Kabalite"
        :archetypes ["Security" "Recon"]
        :labels ["Kabalite"]
        :oplimit 5
        :operatives [{
            :role "Warrior"
            :base true
          },{
            :role "Gunner"
            :fireteamlimit 1
            :equipment {
              :base ["Array of blades"]
              :options ["Blaster" "Shredder"]
            }
          },{
            :role "Heavy Gunner"
            :killteamlimit 1
            :equipment {
              :base ["Array of blades"]
              :options ["Dark Lance" "Splinter Cannon"]
            }
          },{
            :role "Sybarite"
            :altname "Sybarite"
            :leader true
            :equipment {
              :options [
                [["Blast pistol" "Splinter Pistol" "Splinter Rifle"]
                 ["Agoniser" "Array of Blades" "Power Weapon"]]
              ]
            }

        }]
      },{
        :name "Wych"
        :oplimit 5
        :operatives [{
            :role "Warrior"
            :base true
          },{
            :role "Fighter"
            :equipment {:options ["Hydra Gauntlets" "Razorflails" "Shardnet and Impaler"]}
          },{
            :role ""
            :altname "Hekatrix"
            :leader true
            :equipment [
              [["Blast Pistol" "Splinter Pistol"]
               ["Agoniser" "Hekatarii blade" "power weapon"]]
            ]
        }]
    }]
  },{
    :id "harlequins"
    :name "Harlequins"
    :labels []
    :fireteams [{
      :archetypes ["Seek and Destroy" "Infiltration" "Recon"]
      :name "Troupe"
      :opname "Player"
      :oplimit 8
      :operatives [{
          :role "Warrior"
          :base true
          :equipment {
            :base ["Shuriken Pistol"]
            :options ["Harlequin's blade" "Harlequin's Caress" "Harlequin's embrace" "Harlequin's Kiss"]
          }
        },{
          :role "Gunner"
          :equipment {
            :options [[["Fusion Pistol" "Neuro disruptor"] ["Harlequin's blade" "Harlequin's Caress" "Harlequin's embrace" "Harlequin's Kiss"]]]
          }
        },{
          :role "Leader"
          :leader true
          :equipment {
            :options [[["Fusion Pistol" "Neuro disruptor"] ["Harlequin's blade" "Harlequin's Caress" "Harlequin's embrace" "Harlequin's Kiss"]]]
          }
      }]
    }]
  }])