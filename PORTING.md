# Fabric-порт DrinkBeer Refill

Форк [DragonsPlusMinecraft/DrinkBeerRefill](https://github.com/DragonsPlusMinecraft/DrinkBeerRefill)
(ветка `1.21.1`, NeoForge) → Fabric 1.21.1 для сервера Kururun.

- upstream remote: `upstream` (`git fetch upstream 1.21.1`)
- рабочая ветка: `fabric-1.21.1`
- база порта: upstream `f3b2bfb` (версия 1.2.0)
- лицензия: **AGPL-3.0-only** (как у апстрима и у оригинала Lekavar/DrinkBeer) — при раздаче
  jar исходники должны быть доступны

## Принцип

Игровая логика апстрима не трогается — переклеен только loader-слой. Всё, что можно было
оставить дословно апстримовским, оставлено дословно: это цена дешёвых мерджей с апстримом,
если он когда-нибудь разморозится.

Маппинги — **официальные Mojang** (`loom.officialMojangMappings()`), а не yarn: апстрим
написан в mojmap, на yarn пришлось бы переименовывать каждый ванильный символ.

## Что заменено

| NeoForge | Fabric |
|---|---|
| `DeferredRegister` (10 реестров) | `lekavar.lma.drinkbeer.fabric.DeferredRegister` — прослойка с той же сигнатурой, регистрирует сразу (на Fabric реестры открыты в init) |
| `@Mod` + `IEventBus` | `ModInitializer` / `ClientModInitializer` (`DrinkBeer`, `DrinkBeerClient`) |
| `Capabilities.ItemHandler.BLOCK` + `ItemStackHandler`/`InvWrapper` | `WorldlyContainer` на самих блок-энтити (кег, бартендинг-стол) |
| `IMenuTypeExtension.create` + `FriendlyByteBuf` | `ExtendedScreenHandlerType` + `BlockPos.STREAM_CODEC`, `ExtendedScreenHandlerFactory<BlockPos>` на BE |
| `PayloadRegistrar` / `PacketDistributor` | `PayloadTypeRegistry` + `ServerPlayNetworking` / `ClientPlayNetworking` |
| `RegisterMenuScreensEvent`, `EntityRenderersEvent`, `RegisterParticleProvidersEvent`, `FMLClientSetupEvent` | всё в `DrinkBeerClient#onInitializeClient` (`MenuScreens`, `BlockEntityRenderers`, `ParticleFactoryRegistry`, `ItemProperties`) |
| `CreativeModeTab.builder()` | `FabricItemGroup.builder()` |
| `new SimpleParticleType(true)` (ctor protected) | `FabricParticleTypes.simple(true)` |
| `RecipeType.simple(id)` | анонимный `RecipeType` (ванильный `register()` вешает namespace `minecraft`) |
| `Item#getFoodProperties(stack, entity)`, `ItemStack#getFoodProperties(entity)` | `stack.get(DataComponents.FOOD)` / `item.components().get(DataComponents.FOOD)` |
| `FoodProperties.Builder#effect(Supplier<MobEffectInstance>, float)` | ванильный `effect(MobEffectInstance, float)` |
| `DataComponentHolder#get(Supplier<..>)`, `ItemStack#set(Supplier<..>, T)`, `Item.Properties#component(Supplier<..>, T)` | те же методы, но с `.get()` на нашем `Supplier` |
| `BlockEntity#onDataPacket` / `#handleUpdateTag` (патчи NeoForge) | удалены: ванильный клиент сам зовёт `loadWithComponents(getUpdateTag())` → `loadAdditional` |
| `AbstractContainerScreen#getXSize()/getYSize()` | поля `imageWidth` / `imageHeight` |
| `ItemHandlerHelper.giveItemToPlayer` | `player.getInventory().placeItemBackInInventory(stack)` |

Миксинов в моде нет — ни у апстрима, ни у порта.

## Автоматизация: как переложены слоты

Апстрим отдаёт хопперам `IItemHandler`; на Fabric это `WorldlyContainer` (его понимают и
ванильные хопперы, и Transfer API). Семантика сохранена один в один:

**Кег** (`BeerBarrelBlockEntity`) — виден только слот результата `5`, забрать можно лишь при
`statusCode == 2` (варка закончена), вставлять нельзя ничего.

**Бартендинг-стол** (`BartendingTableBlockEntity`) — три виртуальных слота поверх контейнера
на 2 ячейки: `0` вход пива (только если стол пуст и специй < 3), `1` готовый коктейль
(только на выход), `2` вход специи — всегда пустой на чтение, положенная туда специя сразу
уходит в коктейль (`putSpice`). Как и в апстриме, «сырое» пиво из слота 0 автоматикой не
вынуть.

## Рецепты

JSON'ы апстрима записаны в NeoForge-формате ингредиентов (`{"item": ...}`, `{"tag": ...}` и
списки из них), ванильный `Ingredient.CODEC` его не понимает. Вместо правки 16 файлов данных
в `DrinkBeerCodes` добавлен `INGREDIENT_COMPAT_CODEC`: сначала пробует ванильный разбор,
иначе переписывает JSON в ванильную форму (`"id"` / `"#tag"`) и повторяет. Принимаются оба
формата, ресурсы апстрима не тронуты.

## Ловушка mojmap: RecipeInput#getItem (краш при 4 заполненных слотах)

Симптом: собранный jar роняет игру `AbstractMethodError: ... BrewingInventory does not
define or inherit ... method_59984 of interface class_9695` при первом же поиске рецепта
(то есть как только в кеге заполнены все 4 слота ингредиентов, чем угодно).

Причина: в Mojang-маппингах `Container#getItem(int)` и `RecipeInput#getItem(int)` —
одно и то же имя с одним дескриптором, поэтому апстримовское
`class BrewingInventory extends SimpleContainer implements IBrewingInventory` компилируется
и на NeoForge работает (там mojmap в рантайме). На Fabric jar ремапится в интермедиари, где
это разные методы: `method_5438` (Container) и `method_59984` (RecipeInput). Унаследованный
от `SimpleContainer` `getItem` реализацией `RecipeInput` не является → метод остаётся
абстрактным.

Фикс: развязка типов. `BrewingInventory` больше не реализует `IBrewingInventory`, а рецепту
передаётся отдельная обёртка `recipes/BrewingRecipeInput` — её иерархия содержит только
`RecipeInput`, поэтому объявленный `getItem` ремапится однозначно.

Проверка остальных пар (скрипт по tiny-маппингам) показала в этой области ровно две такие
коллизии: `getItem` (наш случай) и `isEmpty` (`method_5442` против `method_59987`) —
последняя безопасна, у `RecipeInput#isEmpty` есть дефолтная реализация. `setChanged` у
`BlockEntity` и `Container` — один и тот же `method_5431`, конфликта нет.

⚠️ **Вывод для тестирования:** `./gradlew runServer` гоняет мод в named-маппингах и такие
баги в принципе не видит. Собранный jar обязательно прогонять на настоящем сервере —
`tools/prod-test.sh` (поднимает Fabric 0.19.3 + fabric-api и проходит сценарии варки,
хопперов, стола и trade box'а).

## Фиксы багов апстрима

### 1. Бартендинг-стол падал на первой же специи

`SpiceData.fromSpiceList` читал `spiceList.get(1)` и `get(2)` без проверки длины списка.
Первая же добавленная специя (список из одного элемента) роняла серверный тик
`IndexOutOfBoundsException: Index 1 out of bounds for length 1` — то есть бартендинг-стол
в апстримовской 1.21.1 не работал вообще. Переписано на безопасное чтение с сохранением
каскадной семантики (пустая специя обнуляет последующие).

### 2. Эффект «пьяный мороз» снимался мгновенно

`DrunkFrostWalkerStatusEffect.applyEffectTick` возвращал `false`. С 1.20.5 возвращаемое
значение означает «эффект ещё жив»: на `false` метод `MobEffectInstance#tick` сразу зовёт
`removeEffect`. То есть эффект от «Ледяного светлого лагера Хаара» снимался на первом же
тике — в `active_effects` оставался только `drinkbeer:drunk`, добавленный из
`onEffectAdded`, поэтому со стороны выглядело как «эффект не применяется». Возвращаем `true`.
(Второй параметр `ReplaceDisk#apply` — `EnchantedItemInUse` — вообще не читается, проверено
по байткоду 1.21.1, так что `null` там безопасен.)

### 3. Пиво невозможно забрать со стола

Апстримовский `BartendingTableBlock#useItemOn` возвращал `ItemInteractionResult.CONSUME` для
**любого** предмета в руке, включая пустую. Ваниль зовёт сначала `useItemOn` и переходит к
`useWithoutItem` (где и лежит «забрать кружку») только при
`PASS_TO_DEFAULT_BLOCK_INTERACTION` — то есть клик съедался и забрать пиво было невозможно
в принципе. Телеметрия с живого клиента: на пустую руку приходил только
`useItemOn stack=0 minecraft:air`, `useWithoutItem` не вызывался ни разу.

Фикс: если в руке не пиво и не специя — сразу `PASS_TO_DEFAULT_BLOCK_INTERACTION`.
Поведение для пива/специй не изменилось.

### 4. Битые ключи локализации у коктейля

`MixedBeerBlockItem` собирал ключи как `"block.drinkbeer." + beerItem.toString()`. Ванильный
`Item#toString()` — это `wrapAsHolder(this).getRegisteredName()`, то есть **полный id с
namespace**, а NeoForge патчит его до одного `path`. На Fabric получалось
`block.drinkbeer.drinkbeer:beer_mug` (видно в Jade и в тултипах). Ручная сборка строк
заменена на `beerItem.getDescriptionId()` / `BuiltInRegistries.ITEM.getKey(...).getPath()`.

### 5. Ни один блок мода не выпадал при добыче

Ваниль ищет лут-таблицу блока по id `<namespace>:blocks/<имя>` (константа `"blocks/"` в
`BlockBehaviour`), то есть файл должен лежать в `data/<ns>/loot_table/**blocks**/`. Апстрим
в коммите «Pack Change» (окт 2024) при миграции на 1.21 переименовал
`loot_tables/blocks` → `loot_table/**block**`, применив переименование к обоим сегментам
пути вместо одного верхнего. Итог: все 41 блок мода ломались без дропа — заметно это
в первую очередь на специях, которые иначе никак не поднять.

Фикс: каталог переименован обратно в `loot_table/blocks/`. Проверено на сервере
`/loot spawn <pos> mine <blockpos>` — специя, кег и стол выпадают предметами.

### 6. Краш при сохранении игрока после коктейля

`MixedBeerManager` вешал эффекты коктейля через `Holder.direct(mobEffect)`. Прямой холдер не
принадлежит реестру, поэтому ванильный кодек `MobEffectInstance#save` падает:
`IllegalStateException: Unregistered holder in ResourceKey[minecraft:root / minecraft:mob_effect]`
— краш «Saving entity NBT» при первом же автосейве/выходе после выпитого коктейля.
Заменено на `BuiltInRegistries.MOB_EFFECT.wrapAsHolder(...)`.

Баг апстримовский и на NeoForge тоже смертельный, но там до него никто не доходил: стол
падал ещё на первой специи (баг №1), а без стола коктейль получить неоткуда.

Все шесть багов не наши, из них №1, 2, 5 и 6 воспроизводятся и на NeoForge; проект апстрима
архивирован, так что живём со своими фиксами.

## Фиксы моих ошибок порта

Помимо ловушки mojmap выше: контейнерные блок-энтити не чистили инвентарь перед
`ContainerHelper.loadAllItems`. Снятые предметы на клиенте не исчезали (пакет присылает
`Items:[]`, а loadAllItems чистку не делает — ванильные контейнеры поэтому пересоздают
список, см. `ChestBlockEntity#loadAdditional`). Добавлен `clearContent()` в кеге, столе и
trade box'е.

## Что выброшено

- **JEI-плагин** (`compat/jei`) — в клиент-паке Kururun стоит **EMI**, не JEI. Вместо него
  написан свой EMI-плагин (см. ниже).
- **Jade-интеграция** — в коде апстрима 1.21.1 её и не осталось (только депа в gradle).
- Порядок креатив-табов (`withTabsBefore`) — метод только в NeoForge; табы на месте, но
  встают после ванильных, а не перед spawn eggs. Косметика.

## EMI вместо JEI

`compat/emi/` — категория `drinkbeer:brewing` с иконкой кега, кег как воркстейшн и по одному
рецепту на каждый JSON варки: 2×2 ингредиенты, слот пустых кружек, стрелка с временем варки,
результат. Зависимость `modCompileOnly` (+ `modLocalRuntime` для `runClient`), в jar
EMI-классов нет, в `fabric.mod.json` EMI только в `suggests` — на клиенте без EMI и на
сервере плагин просто не загружается.

EMI берётся с maven Modrinth (`maven.modrinth:emi:1.1.24+1.21.1+fabric` — ровно версия из
клиент-пака); официальный maven TerraformersMC артефакт `dev.emi:emi-fabric` не отдаёт.

Ключи локализации добавлены в `en_us`/`ru_ru`: `emi.category.drinkbeer.brewing`
(«Варка») и `drinkbeer.emi.brewing_time` («Время варки: %s»).

## Проверено

**Собранный jar на настоящем Fabric-сервере** (`tools/prod-test.sh`) — сценарии A–F:
4 мусорных слота (бывший краш) без ошибок; валидный набор → варка, списание, возврат ведра,
кружки 8→4; варка до конца → хоппер вытянул 4× Blaze Stout в сундук; стол + 2 специи →
компоненты `spiceA/spiceB`; мусор в слот специи отклонён; trade box тикает.

Отдельно проверено на собранном jar: `effect give drinkbeer:drunk_frost_walker` держится и
тикает (до фикса №2 исчезал в тот же тик).

Дев-сервер (`./gradlew runServer`, flat-мир, команды через консоль). ⚠️ В тестовом мире нет
игроков, поэтому чанки загружены, но **не тикают** — без `forceload add` ни кег, ни хопперы
не работают, и это артефакт теста, а не мода.

- старт чистый: мод грузится, реестры регистрируются, **9 рецептов варки** распознаны
  (значит compat-кодек ингредиентов работает);
- **варка целиком**: ингредиенты + 8 кружек → рецепт сматчился → ингредиенты списаны,
  ведро вернулось, кружек осталось 4, в выходном слоте 4× Blaze Stout, таймер тикает →
  по окончании `statusCode` = 2;
- **хоппер под кегом**: до окончания варки не забирает ничего (гейт `statusCode == 2`),
  после окончания вытянул 4× Blaze Stout в сундук, выходной слот опустел, кег вернулся
  в `statusCode` 0;
- **бартендинг-стол**: пиво в слот 0 → специя в слот 2 → в слоте 1 `mixed_beer`
  с компонентом `{spiceA: 6}`; три специи → `{spiceA: 6, spiceB: 12, spiceC: 8}`;
  четвёртая корректно отклонена;
- **хоппер под столом**: забрал готовый коктейль (со всеми компонентами) и передал в сундук;
- trade box ставится, NBT (`CoolingTime`, `LocationId`, `ResidentId`) инициализируется,
  ошибок нет.

Не проверено автоматикой: клиентская часть (GUI кега и tradebox, рендер кружки на столе,
партиклы, тултипы), питьё пива и эффекты, торговля через колокольчик, **EMI-категория**
(код компилируется против EMI 1.1.24, но вживую не запускался).
