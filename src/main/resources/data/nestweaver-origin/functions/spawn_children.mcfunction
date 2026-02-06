summon minecraft:cave_spider ~ ~ ~ {Tags:[nestweaver-origin:spawn_children]}
summon minecraft:cave_spider ~ ~ ~ {Tags:[nestweaver-origin:spawn_children]}
summon minecraft:cave_spider ~ ~ ~ {Tags:[nestweaver-origin:spawn_children]}
execute as @e[tag=nestweaver-origin:spawn_children] run data modify entity @s Owner set from entity @p UUID
execute as @e[tag=nestweaver-origin:spawn_children] run data remove entity @s Tags[]