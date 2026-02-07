summon minecraft:cave_spider ~0.3 ~ ~0.2 {Tags:[nestweaver-spawn]}
summon minecraft:cave_spider ~-0.2 ~ ~-0.3 {Tags:[nestweaver-spawn]}
execute as @e[tag=nestweaver-spawn] run data modify entity @s Owner set from entity @p UUID
execute as @e[tag=nestweaver-spawn] run data remove entity @s Tags[]