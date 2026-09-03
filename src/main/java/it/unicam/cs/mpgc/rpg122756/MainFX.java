package it.unicam.cs.mpgc.rpg122756;

import it.unicam.cs.mpgc.rpg122756.game.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg122756.game.engine.GameEngine;
import it.unicam.cs.mpgc.rpg122756.game.world.DungeonBuilder;
import it.unicam.cs.mpgc.rpg122756.game.world.MapLayoutBuilder;
import it.unicam.cs.mpgc.rpg122756.model.crafting.AlchemyBook;
import it.unicam.cs.mpgc.rpg122756.model.crafting.Recipe;
import it.unicam.cs.mpgc.rpg122756.model.crafting.RecipeDatabase;
import it.unicam.cs.mpgc.rpg122756.model.entities.Alchemist;
import it.unicam.cs.mpgc.rpg122756.model.entities.Monster;
import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import it.unicam.cs.mpgc.rpg122756.model.items.Item;
import it.unicam.cs.mpgc.rpg122756.model.items.Potion;
import it.unicam.cs.mpgc.rpg122756.model.items.PotionEffect;
import it.unicam.cs.mpgc.rpg122756.persistence.GameState;
import it.unicam.cs.mpgc.rpg122756.persistence.impl.JsonSaveManager;
import it.unicam.cs.mpgc.rpg122756.model.entities.ActiveEffect;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.stream.Collectors;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;
import java.util.*;

public class MainFX extends Application {

    private GameEngine engine;
    private CombatSystem combatSystem;
    private JsonSaveManager saveManager;
    private GridPane gameGrid;
    private TextArea dialogArea;
    private Label hpLabel;
    private Label apLabel;
    private Label atkLabel;
    private Label defLabel;
    private Label levelLabel;
    private Label xpLabel;
    private Label roomLabel;
    private StackPane gameArea;
    private String currentSaveName = "partita1";

    private int playerX = 7;
    private int playerY = 13;
    private int currentRoom = 1;

    private static final int GRID_SIZE = 15;
    private double cellSize;

    private int[][][] currentMap = new int[30][GRID_SIZE][GRID_SIZE];
    private String[][][] itemNames = new String[30][GRID_SIZE][GRID_SIZE];
    private Item[][][] itemsOnMap = new Item[30][GRID_SIZE][GRID_SIZE];
    private String[][][] monsterNames = new String[30][GRID_SIZE][GRID_SIZE];
    private Monster[][][] monsters = new Monster[30][GRID_SIZE][GRID_SIZE];

    private boolean inCombat = false;
    private boolean showingPotionMenu = false;
    private boolean showingBook = false;
    private boolean showingInventory = false;
    private Monster currentMonster;
    private boolean isLevelingUp = false;

    private Button btnUp, btnDown, btnLeft, btnRight;
    private Button btnA;
    private HBox actionBox;
    private HBox bottomControls;
    private final Random random = new Random();
    private Timeline respawnTimer;

    private StackPane currentOverlay = null;

    // Variabili per il sistema SHEN
    private List<Ingredient> shenSelectedIngredients = new ArrayList<>();
    private double inventoryScrollPos = 0.0;
    private ScrollPane currentInventoryScroll;
    private static final long BOSS_RESPAWN_MINUTES = 30;
    private int maxShenSlots = 4;
    private java.util.Map<Integer, Long> bossDefeatedTimes = new java.util.HashMap<>();
    private ThemeManager themeManager = new ThemeManager();
    private MapLayoutBuilder mapLayoutBuilder;
    private RecipeDatabase recipeDatabase;

    @Override
    public void start(Stage primaryStage) {
        Alchemist alchemist = new Alchemist("Nicholas");
        AlchemyBook alchemyBook = new AlchemyBook();
        combatSystem = new CombatSystem();
        saveManager = new JsonSaveManager();

        var dungeon = DungeonBuilder.buildFullDungeon(alchemist);
        engine = new GameEngine(alchemist, dungeon, alchemyBook, combatSystem, saveManager);
        dungeon.start();

        recipeDatabase = new RecipeDatabase();
        updateMaxShenSlots();

        initializeAllRooms();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2c3e50;");

        HBox topBar = createTopBar();
        root.setTop(topBar);

        gameArea = createGameArea();

        gameArea.setMaxHeight(420);
        gameArea.setPrefHeight(420);

        gameArea.setPadding(new Insets(0, 0, 15, 0));

        root.setCenter(gameArea);

        bottomControls = createBottomControls();

        bottomControls.setMinHeight(190);
        bottomControls.setPrefHeight(190);

        root.setBottom(bottomControls);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("L'Alchimista del Dungeon - Stanza 1");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        Platform.runLater(() -> {
            resizeGrid();
        });

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> resizeGrid());
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> resizeGrid());

        dialogArea.setOnMouseClicked(e -> dialogArea.clear());

        updateDialog();
        updateStats();

        startRespawnSystem();
    }

    private void resizeGrid() {
        if (gameGrid == null || gameArea == null || gameArea.getScene() == null)
            return;

        gameArea.applyCss();
        gameArea.layout();

        double sceneWidth = gameArea.getScene().getWidth();
        double sceneHeight = gameArea.getScene().getHeight();

        double topBarHeight = 75;
        double bottomBarHeight = 190;

        double safeAreaHeight = sceneHeight - topBarHeight - bottomBarHeight;
        double safeAreaWidth = sceneWidth - 40;

        if (safeAreaHeight <= 0 || safeAreaWidth <= 0)
            return;

        cellSize = Math.min(safeAreaWidth / GRID_SIZE, safeAreaHeight / GRID_SIZE);
        cellSize = Math.min(cellSize, 30);
        cellSize = Math.max(cellSize, 20);

        double totalSize = cellSize * GRID_SIZE;
        gameGrid.setPrefSize(totalSize, totalSize);
        gameGrid.setMaxSize(totalSize, totalSize);
        gameGrid.setMinSize(totalSize, totalSize);

        drawGrid();
    }

    private HBox createTopBar() {
        HBox bar = new HBox(20);
        bar.setPadding(new Insets(15));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #34495e;");

        // ==========================================
        // STATS BOX (Modificata: ATT e DEF sotto la barra principale)
        // ==========================================

        // Riga superiore: HP, AP, LIV, XP, Stanza
        HBox topStatsRow = new HBox(20);
        topStatsRow.setAlignment(Pos.CENTER_LEFT);

        hpLabel = new Label("HP: 9999/9999"); // ✅ Lasciato a 9999 come richiesto
        hpLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        hpLabel.setTextFill(Color.WHITE);

        apLabel = new Label("AP: 50/50");
        apLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        apLabel.setTextFill(Color.LIGHTBLUE);

        levelLabel = new Label("LIV: 1");
        levelLabel.setFont(Font.font("Courier New", 18));
        levelLabel.setTextFill(Color.YELLOW);

        xpLabel = new Label("XP: 0/50");
        xpLabel.setFont(Font.font("Courier New", 14));
        xpLabel.setTextFill(Color.LIGHTGRAY);

        roomLabel = new Label("Stanza: 1");
        roomLabel.setFont(Font.font("Courier New", 18));
        roomLabel.setTextFill(Color.ORANGE);

        topStatsRow.getChildren().addAll(hpLabel, apLabel, levelLabel, xpLabel, roomLabel);

        // Riga inferiore: ATT e DEF
        HBox bottomStatsRow = new HBox(20);
        bottomStatsRow.setAlignment(Pos.CENTER_LEFT);

        atkLabel = new Label("ATT: 10");
        atkLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        atkLabel.setTextFill(Color.rgb(255, 100, 100)); // Rosso chiaro

        defLabel = new Label("DEF: 5");
        defLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        defLabel.setTextFill(Color.rgb(100, 200, 255)); // Azzurro chiaro

        bottomStatsRow.getChildren().addAll(atkLabel, defLabel);

        // VBox che contiene le due righe di statistiche (5px di spazio verticale tra di
        // esse)
        VBox statsBox = new VBox(5);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.getChildren().addAll(topStatsRow, bottomStatsRow);

        // ==========================================
        // SPAZIATORE E PULSANTI DESTRI
        // ==========================================
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightButtons = new HBox(15);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        // 1️⃣ PULSANTE SALVA
        Button btnSave = new Button("💾 Salva");
        btnSave.setPrefSize(100, 40);
        btnSave.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5;");
        btnSave.setOnAction(e -> {
            if (isLevelingUp)
                return;
            TextInputDialog dialog = new TextInputDialog(currentSaveName);
            dialog.setTitle("Salvataggio Partita");
            dialog.setHeaderText("Inserisci un nome per il salvataggio:");
            dialog.setContentText("Nome file:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                String fileName = name.endsWith(".json") ? name : name + ".json";
                File saveDir = new File("saves");
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                String filePath = "saves/" + fileName;
                File file = new File(filePath);

                if (file.exists() && !fileName.equals(currentSaveName)) {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    alert.setTitle("File già esistente");
                    alert.setHeaderText("Il file '" + fileName + "' esiste già.");
                    alert.setContentText("Vuoi sovrascriverlo?");
                    Optional<javafx.scene.control.ButtonType> choice = alert.showAndWait();
                    if (choice.isEmpty() || choice.get() != javafx.scene.control.ButtonType.OK) {
                        return;
                    }
                }

                try {
                    engine.saveGame(filePath);
                    currentSaveName = fileName;
                    engine.addMessage("💾 Partita salvata con successo in: " + fileName);
                    updateDialog();
                } catch (Exception ex) {
                    engine.addMessage("❌ Errore nel salvataggio: " + ex.getMessage());
                    updateDialog();
                }
            });
        });

        // 2️⃣ PULSANTE CARICA
        Button btnLoad = new Button("📂 Carica");
        btnLoad.setPrefSize(100, 40);
        btnLoad.setStyle(
                "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5;");
        btnLoad.setOnAction(e -> {
            if (isLevelingUp)
                return;
            File saveDir = new File("saves");
            File[] files = saveDir.listFiles((dir, n) -> n.endsWith(".json"));

            if (files == null || files.length == 0) {
                engine.addMessage("❌ Nessun salvataggio trovato nella cartella 'saves'.");
                updateDialog();
                return;
            }

            List<String> saveNames = Arrays.stream(files)
                    .map(File::getName)
                    .collect(Collectors.toList());

            ChoiceDialog<String> loadDialog = new ChoiceDialog<>(saveNames.get(0), saveNames);
            loadDialog.setTitle("Caricamento Partita");
            loadDialog.setHeaderText("Scegli un salvataggio da caricare:");
            loadDialog.setContentText("File:");

            Optional<String> result = loadDialog.showAndWait();
            result.ifPresent(fileName -> {
                String filePath = "saves/" + fileName;
                try {
                    GameState state = saveManager.load(filePath);
                    if (state != null) {
                        Alchemist savedAlch = state.getAlchemist();
                        Alchemist currentAlch = engine.getAlchemist();

                        currentAlch.setLevel(savedAlch.getLevel());
                        currentAlch.setHp(savedAlch.getHp());
                        currentAlch.setAp(savedAlch.getAp());
                        currentAlch.setExperience(savedAlch.getExperience());

                        currentAlch.getInventory().clear();
                        for (Item item : savedAlch.getInventory().getItems()) {
                            currentAlch.getInventory().addItem(item);
                        }

                        engine.getAlchemyBook().getDiscoveredIngredients().clear();
                        engine.getAlchemyBook().getDiscoveredIngredients().addAll(state.getDiscoveredIngredients());

                        int levelIdx = state.getCurrentLevelIndex();
                        engine.getDungeon().enterLevel(levelIdx);
                        currentRoom = levelIdx + 1;

                        inCombat = false;
                        currentMonster = null;
                        switchToExplorationMode();
                        drawGrid();
                        updateStats();

                        currentSaveName = fileName;

                        engine.addMessage("📂 Partita caricata con successo: " + fileName);
                        updateDialog();
                    } else {
                        engine.addMessage("❌ Errore: il file di salvataggio è corrotto o vuoto.");
                        updateDialog();
                    }
                } catch (Exception ex) {
                    engine.addMessage("❌ Errore nel caricamento: " + ex.getMessage());
                    updateDialog();
                }
            });
        });

        // 3️⃣ PULSANTI INVENTARIO E LIBRO ALCHEMICO
        Button btnInventory = new Button("🎒 Inventario");
        btnInventory.setPrefSize(140, 40);
        btnInventory.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5;");
        btnInventory.setOnAction(e -> {
            if (isLevelingUp)
                return;
            showInventoryOverlay();
        });
        Button btnBook = new Button("📖 Libro Alchemico");
        btnBook.setPrefSize(160, 40);
        btnBook.setStyle(
                "-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5;");
        btnBook.setOnAction(e -> {
            if (isLevelingUp)
                return;
            showAlchemyBookOverlay();
        });

        rightButtons.getChildren().addAll(btnSave, btnLoad, btnInventory, btnBook);

        bar.getChildren().addAll(statsBox, spacer, rightButtons);
        HBox.setHgrow(bar, Priority.ALWAYS);
        return bar;
    }

    // --- INIZIALIZZAZIONE DELLE STANZE ---
    private void initializeAllRooms() {
        mapLayoutBuilder = new MapLayoutBuilder(currentMap, monsters, monsterNames, itemsOnMap, itemNames, GRID_SIZE,
                random, engine);
        for (int room = 0; room < currentMap.length; room++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                for (int y = 0; y < GRID_SIZE; y++) {
                    currentMap[room][x][y] = TileConstants.TILE_GRASS;
                    itemNames[room][x][y] = null;
                    itemsOnMap[room][x][y] = null;
                    monsterNames[room][x][y] = null;
                    monsters[room][x][y] = null;
                }
            }
        }

        mapLayoutBuilder.initializeRoom1();
        mapLayoutBuilder.initializeRoom2();
        mapLayoutBuilder.initializeRoom3();
        mapLayoutBuilder.initializeRoom4();
        mapLayoutBuilder.initializeRoom5();
        mapLayoutBuilder.initializeRoom6();
        mapLayoutBuilder.initializeRoom7();
        mapLayoutBuilder.initializeRoom8();
        mapLayoutBuilder.initializeRoom9();
    }

    private void startRespawnSystem() {
        respawnTimer = new Timeline(new KeyFrame(Duration.seconds(30), e -> respawnMonstersAndItems()));
        respawnTimer.setCycleCount(Timeline.INDEFINITE);
        respawnTimer.play();
    }

    private void respawnMonstersAndItems() {
        for (int room = 0; room <= 1; room++) {
            List<Monster> monsterPool = Arrays.asList(
                    new Monster("Ragno Velenoso", 40, 8, 3, 25,
                            Arrays.asList(new Ingredient("Tela di Ragno", "Appiccicosa", 19, 1)),
                            "Fuoco", false),
                    new Monster("Lupo Mannaro", 60, 12, 5, 35,
                            Arrays.asList(new Ingredient("Zanna di Lupo", "Affilata", 23, 1)),
                            "Luce", false),
                    new Monster("Folletto Dispettoso", 30, 6, 2, 20,
                            Arrays.asList(new Ingredient("Polvere di Folletto", "Scintillante", 13, 1)),
                            "Luce", false));

            List<String[]> availableItems = Arrays.asList(
                    new String[] { "Erba Lunare", "Erba che brilla.", "11" },
                    new String[] { "Lucciola Argentea", "Luce tenue.", "17" },
                    new String[] { "Radice Secca", "Materiale base.", "7" });

            int existingMonsters = 0;
            int existingItems = 0;
            for (int x = 1; x < GRID_SIZE - 1; x++) {
                for (int y = 1; y < GRID_SIZE - 1; y++) {
                    if (currentMap[room][x][y] == TileConstants.TILE_MONSTER)
                        existingMonsters++;
                    if (currentMap[room][x][y] == TileConstants.TILE_ITEM)
                        existingItems++;
                }
            }

            int targetMonsters = (room == 0) ? 2 : 6;
            while (existingMonsters < targetMonsters) {
                int x, y;
                do {
                    x = 2 + random.nextInt(GRID_SIZE - 4);
                    y = 2 + random.nextInt(GRID_SIZE - 4);
                } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

                currentMap[room][x][y] = TileConstants.TILE_MONSTER;
                Monster baseMonster = monsterPool.get(random.nextInt(monsterPool.size()));
                monsters[room][x][y] = new Monster(baseMonster.getName(), baseMonster.getMaxHp(),
                        baseMonster.getAttack(), baseMonster.getDefense(),
                        baseMonster.getXpReward(), baseMonster.getPossibleDrops(),
                        baseMonster.getWeakness(), false);
                monsterNames[room][x][y] = baseMonster.getName();
                existingMonsters++;
            }

            int targetItems = (room == 0) ? 2 : 6;
            while (existingItems < targetItems) {
                int x, y;
                do {
                    x = 2 + random.nextInt(GRID_SIZE - 4);
                    y = 2 + random.nextInt(GRID_SIZE - 4);
                } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

                currentMap[room][x][y] = TileConstants.TILE_ITEM;
                String[] itemData = availableItems.get(random.nextInt(availableItems.size()));
                itemNames[room][x][y] = itemData[0];
                itemsOnMap[room][x][y] = new Ingredient(itemData[0], itemData[1], Integer.parseInt(itemData[2]), 1);
                existingItems++;
            }
        }

        if (currentRoom <= 2) {
            drawGrid();
        }
    }

    private void changeRoom(int newRoom, int newX, int newY) {
        currentRoom = newRoom;
        playerX = newX;
        playerY = newY;

        String roomName = "";
        switch (newRoom) {
            case 1:
                roomName = "Ingresso (Foresta)";
                break;
            case 2:
                roomName = "Sala dei Mostri (Foresta)";
                break;
            case 3:
                roomName = "Tana del Guardiano della Foresta";
                break;
            case 4:
                roomName = "Ingresso alle Caverne Umide";
                break;
            case 5:
                roomName = "Profondità delle Caverne Umide";
                break;
            case 6:
                roomName = "Tana del Golem di Pietra";
                break;
        }

        engine.addMessage("🚪 Entri nella Stanza " + newRoom + ": " + roomName);
        updateDialog();
        updateStats();
        drawGrid();

        Stage stage = (Stage) gameArea.getScene().getWindow();
        stage.setTitle("L'Alchimista del Dungeon - Stanza " + newRoom);
    }

    private void updateMaxShenSlots() {
        int level = engine.getAlchemist().getLevel();
        if (level <= 10)
            maxShenSlots = 4;
        else if (level <= 20)
            maxShenSlots = 6;
        else if (level <= 30)
            maxShenSlots = 8;
        else if (level <= 40)
            maxShenSlots = 10;
        else
            maxShenSlots = 12;
    }

    // --- OVERLAY LEVEL UP ---
    private void showLevelUpOverlay() {
        isLevelingUp = true;
        disableControls();

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.9);");

        overlay.setMouseTransparent(false);
        overlay.setOnMouseClicked(e -> e.consume());

        currentOverlay = overlay;

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(700);
        content.setStyle(
                "-fx-background-color: #1a1a2e; -fx-border-color: #ffd700; -fx-border-width: 5; -fx-border-radius: 15;");

        Label title = new Label("✨ LEVEL UP! ✨");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 36));
        title.setTextFill(Color.rgb(255, 215, 0));

        int newLevel = engine.getAlchemist().getLevel();
        Label subtitle = new Label("Livello " + newLevel + " raggiunto! Scegli un bonus:");
        subtitle.setFont(Font.font("Courier New", 16));
        subtitle.setTextFill(Color.WHITE);

        int hpBonus, attBonus, defBonus, apBonus, invBonus;
        if (newLevel <= 10) {
            hpBonus = 10;
            attBonus = 3;
            defBonus = 2;
            apBonus = 5;
            invBonus = 2;
        } else if (newLevel <= 20) {
            hpBonus = 15;
            attBonus = 4;
            defBonus = 3;
            apBonus = 7;
            invBonus = 3;
        } else if (newLevel <= 30) {
            hpBonus = 20;
            attBonus = 5;
            defBonus = 4;
            apBonus = 10;
            invBonus = 4;
        } else if (newLevel <= 40) {
            hpBonus = 25;
            attBonus = 6;
            defBonus = 5;
            apBonus = 12;
            invBonus = 5;
        } else {
            hpBonus = 30;
            attBonus = 7;
            defBonus = 6;
            apBonus = 15;
            invBonus = 6;
        }

        GridPane bonusGrid = new GridPane();
        bonusGrid.setHgap(15);
        bonusGrid.setVgap(15);
        bonusGrid.setAlignment(Pos.CENTER);

        String[][] bonuses = {
                { "❤️ Vitalità", "+" + hpBonus + " HP Max", "HP" },
                { "⚔️ Potenza", "+" + attBonus + " Attacco", "ATT" },
                { "🛡️ Resistenza", "+" + defBonus + " Difesa", "DEF" },
                { "✨ Energia", "+" + apBonus + " AP Max", "AP" },
                { "🎒 Capacità", "+" + invBonus + " Slot", "INV" }
        };

        int col = 0, row = 0;
        for (String[] bonus : bonuses) {
            Button btn = new Button(bonus[0] + "\n" + bonus[1]);
            btn.setPrefSize(200, 80);
            btn.setStyle(
                    "-fx-background-color: #16213e; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-color: #ffd700; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

            final String bonusType = bonus[2];
            btn.setOnAction(e -> {
                switch (bonusType) {
                    case "HP":
                        engine.getAlchemist().applyLevelBonus(Alchemist.BonusType.HP);
                        break;
                    case "ATT":
                        engine.getAlchemist().applyLevelBonus(Alchemist.BonusType.ATTACK);
                        break;
                    case "DEF":
                        engine.getAlchemist().applyLevelBonus(Alchemist.BonusType.DEFENSE);
                        break;
                    case "AP":
                        engine.getAlchemist().applyLevelBonus(Alchemist.BonusType.AP);
                        break;
                    case "INV":
                        engine.getAlchemist().applyLevelBonus(Alchemist.BonusType.INVENTORY);
                        break;
                }

                engine.getAlchemist().consumeOneLevelUp();

                handleMilestone();

                engine.addMessage("🎉 Bonus scelto: " + bonus[0] + "!");
                updateDialog();
                updateStats();

                if (currentOverlay != null) {
                    gameArea.getChildren().remove(currentOverlay);
                    currentOverlay = null;
                }

                if (engine.getAlchemist().getPendingLevelUpCount() > 0) {
                    javafx.application.Platform.runLater(() -> {
                        showLevelUpOverlay();
                    });
                } else {
                    isLevelingUp = false;
                    enableControls();
                }
            });

            bonusGrid.add(btn, col, row);
            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }

        content.getChildren().addAll(title, subtitle, bonusGrid);
        overlay.getChildren().add(content);
        gameArea.getChildren().add(overlay);
    }

    private void handleMilestone() {
        int level = engine.getAlchemist().getLevel();
        if (level % 5 != 0)
            return;
        Potion milestonePotion = createMilestonePotion(level);
        if (milestonePotion == null)
            return;
        Ingredient ing1 = new Ingredient("Ingrediente Base", "Base", 11, 1);
        Ingredient ing2 = new Ingredient("Ingrediente Base", "Base", 11, 1);
        Recipe recipe = new Recipe(milestonePotion.getName(), milestonePotion.getValue(), milestonePotion,
                Arrays.asList(ing1, ing2));
        engine.getAlchemyBook().discoverRecipe(recipe);
        engine.getAlchemist().getInventory().addItem(milestonePotion);
        engine.getAlchemist().getInventory().addItem(milestonePotion);
        engine.addMessage("🎁 MILESTONE! Hai sbloccato: " + milestonePotion.getName() + "!");
        updateDialog();
    }

    private Potion createMilestonePotion(int level) {
        switch (level) {
            case 5:
                return new Potion("Pozione di Cura Minore", "Cura 30 HP.", 20, 8,
                        new PotionEffect(PotionEffect.EffectType.HEAL, 30, 0));
            case 10:
                return new Potion("Pozione di Energia", "Recupera 30 AP.", 25, 10,
                        new PotionEffect(PotionEffect.EffectType.HEAL, 30, 0));
            case 15:
                return new Potion("Pozione di Fuoco", "40 danni.", 35, 12,
                        new PotionEffect(PotionEffect.EffectType.DAMAGE, 40, 0));
            case 20:
                return new Potion("Pozione di Luce", "50 danni.", 40, 15,
                        new PotionEffect(PotionEffect.EffectType.DAMAGE, 50, 0));
            case 25:
                return new Potion("Pozione di Cura Maggiore", "Cura 100 HP.", 60, 20,
                        new PotionEffect(PotionEffect.EffectType.HEAL, 100, 0));
            case 30:
                return new Potion("Pozione di Veleno", "60 danni.", 50, 18,
                        new PotionEffect(PotionEffect.EffectType.DAMAGE, 60, 0));
            case 35:
                return new Potion("Pozione di Ghiaccio", "70 danni.", 65, 22,
                        new PotionEffect(PotionEffect.EffectType.DAMAGE, 70, 0));
            case 40:
                return new Potion("Elisir di Vita", "+200 HP.", 120, 30,
                        new PotionEffect(PotionEffect.EffectType.HEAL, 200, 0));
            case 45:
                return new Potion("Pozione di Fulmine", "100 danni.", 90, 25,
                        new PotionEffect(PotionEffect.EffectType.DAMAGE, 100, 0));
            case 50:
                return new Potion("Pietra Filosofale", "200 danni.", 200, 50,
                        new PotionEffect(PotionEffect.EffectType.DAMAGE, 200, 0));
            default:
                return null;
        }
    }

    private void checkPendingLevelUp() {
        if (engine.getAlchemist().isPendingLevelUp())
            showLevelUpOverlay();
    }

    // --- OVERLAY INVENTARIO ---
    private void showInventoryOverlay() {
        if (inCombat) {
            engine.addMessage("Non puoi aprire l'inventario durante il combattimento!");
            updateDialog();
            return;
        }

        closeOverlay();
        showingInventory = true;
        disableControls();

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.85);");
        currentOverlay = overlay;

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(750);
        content.setStyle(
                "-fx-background-color: #1e272e; -fx-border-color: #27ae60; -fx-border-width: 4; -fx-border-radius: 10;");

        Label title = new Label("🎒 Inventario");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        title.setTextFill(Color.rgb(46, 204, 113));

        int usedSlots = engine.getAlchemist().getInventory().getUsedSlots();
        int maxSlots = engine.getAlchemist().getInventory().getMaxSlots();
        Label slotsInfo = new Label("Slot: " + usedSlots + " / " + maxSlots);
        slotsInfo.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        slotsInfo.setTextFill(Color.WHITE);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefHeight(400);
        scrollPane.setPrefWidth(700);
        scrollPane.setStyle("-fx-background-color: #0a0a0a;");
        scrollPane.setFitToWidth(true);

        currentInventoryScroll = scrollPane;
        scrollPane.setVvalue(inventoryScrollPos);

        // FIX: GridPane unico per tutta la lista (tabella invisibile con colonne
        // allineate)
        GridPane itemsGrid = new GridPane();
        itemsGrid.setHgap(10);
        itemsGrid.setVgap(5);
        itemsGrid.setPadding(new Insets(5));

        // Definiamo le colonne con larghezze fisse
        ColumnConstraints colIcon = new ColumnConstraints(30);
        ColumnConstraints colQty = new ColumnConstraints(40);
        ColumnConstraints colName = new ColumnConstraints();
        colName.setHgrow(Priority.ALWAYS);
        ColumnConstraints colVal = new ColumnConstraints(80);
        ColumnConstraints colDel1 = new ColumnConstraints(70);
        ColumnConstraints colDelAll = new ColumnConstraints(80);
        itemsGrid.getColumnConstraints().addAll(colIcon, colQty, colName, colVal, colDel1, colDelAll);

        List<Item> invItems = engine.getAlchemist().getInventory().getItems();

        if (invItems.isEmpty()) {
            Label empty = new Label("L'inventario è vuoto...");
            empty.setFont(Font.font("Courier New", 16));
            empty.setTextFill(Color.GRAY);
            empty.setPadding(new Insets(20));
            itemsGrid.add(empty, 0, 0, 6, 1);
        } else {
            Map<String, List<Item>> grouped = invItems.stream().collect(Collectors.groupingBy(Item::getName));
            int rowIndex = 0;

            for (Map.Entry<String, List<Item>> entry : grouped.entrySet()) {
                String itemName = entry.getKey();
                List<Item> itemList = entry.getValue();
                int quantity = itemList.size();
                Item firstItem = itemList.get(0);

                // FIX: Aggiungiamo lo sfondo per la riga (occupa tutte le 6 colonne)
                HBox rowBackground = new HBox();
                rowBackground.setStyle(
                        "-fx-background-color: #2c3e50; -fx-border-color: #27ae60; -fx-border-width: 1; -fx-border-radius: 5;");
                rowBackground.setPrefHeight(45); // Altezza della riga
                itemsGrid.add(rowBackground, 0, rowIndex, 6, 1); // Span di 6 colonne

                // Colonna 0: Icona
                Label itemIcon = new Label(firstItem instanceof Potion ? "🧪" : "🌿");
                itemIcon.setFont(Font.font("Courier New", 22));

                // Colonna 1: Quantità
                Label quantityLabel = new Label(quantity + "x");
                quantityLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
                quantityLabel.setTextFill(Color.WHITE);

                // Colonna 2: Nome
                Label nameLabel = new Label(itemName);
                nameLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
                nameLabel.setTextFill(Color.rgb(46, 204, 113));

                // Colonna 3: Valore (centrato)
                Label valueLabel = new Label("Val: " + firstItem.getValue());
                valueLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                valueLabel.setTextFill(Color.rgb(241, 196, 15));
                valueLabel.setAlignment(Pos.CENTER);

                // Colonna 4: Elimina 1 (visibile solo se quantity > 1)
                HBox del1Box = new HBox();
                del1Box.setAlignment(Pos.CENTER);
                if (quantity > 1) {
                    Button btnRemoveOne = new Button("🗑️ -1");
                    btnRemoveOne.setPrefSize(65, 30);
                    btnRemoveOne.setStyle(
                            "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11; -fx-font-weight: bold; -fx-border-radius: 5;");
                    btnRemoveOne.setOnAction(e -> {
                        removeOneItemFromInventory(itemName);
                        refreshInventoryOverlay();
                    });
                    del1Box.getChildren().add(btnRemoveOne);
                }

                // Colonna 5: Elimina Tutto
                HBox delAllBox = new HBox();
                delAllBox.setAlignment(Pos.CENTER);
                Button btnRemoveAll = new Button("🗑️ Tutto");
                btnRemoveAll.setPrefSize(75, 30);
                btnRemoveAll.setStyle(
                        "-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 11; -fx-font-weight: bold; -fx-border-radius: 5;");
                btnRemoveAll.setOnAction(e -> {
                    removeAllItemsFromInventory(itemName);
                    refreshInventoryOverlay();
                });
                delAllBox.getChildren().add(btnRemoveAll);

                // Aggiungiamo gli elementi SOPRA lo sfondo
                itemsGrid.add(itemIcon, 0, rowIndex);
                itemsGrid.add(quantityLabel, 1, rowIndex);
                itemsGrid.add(nameLabel, 2, rowIndex);
                itemsGrid.add(valueLabel, 3, rowIndex);
                itemsGrid.add(del1Box, 4, rowIndex);
                itemsGrid.add(delAllBox, 5, rowIndex);

                rowIndex++;
            }
        }

        scrollPane.setContent(itemsGrid);

        Button btnClose = new Button("Chiudi");
        btnClose.setPrefSize(150, 40);
        btnClose.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-border-radius: 5;");
        btnClose.setOnAction(e -> closeOverlay());

        content.getChildren().addAll(title, slotsInfo, scrollPane, btnClose);
        overlay.getChildren().add(content);
        gameArea.getChildren().add(overlay);
    }

    private void removeOneItemFromInventory(String itemName) {
        // FIX 2: Salva la posizione dello scroll prima di modificare la lista
        if (currentInventoryScroll != null) {
            inventoryScrollPos = currentInventoryScroll.getVvalue();
        }

        List<Item> items = engine.getAlchemist().getInventory().getItems();
        for (Item item : items) {
            if (item.getName().equals(itemName)) {
                engine.getAlchemist().getInventory().removeItem(item);
                engine.addMessage("️ Hai eliminato 1x " + itemName);
                updateDialog();
                updateStats();
                return;
            }
        }
    }

    private void removeAllItemsFromInventory(String itemName) {
        List<Item> items = engine.getAlchemist().getInventory().getItems();
        List<Item> toRemove = new ArrayList<>();
        for (Item item : items) {
            if (item.getName().equals(itemName))
                toRemove.add(item);
        }
        for (Item item : toRemove)
            engine.getAlchemist().getInventory().removeItem(item);
        if (!toRemove.isEmpty()) {
            engine.addMessage("🗑️ Hai eliminato tutto: " + itemName);
            updateDialog();
            updateStats();
        }
    }

    // --- OVERLAY LIBRO ALCHEMICO ---
    private int currentAlchemyPage = 0;
    private int recipesPerPage = 4;

    private void showAlchemyBookOverlay() {
        if (inCombat) {
            engine.addMessage("Non puoi consultare il libro durante il combattimento!");
            updateDialog();
            return;
        }

        closeOverlay();

        showingBook = true;
        disableControls();

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.9);");
        currentOverlay = overlay;

        List<Recipe> recipes = engine.getAlchemyBook().getDiscoveredRecipes();
        int totalPages = Math.max(1, (int) Math.ceil((double) recipes.size() / recipesPerPage));

        VBox bookContainer = new VBox(10);
        bookContainer.setPadding(new Insets(15, 20, 5, 20));
        bookContainer.setAlignment(Pos.CENTER);
        bookContainer.setMaxWidth(900);
        bookContainer.setPrefHeight(470);
        bookContainer.setMaxHeight(470);
        bookContainer.setStyle(
                "-fx-background-color: #3e2723; -fx-border-color: #d4af37; -fx-border-width: 6; -fx-border-radius: 15;");

        Label title = new Label("📖 Libro Alchemico");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        title.setTextFill(Color.rgb(212, 175, 55));
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        Label subtitle = new Label("Clicca su una ricetta per vedere i dettagli");
        subtitle.setFont(Font.font("Courier New", 13));
        subtitle.setTextFill(Color.rgb(212, 175, 55));
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setMaxWidth(Double.MAX_VALUE);

        HBox pagesArea = new HBox(0);
        pagesArea.setAlignment(Pos.CENTER);
        pagesArea.setPadding(new Insets(10));

        VBox leftPage = new VBox(10);
        leftPage.setPadding(new Insets(15));
        leftPage.setPrefWidth(380);
        leftPage.setMinHeight(300);
        leftPage.setStyle(
                "-fx-background-color: #f4e4c1; -fx-border-color: #d4af37; -fx-border-width: 2; -fx-border-radius: 5;");

        Rectangle centerLine = new Rectangle(4, 300);
        centerLine.setFill(Color.rgb(212, 175, 55));

        VBox rightPage = new VBox(10);
        rightPage.setPadding(new Insets(15));
        rightPage.setPrefWidth(380);
        rightPage.setMinHeight(300);
        rightPage.setStyle(
                "-fx-background-color: #f4e4c1; -fx-border-color: #d4af37; -fx-border-width: 2; -fx-border-radius: 5;");

        int startIndex = currentAlchemyPage * recipesPerPage;
        int endIndex = Math.min(startIndex + recipesPerPage, recipes.size());

        int leftCount = 0;
        for (int i = startIndex; i < endIndex && leftCount < 2; i++) {
            Recipe recipe = recipes.get(i);
            VBox recipeCard = createCompactRecipeCard(recipe);
            leftPage.getChildren().add(recipeCard);
            leftCount++;
        }

        // Pagina destra (prossime 2 ricette)
        int rightCount = 0;
        for (int i = startIndex + 2; i < endIndex && rightCount < 2; i++) {
            Recipe recipe = recipes.get(i);
            VBox recipeCard = createCompactRecipeCard(recipe);
            rightPage.getChildren().add(recipeCard);
            rightCount++;
        }

        if (recipes.isEmpty()) {
            Label emptyLeft = new Label("Il libro è vuoto...");
            emptyLeft.setFont(Font.font("Courier New", 16));
            emptyLeft.setTextFill(Color.GRAY);
            emptyLeft.setAlignment(Pos.CENTER);
            emptyLeft.setMaxWidth(Double.MAX_VALUE);
            emptyLeft.setMinHeight(300);
            leftPage.getChildren().add(emptyLeft);

            Label emptyRight = new Label("Usa SHEN per scoprire ricette!");
            emptyRight.setFont(Font.font("Courier New", 16));
            emptyRight.setTextFill(Color.GRAY);
            emptyRight.setAlignment(Pos.CENTER);
            emptyRight.setMaxWidth(Double.MAX_VALUE);
            emptyRight.setMinHeight(300);
            rightPage.getChildren().add(emptyRight);
        }

        pagesArea.getChildren().addAll(leftPage, centerLine, rightPage);

        // Navigazione
        HBox navigation = new HBox(30);
        navigation.setAlignment(Pos.CENTER);

        Button btnPrev = new Button("◀ Precedente");
        btnPrev.setPrefSize(150, 35);
        btnPrev.setStyle(
                "-fx-background-color: #d4af37; -fx-text-fill: #3e2723; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5;");
        btnPrev.setDisable(currentAlchemyPage == 0);
        btnPrev.setOnAction(e -> {
            if (currentAlchemyPage > 0) {
                currentAlchemyPage--;
                showAlchemyBookOverlay();
            }
        });

        Label pageInfo = new Label("Pagina " + (currentAlchemyPage + 1) + " di " + totalPages);
        pageInfo.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        pageInfo.setTextFill(Color.rgb(212, 175, 55));

        Button btnNext = new Button("Successiva ▶");
        btnNext.setPrefSize(150, 35);
        btnNext.setStyle(
                "-fx-background-color: #d4af37; -fx-text-fill: #3e2723; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5;");
        btnNext.setDisable(currentAlchemyPage >= totalPages - 1);
        btnNext.setOnAction(e -> {
            if (currentAlchemyPage < totalPages - 1) {
                currentAlchemyPage++;
                showAlchemyBookOverlay();
            }
        });

        navigation.getChildren().addAll(btnPrev, pageInfo, btnNext);

        Set<String> discovered = engine.getAlchemyBook().getDiscoveredIngredients();
        Label discoveredLabel = new Label("Ingredienti scoperti: " + discovered.size());
        discoveredLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        discoveredLabel.setTextFill(Color.rgb(212, 175, 55));
        discoveredLabel.setAlignment(Pos.CENTER);
        discoveredLabel.setMaxWidth(Double.MAX_VALUE);

        HBox footerButtons = new HBox(20);
        footerButtons.setAlignment(Pos.CENTER);

        Button btnShen = new Button("🔮 SHEN");
        btnShen.setPrefSize(140, 35);
        btnShen.setStyle(
                "-fx-background-color: #00bcd4; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5;");
        btnShen.setOnAction(e -> {
            currentAlchemyPage = 0;
            closeOverlay();
            showShenCircleOverlay();
        });

        Button btnClose = new Button("Chiudi Libro");
        btnClose.setPrefSize(140, 35);
        btnClose.setStyle(
                "-fx-background-color: #d4af37; -fx-text-fill: #3e2723; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5;");
        btnClose.setOnAction(e -> {
            currentAlchemyPage = 0;
            closeOverlay();
        });

        footerButtons.getChildren().addAll(btnShen, btnClose);

        bookContainer.getChildren().addAll(title, subtitle, pagesArea, navigation, discoveredLabel, footerButtons);
        overlay.getChildren().add(bookContainer);
        gameArea.getChildren().add(overlay);
    }

    // Card compatta: solo nome della ricetta
    private VBox createCompactRecipeCard(Recipe recipe) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(12));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinHeight(70); // Più piccola
        card.setStyle(
                "-fx-background-color: rgba(212,175,55,0.2); -fx-border-color: #d4af37; -fx-border-width: 2; -fx-border-radius: 8;");
        card.setOnMouseClicked(e -> showRecipeDetailOverlay(recipe));
        card.setCursor(javafx.scene.Cursor.HAND);

        Label recipeName = new Label(" " + recipe.getName());
        recipeName.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        recipeName.setTextFill(Color.rgb(62, 39, 35));
        recipeName.setAlignment(Pos.CENTER);
        recipeName.setMaxWidth(Double.MAX_VALUE);

        Label clickHint = new Label("Clicca per dettagli");
        clickHint.setFont(Font.font("Courier New", 10));
        clickHint.setTextFill(Color.GRAY);
        clickHint.setAlignment(Pos.CENTER);
        clickHint.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(recipeName, clickHint);
        return card;
    }

    // Nuova overlay per i dettagli della ricetta
    private void showRecipeDetailOverlay(Recipe recipe) {
        if (currentOverlay != null) {
            gameArea.getChildren().remove(currentOverlay);
        }

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.92);");
        currentOverlay = overlay;

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(600);
        content.setMaxHeight(500);
        content.setStyle(
                "-fx-background-color: #f4e4c1; -fx-border-color: #d4af37; -fx-border-width: 4; -fx-border-radius: 10;");

        // Titolo
        Label title = new Label("🧪 " + recipe.getName());
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 26));
        title.setTextFill(Color.rgb(62, 39, 35));
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        // Descrizione (cos'è)
        Label descTitle = new Label("📜 Cos'è:");
        descTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        descTitle.setTextFill(Color.rgb(62, 39, 35));
        descTitle.setMaxWidth(Double.MAX_VALUE);

        Label descText = new Label(recipe.getResultPotion().getDescription());
        descText.setFont(Font.font("Courier New", 13));
        descText.setTextFill(Color.rgb(62, 39, 35));
        descText.setWrapText(true);
        descText.setMaxWidth(Double.MAX_VALUE);

        // Effetto (cosa fa)
        Label effectTitle = new Label("⚡ Effetto:");
        effectTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        effectTitle.setTextFill(Color.rgb(62, 39, 35));
        effectTitle.setMaxWidth(Double.MAX_VALUE);

        PotionEffect effect = recipe.getResultPotion().getEffect();
        String effectText = "";
        if (effect.getType() == PotionEffect.EffectType.HEAL) {
            effectText = "Recupera " + effect.getMagnitude() + " HP";
        } else if (effect.getType() == PotionEffect.EffectType.DAMAGE) {
            effectText = "Infligge " + effect.getMagnitude() + " danni al nemico";
        }
        Label effectLabel = new Label(effectText);
        effectLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        effectLabel.setTextFill(Color.rgb(211, 84, 0));
        effectLabel.setMaxWidth(Double.MAX_VALUE);

        // Costo AP
        Label apLabel = new Label("Costo AP: " + recipe.getResultPotion().getApCost());
        apLabel.setFont(Font.font("Courier New", 13));
        apLabel.setTextFill(Color.rgb(62, 39, 35));
        apLabel.setMaxWidth(Double.MAX_VALUE);

        // Separatore
        Label separator = new Label("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        separator.setFont(Font.font("Courier New", 12));
        separator.setTextFill(Color.rgb(212, 175, 55));
        separator.setAlignment(Pos.CENTER);
        separator.setMaxWidth(Double.MAX_VALUE);

        // Come farla (ingredienti)
        Label craftTitle = new Label("🔬 Come farla:");
        craftTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        craftTitle.setTextFill(Color.rgb(62, 39, 35));
        craftTitle.setMaxWidth(Double.MAX_VALUE);

        Label targetLabel = new Label("Valore totale richiesto: " + recipe.getTargetValue());
        targetLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        targetLabel.setTextFill(Color.rgb(211, 84, 0));
        targetLabel.setMaxWidth(Double.MAX_VALUE);

        VBox ingredientsBox = new VBox(5);
        ingredientsBox.setAlignment(Pos.CENTER_LEFT);
        ingredientsBox.setPadding(new Insets(10));
        ingredientsBox.setStyle("-fx-background-color: rgba(212,175,55,0.15); -fx-border-radius: 5;");

        for (Ingredient ing : recipe.getSuggestedIngredients()) {
            Label ingLabel = new Label("  🌿 " + ing.getName() + " (Valore: " + ing.getValue() + ")");
            ingLabel.setFont(Font.font("Courier New", 12));
            ingLabel.setTextFill(Color.rgb(62, 39, 35));
            ingredientsBox.getChildren().add(ingLabel);
        }

        Label hintLabel = new Label(
                "💡 Puoi usare QUALSIASI combinazione di ingredienti che sommati danno " + recipe.getTargetValue());
        hintLabel.setFont(Font.font("Courier New", 11));
        hintLabel.setTextFill(Color.GRAY);
        hintLabel.setWrapText(true);
        hintLabel.setMaxWidth(Double.MAX_VALUE);

        // Pulsanti
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button btnCraft = new Button("️ Vai al Cerchio SHEN");
        btnCraft.setPrefSize(200, 40);
        btnCraft.setStyle(
                "-fx-background-color: #00bcd4; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5;");
        btnCraft.setOnAction(e -> {
            closeOverlay(); // Chiude i dettagli
            showShenCircleOverlay();
        });

        Button btnBack = new Button("⬅️ Torna al Libro");
        btnBack.setPrefSize(180, 40);
        btnBack.setStyle(
                "-fx-background-color: #d4af37; -fx-text-fill: #3e2723; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5;");
        btnBack.setOnAction(e -> {
            closeOverlay();
            showAlchemyBookOverlay();
        });

        buttons.getChildren().addAll(btnCraft, btnBack);

        content.getChildren().addAll(title, descTitle, descText, effectTitle, effectLabel, apLabel, separator,
                craftTitle, targetLabel, ingredientsBox, hintLabel, buttons);
        overlay.getChildren().add(content);
        gameArea.getChildren().add(overlay);
    }

    private void showShenCircleOverlay() {
        closeOverlay();
        disableControls();

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.98);");
        currentOverlay = overlay;

        Label title = new Label("🔮 Cerchio Alchemico - SHEN");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 32));
        title.setTextFill(Color.rgb(0, 188, 212));

        Label slotsInfo = new Label("Slot: " + shenSelectedIngredients.size() + " / " + maxShenSlots);
        slotsInfo.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        slotsInfo.setTextFill(Color.WHITE);

        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getChildren().addAll(title, slotsInfo);

        HBox mainLayout = new HBox(40);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        // 1️⃣ COLONNA SINISTRA: Pulsanti
        VBox leftColumn = new VBox(25);
        leftColumn.setAlignment(Pos.CENTER);

        Button btnInventory = new Button("🎒 Inventario");
        btnInventory.setPrefSize(220, 60);
        btnInventory.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;");
        btnInventory.setOnAction(e -> showShenInventorySelection());

        Button btnTransmute = new Button("⚗️ TRASmuta");
        btnTransmute.setPrefSize(220, 60);
        btnTransmute.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;");
        btnTransmute.setOnAction(e -> performTransmutation());

        Button btnBack = new Button("⬅️ Indietro");
        btnBack.setPrefSize(220, 60);
        btnBack.setStyle(
                "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold; -fx-border-radius: 12; -fx-background-radius: 12;");

        btnBack.setOnAction(e -> {
            for (Ingredient ing : shenSelectedIngredients) {
                engine.getAlchemist().getInventory().addItem(ing);
            }

            shenSelectedIngredients.clear();

            if (currentOverlay != null) {
                gameArea.getChildren().remove(currentOverlay);
                currentOverlay = null;
            }

            enableControls();
        });

        leftColumn.getChildren().addAll(btnInventory, btnTransmute, btnBack);

        // 2️⃣ COLONNA CENTRALE: Lista ingredienti a scorrimento
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefWidth(420);
        scrollPane.setPrefHeight(500);
        scrollPane.setMaxHeight(500);
        scrollPane.setStyle(
                "-fx-background-color: #f0f0f0; -fx-border-color: #00bcd4; -fx-border-width: 3; -fx-border-radius: 10;");
        scrollPane.setFitToWidth(true);

        VBox selectedBox = new VBox(10);
        selectedBox.setPadding(new Insets(15));
        selectedBox.setStyle("-fx-background-color: #ffffff;");

        if (shenSelectedIngredients.isEmpty()) {
            Label empty = new Label("Nessun ingrediente selezionato...\nClicca 'Inventario' per iniziare.");
            empty.setFont(Font.font("Courier New", 16));
            empty.setStyle("-fx-text-fill: black;");
            empty.setPadding(new Insets(20));
            empty.setAlignment(Pos.CENTER);
            selectedBox.getChildren().add(empty);
        } else {
            int totalValue = 0;
            for (Ingredient ing : shenSelectedIngredients) {
                HBox ingRow = new HBox(15);
                ingRow.setAlignment(Pos.CENTER_LEFT);
                ingRow.setPadding(new Insets(10));
                ingRow.setStyle("-fx-background-color: #e8e8e8; -fx-border-radius: 5;");

                Label ingLabel = new Label("🌿 " + ing.getName() + " (Val: " + ing.getValue() + ")");
                ingLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
                ingLabel.setStyle("-fx-text-fill: black;");
                HBox.setHgrow(ingLabel, Priority.ALWAYS);

                Button btnRemove = new Button("❌");
                btnRemove.setPrefSize(35, 35);
                btnRemove.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 17;");
                btnRemove.setOnAction(e -> {
                    engine.getAlchemist().getInventory().addItem(ing);
                    shenSelectedIngredients.remove(ing);
                    showShenCircleOverlay();
                });

                ingRow.getChildren().addAll(ingLabel, btnRemove);
                selectedBox.getChildren().add(ingRow);
                totalValue += ing.getValue();
            }

            Label totalLabel = new Label("💎 Valore Totale: " + totalValue);
            totalLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
            totalLabel.setStyle("-fx-text-fill: #d35400;");
            totalLabel.setPadding(new Insets(15, 0, 0, 0));
            totalLabel.setAlignment(Pos.CENTER);
            selectedBox.getChildren().add(totalLabel);
        }
        scrollPane.setContent(selectedBox);

        VBox centerColumn = new VBox(10);
        centerColumn.setAlignment(Pos.CENTER);
        centerColumn.getChildren().add(scrollPane);

        // 3️⃣ COLONNA DESTRA: Immagine SHEN
        VBox rightColumn = new VBox(20);
        rightColumn.setAlignment(Pos.CENTER);

        Node circleNode;
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/shen.jpeg");
            if (is == null) {
                is = getClass().getResourceAsStream("shen.jpeg");
            }

            if (is != null) {
                System.out.println("✅ SUCCESSO: Immagine 'shen.jpg' caricata correttamente!");
                Image shenImage = new Image(is);
                ImageView iv = new ImageView(shenImage);
                iv.setFitWidth(400);
                iv.setFitHeight(400);
                iv.setPreserveRatio(true);
                iv.setStyle("-fx-effect: dropshadow(gaussian, #00bcd4, 30, 0.7, 0, 0);");
                circleNode = iv;
            } else {
                System.out.println("❌ ERRORE: Immagine 'shen.jpg' NON trovata.");
                System.out.println("Verifica che il file sia in: src/main/resources/shen.jpg");
                circleNode = createFallbackCircle();
            }
        } catch (Exception e) {
            System.out.println("❌ ECCEZIONE caricamento immagine: " + e.getMessage());
            circleNode = createFallbackCircle();
        }

        rightColumn.getChildren().add(circleNode);

        // Assemblaggio colonne
        mainLayout.getChildren().addAll(leftColumn, centerColumn, rightColumn);

        // Contenitore principale fullscreen
        VBox content = new VBox(20);
        content.setPadding(new Insets(20, 30, 10, 30));
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(1250);
        content.setMaxWidth(1250);
        content.setPrefHeight(580);
        content.setMaxHeight(580);
        content.setStyle(
                "-fx-background-color: #0a0a0a; -fx-border-color: #00bcd4; -fx-border-width: 3; -fx-border-radius: 15;");

        content.getChildren().addAll(headerBox, mainLayout);
        overlay.getChildren().add(content);
        gameArea.getChildren().add(overlay);
    }

    // Metodo di riserva se l'immagine non viene trovata
    private Node createFallbackCircle() {
        Circle c = new Circle(180);
        c.setFill(Color.TRANSPARENT);
        c.setStroke(Color.CYAN);
        c.setStrokeWidth(6);
        Label txt = new Label("SHEN\n(Immagine non trovata)");
        txt.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        txt.setTextFill(Color.CYAN);
        txt.setAlignment(Pos.CENTER);
        txt.setMaxWidth(Double.MAX_VALUE);
        StackPane sp = new StackPane(c, txt);
        return sp;
    }

    private void showShenInventorySelection() {
        StackPane selectionOverlay = new StackPane();
        selectionOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.9);");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(750);
        content.setStyle("-fx-background-color: #1e272e; -fx-border-color: #00bcd4; -fx-border-width: 4;");

        Label title = new Label("Seleziona ingredienti per SHEN");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefHeight(300);
        scrollPane.setPrefWidth(700);
        scrollPane.setMaxWidth(700);
        scrollPane.setFitToWidth(true); // FIX: La scroll si adatta alla larghezza
        scrollPane.setStyle("-fx-background-color: #0a0a0a;");

        // GridPane unico per la tabella SHEN (5 colonne)
        GridPane itemsGrid = new GridPane();
        itemsGrid.setHgap(10);
        itemsGrid.setVgap(5);
        itemsGrid.setPadding(new Insets(5));
        itemsGrid.setMaxWidth(Double.MAX_VALUE); // FIX: La griglia si espande

        // Definiamo le colonne
        ColumnConstraints colIcon = new ColumnConstraints(30);
        ColumnConstraints colQty = new ColumnConstraints(40);
        ColumnConstraints colName = new ColumnConstraints();
        colName.setHgrow(Priority.ALWAYS); // Il nome si espande per riempire lo spazio
        ColumnConstraints colVal = new ColumnConstraints(80);
        ColumnConstraints colAdd = new ColumnConstraints(100);
        itemsGrid.getColumnConstraints().addAll(colIcon, colQty, colName, colVal, colAdd);

        List<Item> items = engine.getAlchemist().getInventory().getItems();
        Map<String, List<Item>> grouped = items.stream()
                .filter(i -> i instanceof Ingredient)
                .collect(Collectors.groupingBy(Item::getName));

        int rowIndex = 0;
        for (Map.Entry<String, List<Item>> entry : grouped.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue().size();
            Ingredient sample = (Ingredient) entry.getValue().get(0);

            // Sfondo riga
            HBox rowBackground = new HBox();
            rowBackground.setStyle(
                    "-fx-background-color: #2c3e50; -fx-border-color: #00bcd4; -fx-border-width: 1; -fx-border-radius: 5;");
            rowBackground.setPrefHeight(45);
            rowBackground.setMaxWidth(Double.MAX_VALUE);
            GridPane.setFillWidth(rowBackground, true); // FIX: Sfondo si espande
            itemsGrid.add(rowBackground, 0, rowIndex, 5, 1);

            // Colonna 0: Icona
            Label itemIcon = new Label("");
            itemIcon.setFont(Font.font("Courier New", 22));
            GridPane.setFillWidth(itemIcon, true); // FIX: Si espande nella cella
            itemsGrid.add(itemIcon, 0, rowIndex);

            // Colonna 1: Quantità
            Label quantityLabel = new Label(qty + "x");
            quantityLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
            quantityLabel.setTextFill(Color.WHITE);
            GridPane.setFillWidth(quantityLabel, true); // FIX: Si espande nella cella
            itemsGrid.add(quantityLabel, 1, rowIndex);

            // Colonna 2: Nome
            Label nameLabel = new Label(name);
            nameLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
            nameLabel.setTextFill(Color.rgb(46, 204, 113));
            GridPane.setFillWidth(nameLabel, true); // FIX: Si espande nella cella
            itemsGrid.add(nameLabel, 2, rowIndex);

            // Colonna 3: Valore
            Label valueLabel = new Label("Val: " + sample.getValue());
            valueLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
            valueLabel.setTextFill(Color.rgb(241, 196, 15));
            valueLabel.setAlignment(Pos.CENTER);
            GridPane.setFillWidth(valueLabel, true); // FIX: Si espande nella cella
            itemsGrid.add(valueLabel, 3, rowIndex);

            // Colonna 4: Pulsante Aggiungi
            HBox addBox = new HBox();
            addBox.setAlignment(Pos.CENTER);
            Button btnAdd = new Button("+ Aggiungi");
            btnAdd.setPrefSize(90, 30);
            btnAdd.setStyle(
                    "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 12; -fx-font-weight: bold; -fx-border-radius: 5;");
            btnAdd.setOnAction(e -> {
                if (shenSelectedIngredients.size() < maxShenSlots) {
                    shenSelectedIngredients.add(sample);
                    engine.getAlchemist().getInventory().removeItem(sample);
                    gameArea.getChildren().remove(selectionOverlay);
                    showShenCircleOverlay();
                } else {
                    engine.addMessage("Slot pieni! Livello massimo: " + maxShenSlots);
                }
            });
            addBox.getChildren().add(btnAdd);
            GridPane.setFillWidth(addBox, true); // FIX: Si espande nella cella
            itemsGrid.add(addBox, 4, rowIndex);

            rowIndex++;
        }

        scrollPane.setContent(itemsGrid);

        Button btnBack = new Button("⬅️ Indietro");
        btnBack.setPrefSize(150, 40);
        btnBack.setStyle(
                "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 5;");
        btnBack.setOnAction(e -> gameArea.getChildren().remove(selectionOverlay));

        content.getChildren().addAll(title, scrollPane, btnBack);
        selectionOverlay.getChildren().add(content);
        gameArea.getChildren().add(selectionOverlay);
    }

    private void performTransmutation() {
        System.out.println("🔍 DEBUG: performTransmutation chiamato");

        if (shenSelectedIngredients.isEmpty()) {
            engine.addMessage("⚠️ Seleziona almeno un ingrediente!");
            updateDialog();
            return;
        }

        int totalValue = 0;
        for (Ingredient ing : shenSelectedIngredients) {
            totalValue += ing.getValue();
        }

        boolean success = false;
        String resultMessage = "";

        // Controllo Elisir dell'Immortalità
        if (totalValue == 628 && shenSelectedIngredients.size() == 4) {
            if (shenSelectedIngredients.get(0).getName().equals("Erba Lunare") &&
                    shenSelectedIngredients.get(1).getName().equals("Osso Corrotto") &&
                    shenSelectedIngredients.get(2).getName().equals("Liquido Instabile") &&
                    shenSelectedIngredients.get(3).getName().equals("Frammento di Vuoto")) {

                Potion elisir = new Potion("Elisir dell'Immortalità", "HP e AP infiniti!", 999, 0,
                        new PotionEffect(PotionEffect.EffectType.HEAL, 9999, 0));

                for (Ingredient ing : shenSelectedIngredients) {
                    engine.getAlchemist().getInventory().removeItem(ing);
                }

                engine.getAlchemist().getInventory().addItem(elisir);
                engine.getAlchemist().addExperience(500);
                resultMessage = "🌑 HAI CREATO L'ELISIR DELL'IMMORTALITÀ! 🌑\n+500 XP guadagnati!";
                success = true;
            }
        }

        if (!success) {
            Recipe found = recipeDatabase.getRecipeByValue(totalValue);
            if (found != null) {
                for (Ingredient ing : shenSelectedIngredients) {
                    engine.getAlchemist().getInventory().removeItem(ing);
                }

                engine.getAlchemyBook().discoverRecipe(found);
                engine.getAlchemist().getInventory().addItem(found.getResultPotion());
                engine.getAlchemist().addExperience(50);
                resultMessage = "✨ TRASmutazione RIUSCITA! ✨\nHai creato: " + found.getName() + "!\n+50 XP guadagnati!";
                success = true;
            } else {
                resultMessage = "💥 TRASmutazione FALLITA! 💥\nGli ingredienti sono andati perduti...\nValore: "
                        + totalValue;
                for (Ingredient ing : shenSelectedIngredients) {
                    engine.getAlchemist().getInventory().removeItem(ing);
                }
            }
        }

        engine.addMessage(resultMessage);
        shenSelectedIngredients.clear();
        closeOverlay();
        updateStats();
        updateDialog();

        checkPendingLevelUp();
    }

    // --- COMBATTIMENTO ---
    private void switchToCombatMode(String monsterName) {
        inCombat = true;
        showingPotionMenu = false;
        currentMonster = monsters[currentRoom - 1][playerX][playerY];
        if (currentMonster == null) {
            currentMonster = new Monster(monsterName, 50, 10, 5, 20,
                    Arrays.asList(new Ingredient("Drop Test", "Test", 10, 1)), "Fuoco", false);
            monsters[currentRoom - 1][playerX][playerY] = currentMonster;
        }
        btnUp.setDisable(true);
        btnDown.setDisable(true);
        btnLeft.setDisable(true);
        btnRight.setDisable(true);

        // ✅ FIX: Assicuriamoci di usare l'actionBox che è effettivamente nella scena
        if (actionBox == null) {
            // Se actionBox è null, proviamo a trovarlo in bottomControls
            // oppure lo ricreiamo e lo aggiungiamo (rimuovendo eventuali duplicati)
            actionBox = createActionButtons();
            if (bottomControls != null) {
                // Rimuovi eventuali vecchi actionBox per evitare duplicati
                bottomControls.getChildren()
                        .removeIf(node -> node instanceof HBox && ((HBox) node).getChildren().stream()
                                .anyMatch(child -> child instanceof Button && ((Button) child).getText().equals("A")));
                bottomControls.getChildren().add(actionBox);
            }
        }

        actionBox.getChildren().clear();
        Button btnAttack = new Button("⚔️ Attacco Base");
        btnAttack.setPrefSize(120, 50);
        btnAttack.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 10;");
        btnAttack.setOnAction(e -> handleCombatAttack());
        Button btnPotions = new Button("🧪 Pozioni");
        btnPotions.setPrefSize(120, 50);
        btnPotions.setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 10;");
        btnPotions.setOnAction(e -> handleShowPotions());
        Button btnFlee = new Button("🏃 Fuga");
        btnFlee.setPrefSize(120, 50);
        btnFlee.setStyle(
                "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-border-radius: 10;");
        btnFlee.setOnAction(e -> handleCombatFlee());
        actionBox.getChildren().addAll(btnAttack, btnPotions, btnFlee);
        engine.addMessage("⚠️ " + monsterName + " (HP: " + currentMonster.getHp() + "/" + currentMonster.getMaxHp()
                + ") ti blocca la strada!");
        if (engine.getAlchemist().hasDiscoveredWeakness(monsterName)) {
            engine.addMessage(" Debolezza nota: " + getElementEmoji(currentMonster.getWeakness()) + " "
                    + currentMonster.getWeakness());
            engine.addMessage("️ Resistenza nota: " + getElementEmoji(currentMonster.getResistance()) + " "
                    + currentMonster.getResistance());
        } else {
            engine.addMessage("[Debolezza: ???] Usa 'Occulus Veritatis' per scoprirla!");
        }
        updateDialog();
    }

    private String getElementEmoji(String element) {
        if (element == null)
            return "⚪";
        switch (element) {
            case "Fuoco":
                return "🔥";
            case "Luce":
                return "✨";
            case "Veleno":
                return "☠️";
            case "Acqua":
                return "💧";
            case "Terra":
                return "🪨";
            case "Ombra":
                return "🌑";
            default:
                return "⚪";
        }
    }

    private String formatPotionEffect(PotionEffect effect) {
        if (effect == null)
            return "Nessun effetto";
        switch (effect.getType()) {
            case HEAL:
                return "❤️ Cura " + effect.getMagnitude() + " HP";
            case DAMAGE:
                return "💥 Infligge " + effect.getMagnitude() + " danni";
            case BUFF_ATTACK:
                return "⚔️ ATT +" + effect.getMagnitude()
                        + (effect.getDuration() > 0 ? " (" + effect.getDuration() + " turni)" : "");
            case BUFF_DEFENSE:
                return "🛡️ DEF +" + effect.getMagnitude()
                        + (effect.getDuration() > 0 ? " (" + effect.getDuration() + " turni)" : "");
            case DEBUFF_ATTACK:
                return "📉 ATT Nemico -" + effect.getMagnitude();
            case REGEN:
                return "💚 Rigenera " + effect.getMagnitude() + " HP/turno (" + effect.getDuration() + " turni)";
            case SHIELD:
                return "🛡️ Scudo (schiva 1 attacco)";
            case ANALYZE:
                return "🔍 Rivela debolezze";
            case SPECIAL:
                return "✨ Effetto Speciale";
            default:
                return effect.getType().toString();
        }
    }

    private void switchToExplorationMode() {
        inCombat = false;
        showingPotionMenu = false;
        currentMonster = null;
        btnUp.setDisable(false);
        btnDown.setDisable(false);
        btnLeft.setDisable(false);
        btnRight.setDisable(false);
        actionBox.getChildren().clear();
        btnA = new Button("A");
        btnA.setPrefSize(70, 70);
        btnA.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 28; -fx-font-weight: bold; -fx-border-radius: 35; -fx-background-radius: 35;");
        btnA.setOnAction(e -> handleActionA());
        actionBox.getChildren().add(btnA);
    }

    private void handleCombatAttack() {
        if (currentMonster == null)
            return;

        // 1. ✅ INIZIO TURNO: Applica veleni e aggiorna buff
        String startTurnLog = combatSystem.processStartOfTurn(engine.getAlchemist(), currentMonster);
        if (!startTurnLog.isEmpty()) {
            engine.addMessage(startTurnLog);
        }

        // 2. Controlla se il mostro è morto per il veleno PRIMA dell'attacco
        if (currentMonster.isDead()) {
            engine.addMessage("🎉 " + currentMonster.getName() + " è morto per gli effetti del veleno!");
            processMonsterVictory(); // Usa il blocco di vittoria (vedi sotto)
            return;
        }

        // 3. Attacco dell'alchimista
        int damage = combatSystem.alchemistAttack(engine.getAlchemist(), currentMonster);
        engine.addMessage("⚔️ Attacco Base: infliggi " + damage + " danni a " + currentMonster.getName() + "!");
        engine.addMessage(
                currentMonster.getName() + " HP: " + currentMonster.getHp() + "/" + currentMonster.getMaxHp());

        // 4. Controlla se il mostro è morto dopo l'attacco
        if (currentMonster.isDead()) {
            engine.addMessage("🎉 Hai sconfitto " + currentMonster.getName() + "!");
            processMonsterVictory();
            return;
        }

        // 5. Il mostro è ancora vivo, contrattacca
        int damageTaken = combatSystem.monsterAttack(currentMonster, engine.getAlchemist());
        engine.addMessage(currentMonster.getName() + " contrattacca! Subisci " + damageTaken + " danni.");
        engine.addMessage("I tuoi HP: " + engine.getAlchemist().getHp() + "/" + engine.getAlchemist().getMaxHp());

        if (engine.getAlchemist().isDead()) {
            handleDeath();
            return;
        }

        updateStats();
        updateDialog();
    }

    private void processMonsterVictory() {
        int room = currentRoom - 1;

        // 1. Logica specifica del Boss (Respawn)
        if (currentMonster.isBoss()) {
            bossDefeatedTimes.put(currentRoom, System.currentTimeMillis());
            engine.addMessage("⏰ Il boss impiegherà " + BOSS_RESPAWN_MINUTES + " minuti per rigenerarsi...");
        }

        // 2. Delega a CombatSystem la gestione di Drop, Inventario e XP
        List<Item> drops = combatSystem.processVictory(engine.getAlchemist(), engine.getAlchemyBook(), currentMonster);

        // 3. Mostra a schermo cosa ha droppato
        if (!drops.isEmpty()) {
            engine.addMessage("Il mostro ha droppato:");
            for (Item drop : drops) {
                engine.addMessage("  + " + drop.getName());
            }
        }

        // 4. Aggiorna la mappa (rimuovi il mostro)
        if (currentMonster.isBoss()) {
            currentMap[room][playerX][playerY] = TileConstants.TILE_PATH;
        } else {
            currentMap[room][playerX][playerY] = TileConstants.TILE_GRASS;
        }
        monsters[room][playerX][playerY] = null;
        monsterNames[room][playerX][playerY] = null;

        // 5. Aggiorna l'interfaccia grafica
        drawGrid();
        switchToExplorationMode();
        updateStats();
        updateDialog();

        // 6. Controllo Level Up ritardato di 2 secondi
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), e -> checkPendingLevelUp()));
        delay.setCycleCount(1);
        delay.play();
    }

    private void handleUsePotion(Potion potion) {
        Alchemist alchemist = engine.getAlchemist();

        // 1. Controllo AP
        if (alchemist.getAp() < potion.getApCost()) {
            engine.addMessage("❌ AP insufficienti per " + potion.getName() + "! (Costo: " + potion.getApCost() + ")");
            updateDialog();
            updateStats();
            handleShowPotions();
            return;
        }

        // 2. Verifica bersaglio
        if (currentMonster == null) {
            currentMonster = monsters[currentRoom - 1][playerX][playerY];
        }
        if (currentMonster == null) {
            engine.addMessage("Nessun bersaglio valido!");
            updateDialog();
            switchToExplorationMode();
            return;
        }

        // 3. Esecuzione effetto pozione
        int result = engine.getCombatSystem().usePotion(alchemist, potion, currentMonster);
        if (result == -1) {
            engine.addMessage("❌ Errore nell'uso della pozione (AP insufficienti o effetto non valido)!");
            updateDialog();
            updateStats();
            handleShowPotions();
            return;
        }

        // 4. Rimuovi la pozione dall'inventario
        alchemist.getInventory().getItems().stream()
                .filter(i -> i.getName().equals(potion.getName()))
                .findFirst()
                .ifPresent(i -> alchemist.getInventory().removeItem(i));

        // 5. Messaggio di successo (✅ PULITO: senza duplicazioni!)
        if (potion.getEffect().getType() == PotionEffect.EffectType.HEAL) {
            engine.addMessage(
                    "💚 Usi " + potion.getName() + "! Recuperi " + potion.getEffect().getMagnitude() + " HP!");
            engine.addMessage("I tuoi HP: " + alchemist.getHp() + "/" + alchemist.getMaxHp());
        } else if (potion.getEffect().getType() == PotionEffect.EffectType.DAMAGE) {
            engine.addMessage("🧪 Lanci " + potion.getName() + " su " + currentMonster.getName() + "! Infliggi "
                    + result + " danni!");
        } else if (potion.getEffect().getType() == PotionEffect.EffectType.ANALYZE) {
            engine.addMessage("✨ Usi Occulus Veritatis su " + currentMonster.getName() + "!");
            engine.addMessage("🔍 DEBOLEZZA SCOPERTA: " + getElementEmoji(currentMonster.getWeakness()) + " "
                    + currentMonster.getWeakness());
            engine.addMessage("🛡️ RESISTENZA SCOPERTA: " + getElementEmoji(currentMonster.getResistance()) + " "
                    + currentMonster.getResistance());
            engine.addMessage("📖 Registrato nel Bestiario!");
        } else {
            engine.addMessage("🧪 Usi " + potion.getName() + "! Effetto applicato con successo.");
        }

        // 6. Controllo morte mostro (✅ SOSTITUITO IL BLOCCO GIGANTE CON 3 RIGHE PULITE)
        if (currentMonster.isDead()) {
            engine.addMessage("🎉 Hai sconfitto " + currentMonster.getName() + " con la pozione!");

            closeOverlay(); // Chiude il menu pozioni
            processMonsterVictory(); // Delega la logica di drop, XP e mappa al metodo unico

            return; // Esci, non mostrare il menu delle pozioni se il mostro è morto
        }

        // 7. Il mostro è vivo, contrattacca!
        int damageTaken = engine.getCombatSystem().monsterAttack(currentMonster, alchemist);
        engine.addMessage(currentMonster.getName() + " contrattacca! Subisci " + damageTaken + " danni.");
        engine.addMessage("I tuoi HP: " + alchemist.getHp() + "/" + alchemist.getMaxHp());

        if (alchemist.isDead()) {
            closeOverlay();
            handleDeath();
            return;
        }

        // 8. Aggiorna UI e riapri il menu pozioni
        updateStats();
        updateDialog();
        handleShowPotions();
    }

    private void handleCombatFlee() {
        if (currentMonster == null) {
            currentMonster = monsters[currentRoom - 1][playerX][playerY];
        }
        if (currentMonster == null) {
            return;
        }

        // ✅ FIX: Blocca la fuga da QUALSIASI boss, non solo dal Guardiano
        if (currentMonster.isBoss()) {
            engine.addMessage("🚫 Impossibile fuggire da un Boss! Devi combatterlo fino alla fine!");
            updateDialog();
            return;
        }

        // Logica di fuga normale per i mostri comuni
        if (Math.random() < 0.5) {
            engine.addMessage("🏃 Sei fuggito con successo!");
            playerX = 7;
            playerY = 7;
            drawGrid();
            switchToExplorationMode();
            engine.addMessage("Sei tornato al centro della stanza.");
            updateDialog();
        } else {
            int damageTaken = engine.getCombatSystem().monsterAttack(currentMonster, engine.getAlchemist());
            engine.addMessage(
                    "❌ Fuga fallita! " + currentMonster.getName() + " ti attacca! Subisci " + damageTaken + " danni.");
            engine.addMessage("I tuoi HP: " + engine.getAlchemist().getHp() + "/" + engine.getAlchemist().getMaxHp());

            if (engine.getAlchemist().isDead()) {
                handleDeath();
                return;
            }
            updateDialog();
        }
        updateStats();
    }

    // --- MOVIMENTO E MAPPA ---
    private void handleMove(String direction) {
        dialogArea.clear();

        if (inCombat || showingBook || showingPotionMenu || showingInventory) {
            engine.addMessage("Non puoi muoverti ora!");
            updateDialog();
            return;
        }

        int newX = playerX;
        int newY = playerY;

        switch (direction) {
            case "nord":
                newY--;
                break;
            case "sud":
                newY++;
                break;
            case "est":
                newX++;
                break;
            case "ovest":
                newX--;
                break;
        }

        int room = currentRoom - 1;

        if (newX >= 0 && newX < GRID_SIZE && newY >= 0 && newY < GRID_SIZE) {
            int targetTile = currentMap[room][newX][newY];

            // ==========================================
            // 1. GESTIONE PORTE NORMALI
            // ==========================================
            if (targetTile == TileConstants.TILE_DOOR) {
                // --- LIVELLO 1: FORESTA ---
                if (currentRoom == 1 && direction.equals("nord")) {
                    changeRoom(2, 7, 12);
                    return;
                } else if (currentRoom == 2 && direction.equals("sud")) {
                    changeRoom(1, 7, 2);
                    return;
                } else if (currentRoom == 2 && direction.equals("nord")) {
                    showBossEntranceConfirmation(3, 7, 12);
                    return;
                }

                // --- LIVELLO 2: CAVERNE ---
                else if (currentRoom == 4 && direction.equals("sud")) {
                    engine.addMessage("🚪 Ritorni verso la tana del Guardiano...");
                    changeRoom(3, 7, 2);
                    return;
                } else if (currentRoom == 4 && direction.equals("nord")) {
                    engine.addMessage("🚪 Prosegui nelle profondità delle Caverne Umide...");
                    changeRoom(5, 7, 12);
                    return;
                } else if (currentRoom == 5 && direction.equals("sud")) {
                    engine.addMessage("🚪 Torni indietro verso l'ingresso delle Caverne...");
                    changeRoom(4, 7, 2);
                    return;
                } else if (currentRoom == 5 && direction.equals("nord")) {
                    showBossEntranceConfirmation(6, 7, 12);
                    return;
                }

                // --- LIVELLO 3: PALUDE ---
                else if (currentRoom == 7 && direction.equals("nord")) {
                    engine.addMessage("🚪 Prosegui nel cuore della Palude...");
                    changeRoom(8, 7, 12);
                    return;
                } else if (currentRoom == 7 && direction.equals("sud")) {
                    engine.addMessage("🚪 Torni indietro verso le Caverne...");
                    changeRoom(6, 7, 12);
                    return;
                } else if (currentRoom == 8 && direction.equals("sud")) {
                    engine.addMessage("🚪 Torni verso l'ingresso della Palude...");
                    changeRoom(7, 7, 2);
                    return;
                } else if (currentRoom == 8 && direction.equals("nord")) {
                    showBossEntranceConfirmation(9, 7, 12);
                    return;
                } else if (currentRoom == 9 && direction.equals("sud")) {
                    int bossX = 7;
                    int bossY = 4;

                    if (monsters[8][bossX][bossY] != null && !monsters[8][bossX][bossY].isDead()) {
                        engine.addMessage("🔒 L'aura del Basilisco blocca la fuga! Sconfiggilo per aprire l'uscita!");
                    } else {
                        engine.addMessage("🚪 Il Basilisco è stato sconfitto! La via di fuga è aperta.");
                        changeRoom(8, 7, 2);
                    }
                    updateDialog();
                    return;
                }
            }

            // ==========================================
            // 2. GESTIONE PORTE NASCOSTE
            // ==========================================
            else if (targetTile == TileConstants.TILE_HIDDEN_DOOR) {
                int bossX = 7;
                int bossY = 4;

                if (currentRoom == 3 && direction.equals("sud")) {
                    if (monsters[2][bossX][bossY] != null && !monsters[2][bossX][bossY].isDead()) {
                        engine.addMessage("🔒 Impossibile fuggire, un'aura schiacciante vieta la fuga!");
                    } else {
                        engine.addMessage("🚪 L'aura oscura è svanita. La via del ritorno è aperta.");
                        changeRoom(2, 7, 2);
                    }
                    updateDialog();
                    return;
                } else if (currentRoom == 3 && direction.equals("nord")) {
                    if (monsters[2][bossX][bossY] == null || monsters[2][bossX][bossY].isDead()) {
                        engine.addMessage("🚪 La via verso le Caverne Umide (Livello 2) è aperta!");
                        changeRoom(4, 7, 12);
                    } else {
                        engine.addMessage("🔒 Un'aura oscura blocca l'USCITA. Sconfiggi il Guardiano prima.");
                    }
                    updateDialog();
                    return;
                } else if (currentRoom == 6 && direction.equals("sud")) {
                    int bossX2 = 7;
                    int bossY2 = 4;

                    if (monsters[5][bossX2][bossY2] != null && !monsters[5][bossX2][bossY2].isDead()) {
                        engine.addMessage("🔒 Il Golem di Pietra blocca l'uscita! Sconfiggilo prima!");
                    } else {
                        engine.addMessage("🚪 Il Golem è stato sconfitto! La via verso le Caverne è aperta.");
                        changeRoom(5, 7, 2);
                    }
                    updateDialog();
                    return;
                } else if (currentRoom == 6 && direction.equals("nord")) {
                    int bossX2 = 7;
                    int bossY2 = 4;

                    if (monsters[5][bossX2][bossY2] != null && !monsters[5][bossX2][bossY2].isDead()) {
                        engine.addMessage("🔒 Il Golem di Pietra blocca l'uscita verso la Palude! Sconfiggilo prima!");
                    } else {
                        engine.addMessage(
                                "🚪 Il Golem è stato sconfitto! La via verso la Palude Maleodorante è aperta.");
                        changeRoom(7, 7, 12);
                    }
                    updateDialog();
                    return;
                }
            }

            // ==========================================
            // 3. MOVIMENTO NORMALE
            // ==========================================
            if (targetTile != TileConstants.TILE_WALL && targetTile != TileConstants.TILE_TREE) {
                playerX = newX;
                playerY = newY;
                drawGrid();

                // ✅ Gestione oggetti/mostri
                checkTileEvent(newX, newY);
            } else {
                // Messaggio di ostacolo dinamico
                if (targetTile == TileConstants.TILE_TREE) {
                    String theme = themeManager.getThemeForRoom(currentRoom);
                    switch (theme) {
                        case "FORESTA":
                            engine.addMessage("🌲 Un albero blocca la strada!");
                            break;
                        case "CAVERNE":
                            engine.addMessage("🪨 Una roccia ostruisce il passaggio!");
                            break;
                        case "PALUDE":
                            engine.addMessage("🌿 Un groviglio di radici marce ti blocca!");
                            break;
                        case "ROVINE":
                            engine.addMessage("🏛️ Una colonna crollata ostruisce la via!");
                            break;
                        case "VULCANO":
                            engine.addMessage("🌋 Lava solidificata ti impedisce di passare!");
                            break;
                        case "LABORATORIO":
                            engine.addMessage("⚗️ Macchinari rotti bloccano il cammino!");
                            break;
                        case "ABISSO":
                            engine.addMessage("🌑 Ombre dense e tangibili ti bloccano!");
                            break;
                        case "TORRE":
                            engine.addMessage("✨ Cristalli magici ti respingono!");
                            break;
                        case "ASTRALE":
                            engine.addMessage("🌌 La realtà si piega e ti impedisce di avanzare!");
                            break;
                        case "CITTADELLA":
                            engine.addMessage("⚔️ Armature corrotte sbarrano il passaggio!");
                            break;
                        default:
                            engine.addMessage("Un ostacolo blocca la strada!");
                    }
                } else {
                    engine.addMessage("La strada è bloccata!");
                }
                updateDialog();
            }
        }
        updateStats();
    }

    private void checkTileEvent(int x, int y) {
        int room = currentRoom - 1;
        int tile = currentMap[room][x][y];

        if (tile == TileConstants.TILE_ITEM) {
            engine.addMessage("✨ Vedi a terra: " + itemNames[room][x][y] + "! Premi A per raccoglierlo.");
            updateDialog();
        } else if (tile == TileConstants.TILE_MONSTER || tile == TileConstants.TILE_BOSS) {
            if (monsters[room][x][y] != null && !monsters[room][x][y].isDead()) {
                switchToCombatMode(monsterNames[room][x][y]);
            }
        }
    }

    private void handleActionA() {
        if (inCombat || showingBook || showingPotionMenu || showingInventory)
            return;
        int room = currentRoom - 1;
        int tile = currentMap[room][playerX][playerY];
        if (tile == TileConstants.TILE_HIDDEN_DOOR) {
            if (currentRoom == 3 && playerY == GRID_SIZE - 2) {
                if (monsters[2][7][4] == null || monsters[2][7][4].isDead()) {
                    engine.addMessage("Torni indietro alla Stanza 2...");
                    updateDialog();
                    changeRoom(2, 7, 3);
                    return;
                } else {
                    engine.addMessage("Impossibile fuggire, un'aura schiacciante vieta la fuga!");
                    updateDialog();
                    return;
                }
            }
        }
        if (tile == TileConstants.TILE_ITEM) {
            String itemName = itemNames[room][playerX][playerY];
            Item item = itemsOnMap[room][playerX][playerY];
            if (item != null && engine.getAlchemist().getInventory().addItem(item)) {
                engine.addMessage("✨ Hai raccolto: " + itemName + "!");
                if (item instanceof Ingredient) {
                    engine.getAlchemyBook().discoverIngredient(item.getName());
                    engine.getAlchemist().addExperience(5);
                    checkPendingLevelUp();
                    updateStats();
                }
            } else
                engine.addMessage("Inventario pieno! Non puoi raccogliere " + itemName + ".");
            currentMap[room][playerX][playerY] = TileConstants.TILE_GRASS;
            itemNames[room][playerX][playerY] = null;
            itemsOnMap[room][playerX][playerY] = null;
            drawGrid();
        } else if (tile == TileConstants.TILE_MONSTER || tile == TileConstants.TILE_BOSS) {
            if (monsters[room][playerX][playerY] != null && !monsters[room][playerX][playerY].isDead())
                switchToCombatMode(monsterNames[room][playerX][playerY]);
            else
                engine.addMessage("Il mostro è già stato sconfitto.");
        } else
            engine.addMessage("Non c'è nulla di interessante qui.");
        updateDialog();
    }

    private void handleDeath() {
        dialogArea.clear();

        // 1. Delega a GameEngine la logica di gioco (penalità, reset stats, messaggi)
        engine.handlePlayerDeath();

        // 2. Reset della posizione e dello stato di combattimento (Logica UI)
        currentRoom = 1;
        playerX = 7;
        playerY = 13;
        inCombat = false;
        currentMonster = null;

        // 3. Aggiornamento dell'interfaccia grafica
        switchToExplorationMode();
        drawGrid();
        updateStats();
        updateDialog();
    }

    private void showBossEntranceConfirmation(int targetRoom, int spawnX, int spawnY) {
        long currentTime = System.currentTimeMillis();
        long respawnTimeMillis = BOSS_RESPAWN_MINUTES * 60 * 1000;

        // ✅ FIX: Controlla il tempo di sconfitta DELLO SPECIFICO boss della stanza
        // targetRoom
        Long defeatTime = bossDefeatedTimes.get(targetRoom);

        // Il boss è vivo se: non è mai stato sconfitto (null) OPPURE è passato
        // abbastanza tempo per il respawn
        boolean bossIsAlive = (defeatTime == null) ||
                ((currentTime - defeatTime) >= respawnTimeMillis);

        if (bossIsAlive) {
            // Boss è VIVO: messaggio di conferma
            disableControls();

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.9);");
            currentOverlay = overlay;

            VBox content = new VBox(20);
            content.setPadding(new Insets(30));
            content.setAlignment(Pos.CENTER);
            content.setMaxWidth(500);
            content.setStyle(
                    "-fx-background-color: #2c0a0a; -fx-border-color: #e74c3c; -fx-border-width: 4; -fx-border-radius: 10;");

            Label title = new Label("⚠️ Attenzione!");
            title.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
            title.setTextFill(Color.rgb(231, 76, 60));

            Label message = new Label(
                    "Un'aura oscura si annida oltre questa porta...\nSei sicuro/a di voler entrare?");
            message.setFont(Font.font("Courier New", 16));
            message.setTextFill(Color.WHITE);
            message.setWrapText(true);
            message.setAlignment(Pos.CENTER);
            message.setMaxWidth(Double.MAX_VALUE);

            HBox buttons = new HBox(20);
            buttons.setAlignment(Pos.CENTER);

            Button btnYes = new Button("Sì, entra!");
            btnYes.setPrefSize(150, 40);
            btnYes.setStyle(
                    "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-border-radius: 5;");
            btnYes.setOnAction(e -> {
                closeOverlay();
                changeRoom(targetRoom, spawnX, spawnY);
            });

            Button btnNo = new Button("No, torna indietro");
            btnNo.setPrefSize(150, 40);
            btnNo.setStyle(
                    "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-border-radius: 5;");
            btnNo.setOnAction(e -> {
                closeOverlay();
                engine.addMessage("Saggia scelta quella di tornare indietro");
                updateDialog();
            });

            buttons.getChildren().addAll(btnYes, btnNo);
            content.getChildren().addAll(title, message, buttons);
            overlay.getChildren().add(content);
            gameArea.getChildren().add(overlay);

        } else {
            // Boss è MORTO: Via libera con countdown
            disableControls();

            long timeSinceDefeat = currentTime - defeatTime;
            long minutesLeft = BOSS_RESPAWN_MINUTES - (timeSinceDefeat / (60 * 1000));
            long secondsLeft = 60 - ((timeSinceDefeat / 1000) % 60);

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.9);");
            currentOverlay = overlay;

            VBox content = new VBox(20);
            content.setPadding(new Insets(30));
            content.setAlignment(Pos.CENTER);
            content.setMaxWidth(500);
            content.setStyle(
                    "-fx-background-color: #1a1a2e; -fx-border-color: #27ae60; -fx-border-width: 4; -fx-border-radius: 10;");

            Label title = new Label("✅ Via Libera");
            title.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
            title.setTextFill(Color.rgb(39, 174, 96));

            Label message = new Label(
                    "Il Boss è stato sconfitto.\nLa via è sicura.\n\n⚠️ Attenzione: L'aura si sta rigenerando!\nTempo rimanente: "
                            + String.format("%02d:%02d", minutesLeft, secondsLeft));
            message.setFont(Font.font("Courier New", 16));
            message.setTextFill(Color.WHITE);
            message.setWrapText(true);
            message.setAlignment(Pos.CENTER);
            message.setMaxWidth(Double.MAX_VALUE);

            HBox buttons = new HBox(20);
            buttons.setAlignment(Pos.CENTER);

            Button btnEnter = new Button("Entra");
            btnEnter.setPrefSize(150, 40);
            btnEnter.setStyle(
                    "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-border-radius: 5;");
            btnEnter.setOnAction(e -> {
                closeOverlay();
                changeRoom(targetRoom, spawnX, spawnY);
            });

            Button btnBack = new Button("Indietro");
            btnBack.setPrefSize(150, 40);
            btnBack.setStyle(
                    "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-border-radius: 5;");
            btnBack.setOnAction(e -> {
                closeOverlay();
            });

            buttons.getChildren().addAll(btnEnter, btnBack);
            content.getChildren().addAll(title, message, buttons);
            overlay.getChildren().add(content);
            gameArea.getChildren().add(overlay);
        }
    }

    // --- UI HELPERS ---
    private void drawGrid() {
        if (gameGrid == null)
            return;
        gameGrid.getChildren().clear();
        int room = currentRoom - 1;
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                int tile = currentMap[room][x][y];
                Color color = themeManager.getTileColor(tile, currentRoom);
                cell.setFill(color);

                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(0.5);
                gameGrid.add(cell, x, y);
            }
        }
        Rectangle player = new Rectangle(cellSize - 4, cellSize - 4);
        player.setFill(Color.BLUE);
        player.setStroke(Color.WHITE);
        player.setStrokeWidth(2);
        gameGrid.add(player, playerX, playerY);
    }

    private void updateStats() {
        Alchemist alchemist = engine.getAlchemist();

        hpLabel.setText("HP: " + alchemist.getHp() + "/" + alchemist.getMaxHp());
        apLabel.setText("AP: " + alchemist.getAp() + "/" + alchemist.getMaxAp());
        atkLabel.setText("ATT: " + alchemist.getAttack());
        defLabel.setText("DEF: " + alchemist.getDefense());
        levelLabel.setText("LIV: " + alchemist.getLevel());
        xpLabel.setText("XP: " + alchemist.getExperience() + "/" + alchemist.getXpForNextLevel());
        roomLabel.setText("Stanza: " + currentRoom);

        StringBuilder effectsDisplay = new StringBuilder("Effetti: ");
        for (ActiveEffect e : engine.getAlchemist().getActiveEffects()) {
            effectsDisplay.append(e.toString()).append(" ");
        }
    }

    private void updateDialog() {
        StringBuilder sb = new StringBuilder();
        while (!engine.getMessageLog().isEmpty()) {
            String msg = engine.getMessageLog().poll();
            sb.append(msg);
            if (!engine.getMessageLog().isEmpty())
                sb.append("\n");
        }
        if (sb.length() > 0)
            dialogArea.setText(sb.toString());
    }

    private void closeOverlay() {
        if (currentOverlay != null) {
            gameArea.getChildren().remove(currentOverlay);
            currentOverlay = null;
        }

        showingInventory = false;
        showingBook = false;
        showingPotionMenu = false;

        if (bottomControls != null) {
            bottomControls.setVisible(true);
            bottomControls.setManaged(true);
        }

        enableControls();
    }

    // FIX: Refresh inventario pulito
    private void refreshInventoryOverlay() {
        if (currentOverlay != null) {
            gameArea.getChildren().remove(currentOverlay);
            currentOverlay = null;
        }
        // Riapre l'inventario da zero senza resettare i controlli
        showInventoryOverlay();
    }

    // (Cerca il btnClose dentro showInventoryOverlay() e metti solo questo)
    // btnClose.setOnAction(e -> closeOverlay());

    private void disableControls() {
        btnUp.setDisable(true);
        btnDown.setDisable(true);
        btnLeft.setDisable(true);
        btnRight.setDisable(true);
        btnA.setDisable(true);
    }

    private void enableControls() {
        if (!inCombat) {
            btnUp.setDisable(false);
            btnDown.setDisable(false);
            btnLeft.setDisable(false);
            btnRight.setDisable(false);
            btnA.setDisable(false);
        }
    }

    private StackPane createGameArea() {
        StackPane area = new StackPane();
        area.setStyle("-fx-background-color: #1a1a1a;");
        area.setPadding(new Insets(10));
        gameGrid = new GridPane();
        gameGrid.setHgap(0);
        gameGrid.setVgap(0);
        gameGrid.setStyle("-fx-border-color: #7f8c8d; -fx-border-width: 3;");
        StackPane.setAlignment(gameGrid, Pos.CENTER);
        area.getChildren().add(gameGrid);
        return area;
    }

    private HBox createBottomControls() {
        HBox bar = new HBox(15);
        bar.setPadding(new Insets(10)); // Padding ridotto per dare più spazio ai contenuti
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("-fx-background-color: #34495e;");

        // 1. DPad: Bloccato alla sua dimensione naturale, non si stirerà mai
        VBox dpad = createDPad();
        dpad.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // 2. DialogArea: Usa le righe di testo invece dei pixel fissi
        dialogArea = new TextArea();
        dialogArea.setPrefRowCount(6);
        dialogArea.setMaxHeight(140);
        dialogArea.setEditable(false);
        dialogArea.setWrapText(true);
        dialogArea.setStyle(
                "-fx-background-color: #1a1a1a; -fx-text-fill: #2ecc71; -fx-font-family: 'Courier New'; -fx-font-size: 14; -fx-control-inner-background: #1a1a1a;");
        HBox.setHgrow(dialogArea, Priority.ALWAYS);

        // 3. ActionBox: Bloccato alla sua dimensione naturale
        HBox actionBox = createActionButtons();
        actionBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        bar.getChildren().addAll(dpad, dialogArea, actionBox);
        bar.setFillHeight(true);

        return bar;
    }

    private VBox createDPad() {
        VBox dpad = new VBox(5);
        dpad.setAlignment(Pos.CENTER);

        // ✅ AGGIUNGI QUESTA RIGA: blocca il DPad alle sue dimensioni naturali
        dpad.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER);
        btnUp = createDirectionButton("▲", 60, 40); // ✅ Dimensioni originali
        btnUp.setOnAction(e -> handleMove("nord"));
        topRow.getChildren().add(btnUp);

        HBox middleRow = new HBox(5);
        middleRow.setAlignment(Pos.CENTER);
        btnLeft = createDirectionButton("◀", 40, 40); // ✅ Dimensioni originali
        btnLeft.setOnAction(e -> handleMove("ovest"));
        btnRight = createDirectionButton("▶", 40, 40); // ✅ Dimensioni originali
        btnRight.setOnAction(e -> handleMove("est"));
        middleRow.getChildren().addAll(btnLeft, btnRight);

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER);
        btnDown = createDirectionButton("▼", 60, 40); // ✅ Dimensioni originali
        btnDown.setOnAction(e -> handleMove("sud"));
        bottomRow.getChildren().add(btnDown);

        dpad.getChildren().addAll(topRow, middleRow, bottomRow);
        return dpad;
    }

    private Button createDirectionButton(String text, int width, int height) {
        Button btn = new Button(text);
        btn.setPrefSize(width, height);
        btn.setStyle(
                "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 20; -fx-border-color: #7f8c8d; -fx-border-radius: 5;");
        return btn;
    }

    private HBox createActionButtons() {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER);
        btnA = new Button("A");
        btnA.setPrefSize(70, 70);
        btnA.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 28; -fx-font-weight: bold; -fx-border-radius: 35; -fx-background-radius: 35;");
        btnA.setOnAction(e -> handleActionA());
        box.getChildren().add(btnA);
        return box;
    }

    private void handleShowPotions() {
        if (currentOverlay != null) {
            gameArea.getChildren().remove(currentOverlay);
        }

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.85);");
        currentOverlay = overlay;

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(550);
        content.setStyle(
                "-fx-background-color: #1a1a2e; -fx-border-color: #8e44ad; -fx-border-width: 3; -fx-border-radius: 10;");

        Label title = new Label("🧪 Inventario Pozioni");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        title.setTextFill(Color.rgb(142, 68, 173)); // Viola

        VBox potionList = new VBox(10);
        potionList.setAlignment(Pos.CENTER);

        List<Potion> potions = engine.getAlchemist().getInventory().getItems().stream()
                .filter(item -> item instanceof Potion)
                .map(item -> (Potion) item)
                .collect(Collectors.toList());

        if (potions.isEmpty()) {
            Label emptyMsg = new Label("Non hai pozioni nell'inventario!");
            emptyMsg.setFont(Font.font("Courier New", 16));
            emptyMsg.setTextFill(Color.LIGHTGRAY);
            potionList.getChildren().add(emptyMsg);
        } else {
            Map<String, List<Potion>> groupedPotions = potions.stream()
                    .collect(Collectors.groupingBy(Potion::getName));

            for (Map.Entry<String, List<Potion>> entry : groupedPotions.entrySet()) {
                Potion p = entry.getValue().get(0);
                int quantity = entry.getValue().size();

                Button potionBtn = new Button();
                potionBtn.setMaxWidth(Double.MAX_VALUE);
                potionBtn.setAlignment(Pos.CENTER_LEFT);

                String effectText = formatPotionEffect(p.getEffect());
                // Formattazione: Nome | Effetto | Quantità
                potionBtn.setText(String.format(" %-22s | %-32s | x%d", p.getName(), effectText, quantity));
                potionBtn.setFont(Font.font("Courier New", 13));
                potionBtn.setStyle(
                        "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-padding: 10; -fx-border-color: #8e44ad; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

                // Hover effect
                potionBtn.setOnMouseEntered(e -> potionBtn.setStyle(
                        "-fx-background-color: #34495e; -fx-text-fill: white; -fx-padding: 10; -fx-border-color: #ffd700; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;"));
                potionBtn.setOnMouseExited(e -> potionBtn.setStyle(
                        "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-padding: 10; -fx-border-color: #8e44ad; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;"));

                potionBtn.setOnAction(e -> {
                    closeOverlay();
                    handleUsePotion(p);
                });

                potionList.getChildren().add(potionBtn);
            }
        }

        Button btnClose = new Button("Chiudi");
        btnClose.setPrefSize(150, 40);
        btnClose.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-border-radius: 5;");
        btnClose.setOnAction(e -> closeOverlay());

        content.getChildren().addAll(title, potionList, btnClose);
        overlay.getChildren().add(content);
        gameArea.getChildren().add(overlay);
    }

    public static void main(String[] args) {
        launch(args);
    }
}