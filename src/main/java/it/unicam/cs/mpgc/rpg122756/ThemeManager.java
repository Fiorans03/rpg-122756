package it.unicam.cs.mpgc.rpg122756;

import javafx.scene.paint.Color;

public class ThemeManager {

    // ==========================================
    // SEZIONE: METODO PRINCIPALE DI RENDERING
    // ==========================================

    public Color getTileColor(int tile, int currentRoom) {
        String theme = getThemeForRoom(currentRoom);
        switch (tile) {
            case TileConstants.TILE_WALL:
                return getWallColor(theme);
            case TileConstants.TILE_GRASS:
                return getFloorColor(theme);
            case TileConstants.TILE_PATH:
                return getPathColor(theme);
            case TileConstants.TILE_TREE:
                return getObstacleColor(theme);
            case TileConstants.TILE_DOOR:
                return Color.SADDLEBROWN;
            case TileConstants.TILE_HIDDEN_DOOR:
                return getHiddenDoorColor(theme);
            case TileConstants.TILE_MONSTER:
                return getMonsterColor(theme);
            case TileConstants.TILE_BOSS:
                return Color.rgb(139, 0, 0);
            case TileConstants.TILE_ITEM:
                return getItemColor(theme);
            default:
                return Color.BLACK;
        }
    }

    // ==========================================
    // SEZIONE: DETERMINAZIONE TEMA
    // ==========================================

    public String getThemeForRoom(int room) {
        if (room >= 1 && room <= 3)
            return "FORESTA";
        if (room >= 4 && room <= 6)
            return "CAVERNE";
        if (room >= 7 && room <= 9)
            return "PALUDE";
        if (room >= 10 && room <= 12)
            return "ROVINE";
        if (room >= 13 && room <= 15)
            return "VULCANO";
        if (room >= 16 && room <= 18)
            return "LABORATORIO";
        if (room >= 19 && room <= 21)
            return "ABISSO";
        if (room >= 22 && room <= 24)
            return "TORRE";
        if (room >= 25 && room <= 27)
            return "ASTRALE";
        if (room >= 28 && room <= 30)
            return "CITTADELLA";
        return "FORESTA";
    }

    // ==========================================
    // SEZIONE: COLORI SPECIFICI PER TIPO DI TILE
    // ==========================================

    private Color getWallColor(String theme) {
        switch (theme) {
            case "FORESTA":
                return Color.rgb(101, 67, 33);
            case "CAVERNE":
                return Color.rgb(30, 30, 30);
            case "PALUDE":
                return Color.rgb(40, 50, 30);
            case "ROVINE":
                return Color.rgb(80, 80, 80);
            case "VULCANO":
                return Color.rgb(60, 20, 20);
            case "LABORATORIO":
                return Color.rgb(50, 50, 70);
            case "ABISSO":
                return Color.rgb(10, 10, 20);
            case "TORRE":
                return Color.rgb(70, 50, 90);
            case "ASTRALE":
                return Color.rgb(20, 30, 60);
            case "CITTADELLA":
                return Color.rgb(40, 10, 10);
            default:
                return Color.DARKGRAY;
        }
    }

    private Color getFloorColor(String theme) {
        switch (theme) {
            case "FORESTA":
                return Color.rgb(34, 139, 34);
            case "CAVERNE":
                return Color.rgb(50, 50, 50);
            case "PALUDE":
                return Color.rgb(60, 80, 40);
            case "ROVINE":
                return Color.rgb(100, 90, 70);
            case "VULCANO":
                return Color.rgb(80, 40, 30);
            case "LABORATORIO":
                return Color.rgb(70, 70, 80);
            case "ABISSO":
                return Color.rgb(20, 20, 30);
            case "TORRE":
                return Color.rgb(90, 70, 110);
            case "ASTRALE":
                return Color.rgb(40, 50, 80);
            case "CITTADELLA":
                return Color.rgb(60, 20, 20);
            default:
                return Color.rgb(34, 139, 34);
        }
    }

    private Color getPathColor(String theme) {
        switch (theme) {
            case "FORESTA":
                return Color.rgb(210, 180, 140);
            case "CAVERNE":
                return Color.rgb(80, 80, 80);
            case "PALUDE":
                return Color.rgb(90, 100, 60);
            default:
                return getFloorColor(theme);
        }
    }

    private Color getObstacleColor(String theme) {
        switch (theme) {
            case "FORESTA":
                return Color.rgb(0, 100, 0);
            case "CAVERNE":
                return Color.rgb(30, 30, 30);
            case "PALUDE":
                return Color.rgb(50, 60, 30);
            case "ROVINE":
                return Color.rgb(120, 110, 90);
            case "VULCANO":
                return Color.rgb(100, 50, 40);
            case "LABORATORIO":
                return Color.rgb(80, 80, 100);
            case "ABISSO":
                return Color.rgb(30, 30, 40);
            case "TORRE":
                return Color.rgb(100, 80, 130);
            case "ASTRALE":
                return Color.rgb(50, 60, 100);
            case "CITTADELLA":
                return Color.rgb(80, 30, 30);
            default:
                return Color.rgb(0, 100, 0);
        }
    }

    private Color getHiddenDoorColor(String theme) {
        return Color.rgb(184, 134, 11);
    }

    private Color getMonsterColor(String theme) {
        return getFloorColor(theme);
    }

    private Color getItemColor(String theme) {
        return getFloorColor(theme);
    }
}