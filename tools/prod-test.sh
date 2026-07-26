#!/bin/bash
# Прогон СОБРАННОГО (ремапнутого) jar на настоящем Fabric-сервере.
#
# Зачем: `./gradlew runServer` запускает мод в named-маппингах, поэтому НЕ ловит
# ошибки ремаппинга. Именно так пролез AbstractMethodError на RecipeInput#getItem
# (см. PORTING.md, «Ловушка mojmap»): дев-сервер зелёный, собранный jar падает.
# Любое изменение порта проверять здесь, а не только через runServer.
#
# Использование:  ./tools/prod-test.sh [каталог]     (по умолчанию /tmp/drinkbeer-prodtest)
set -euo pipefail

REPO="$(cd "$(dirname "$0")/.." && pwd)"
DIR="${1:-/tmp/drinkbeer-prodtest}"
MC=1.21.1
LOADER=0.19.3
FABRIC_API_VERSION=0.116.12+1.21.1

JAR=$(ls -t "$REPO"/build/libs/drinkbeer-fabric-*.jar | head -1)
[ -n "$JAR" ] || { echo "Нет собранного jar — сначала ./gradlew build"; exit 1; }

mkdir -p "$DIR/mods"
cd "$DIR"

if [ ! -f fabric-server-launch.jar ]; then
  INSTALLER=$(curl -s https://meta.fabricmc.net/v2/versions/installer \
    | python3 -c "import json,sys; print(json.load(sys.stdin)[0]['version'])")
  curl -sL -o fabric-server-launch.jar \
    "https://meta.fabricmc.net/v2/versions/loader/$MC/$LOADER/$INSTALLER/server/jar"
fi

# fabric-api берём из кеша gradle (это оригинальный production-jar, не dev-ремап)
if ! ls mods/fabric-api-*.jar >/dev/null 2>&1; then
  API=$(find ~/.gradle/caches/modules-2/files-2.1/net.fabricmc.fabric-api \
        -name "fabric-api-$FABRIC_API_VERSION.jar" | head -1)
  [ -n "$API" ] || { echo "fabric-api $FABRIC_API_VERSION не найден в кеше gradle"; exit 1; }
  cp "$API" mods/
fi

rm -f mods/drinkbeer-*.jar
cp "$JAR" mods/
echo "eula=true" > eula.txt
cat > server.properties <<'PROPS'
level-type=minecraft\:flat
online-mode=false
level-name=test
max-tick-time=-1
view-distance=6
simulation-distance=6
PROPS
rm -rf test

echo "Тестируем $(basename "$JAR") в $DIR"

# ВАЖНО: в мире без игроков чанки загружены, но НЕ тикают — отсюда forceload.
{
  sleep 55
  echo "forceload add 0 0 24 24"; sleep 3
  echo "setblock 10 4 10 drinkbeer:beer_barrel"; sleep 2

  echo "say ==== A: 4 мусорных слота (не должно быть краша) ===="
  echo "item replace block 10 4 10 container.0 with minecraft:stone"
  echo "item replace block 10 4 10 container.1 with minecraft:dirt"
  echo "item replace block 10 4 10 container.2 with minecraft:cobblestone"
  echo "item replace block 10 4 10 container.3 with minecraft:sand"
  sleep 6; echo "data get block 10 4 10"; sleep 2

  echo "say ==== B: валидный набор + кружки ===="
  echo "item replace block 10 4 10 container.0 with minecraft:wheat"
  echo "item replace block 10 4 10 container.1 with minecraft:wheat"
  echo "item replace block 10 4 10 container.2 with minecraft:blaze_powder"
  echo "item replace block 10 4 10 container.3 with minecraft:water_bucket"
  echo "item replace block 10 4 10 container.4 with drinkbeer:empty_beer_mug 8"
  sleep 6; echo "data get block 10 4 10"; sleep 2

  echo "say ==== C: варка до конца + хоппер ===="
  echo "setblock 10 3 10 minecraft:hopper[facing=down]"
  echo "setblock 10 2 10 minecraft:chest"
  echo "data merge block 10 4 10 {RemainingBrewTime: 10}"
  sleep 8; echo "data get block 10 2 10"; sleep 2

  echo "say ==== D: бартендинг-стол + специи ===="
  echo "setblock 12 4 10 drinkbeer:bartending_table_normal"; sleep 2
  echo "item replace block 12 4 10 container.0 with drinkbeer:beer_mug"; sleep 2
  echo "item replace block 12 4 10 container.2 with drinkbeer:spice_ice_mint"
  sleep 3; echo "data get block 12 4 10"; sleep 2
  echo "item replace block 12 4 10 container.2 with drinkbeer:spice_storm_shards"
  sleep 3; echo "data get block 12 4 10"; sleep 2

  echo "say ==== E: мусор в слот специи (должен быть отклонён) ===="
  echo "item replace block 12 4 10 container.2 with minecraft:stone"
  sleep 3; echo "data get block 12 4 10"; sleep 3

  echo "say ==== F: trade box ===="
  echo "setblock 14 4 10 drinkbeer:trade_box_normal"
  sleep 4; echo "data get block 14 4 10"; sleep 3

  echo "stop"; sleep 20
} | timeout 420 java -Xmx2G -jar fabric-server-launch.jar nogui 2>&1 | tee prod-test.log \
  | grep -E '===|has the following|AbstractMethodError|Exception|Ticking'

echo
echo "Полный лог: $DIR/prod-test.log"
