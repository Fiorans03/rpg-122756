package it.unicam.cs.mpgc.rpg122756.game.world;

import it.unicam.cs.mpgc.rpg122756.model.entities.Monster;
import java.util.Map;

public class NavigationManager {

    public static final long BOSS_RESPAWN_MINUTES = 30;

    public enum MoveResult {
        NORMAL_MOVE,
        ROOM_CHANGE,
        BOSS_CONFIRMATION,
        BLOCKED_EXIT,
        HIDDEN_DOOR,
        BLOCKED
    }

    private int targetRoom;
    private int targetX;
    private int targetY;
    private String message;

    public MoveResult handleDoor(int currentRoom, String direction, Monster[][][] monsters,
            Map<Integer, Long> bossDefeatedTimes) {
        targetRoom = 0;
        targetX = 0;
        targetY = 0;
        message = "";

        // ==========================================
        // LIVELLO 1: FORESTA
        // ==========================================
        if (currentRoom == 1 && direction.equals("nord")) {
            targetRoom = 2;
            targetX = 7;
            targetY = 12;
            return MoveResult.ROOM_CHANGE;
        }
        if (currentRoom == 2 && direction.equals("sud")) {
            targetRoom = 1;
            targetX = 7;
            targetY = 2;
            return MoveResult.ROOM_CHANGE;
        }
        if (currentRoom == 2 && direction.equals("nord")) {
            targetRoom = 3;
            targetX = 7;
            targetY = 12;
            return MoveResult.BOSS_CONFIRMATION;
        }

        // ==========================================
        // LIVELLO 2: CAVERNE
        // ==========================================
        if (currentRoom == 4 && direction.equals("sud")) {
            // ✅ TORNANDO ALLA TANA DEL GUARDIANO: controlla stato boss
            targetRoom = 3;
            targetX = 7;
            targetY = 2;
            message = getBossRoomMessage(3, monsters, bossDefeatedTimes);
            return MoveResult.ROOM_CHANGE;
        }
        if (currentRoom == 4 && direction.equals("nord")) {
            targetRoom = 5;
            targetX = 7;
            targetY = 12;
            message = "🚪 Prosegui nelle profondità delle Caverne Umide...";
            return MoveResult.ROOM_CHANGE;
        }
        if (currentRoom == 5 && direction.equals("sud")) {
            targetRoom = 4;
            targetX = 7;
            targetY = 2;
            message = "🚪 Torni indietro verso l'ingresso delle Caverne...";
            return MoveResult.ROOM_CHANGE;
        }
        if (currentRoom == 5 && direction.equals("nord")) {
            targetRoom = 6;
            targetX = 7;
            targetY = 12;
            return MoveResult.BOSS_CONFIRMATION;
        }

        // ==========================================
        // LIVELLO 3: PALUDE
        // ==========================================
        if (currentRoom == 7 && direction.equals("nord")) {
            targetRoom = 8;
            targetX = 7;
            targetY = 12;
            message = "🚪 Prosegui nel cuore della Palude...";
            return MoveResult.ROOM_CHANGE;
        }
        if (currentRoom == 7 && direction.equals("sud")) {
            // ✅ TORNANDO ALLA TANA DEL GOLEM: controlla stato boss
            targetRoom = 6;
            targetX = 7;
            targetY = 12;
            message = getBossRoomMessage(6, monsters, bossDefeatedTimes);
            return MoveResult.ROOM_CHANGE;
        }
        if (currentRoom == 8 && direction.equals("sud")) {
            targetRoom = 7;
            targetX = 7;
            targetY = 2;
            message = "🚪 Torni verso l'ingresso della Palude...";
            return MoveResult.ROOM_CHANGE;
        }
        if (currentRoom == 8 && direction.equals("nord")) {
            targetRoom = 9;
            targetX = 7;
            targetY = 12;
            return MoveResult.BOSS_CONFIRMATION;
        }
        if (currentRoom == 9 && direction.equals("sud")) {
            int bossX = 7;
            int bossY = 4;
            if (monsters[8][bossX][bossY] != null && !monsters[8][bossX][bossY].isDead()) {
                message = "🔒 L'aura del Basilisco blocca la fuga! Sconfiggilo per aprire l'uscita!";
                return MoveResult.BLOCKED_EXIT;
            } else {
                targetRoom = 8;
                targetX = 7;
                targetY = 2;
                message = "🚪 Il Basilisco è stato sconfitto! La via di fuga è aperta.";
                return MoveResult.ROOM_CHANGE;
            }
        }

        return MoveResult.BLOCKED;
    }

    public MoveResult handleHiddenDoor(int currentRoom, String direction, Monster[][][] monsters,
            Map<Integer, Long> bossDefeatedTimes) {
        targetRoom = 0;
        targetX = 0;
        targetY = 0;
        message = "";

        int bossX = 7;
        int bossY = 4;

        // ==========================================
        // Stanza 3 (Boss Guardiano)
        // ==========================================
        if (currentRoom == 3 && direction.equals("sud")) {
            if (monsters[2][bossX][bossY] != null && !monsters[2][bossX][bossY].isDead()) {
                message = "🔒 Impossibile fuggire, un'aura schiacciante vieta la fuga!";
                return MoveResult.BLOCKED_EXIT;
            } else {
                targetRoom = 2;
                targetX = 7;
                targetY = 2;
                message = "🚪 L'aura oscura è svanita. La via del ritorno è aperta.";
                return MoveResult.ROOM_CHANGE;
            }
        }
        if (currentRoom == 3 && direction.equals("nord")) {
            if (monsters[2][bossX][bossY] == null || monsters[2][bossX][bossY].isDead()) {
                targetRoom = 4;
                targetX = 7;
                targetY = 12;
                message = "🚪 La via verso le Caverne Umide (Livello 2) è aperta!";
                return MoveResult.ROOM_CHANGE;
            } else {
                message = "🔒 Un'aura oscura blocca l'USCITA. Sconfiggi il Guardiano prima.";
                return MoveResult.BLOCKED_EXIT;
            }
        }

        // ==========================================
        // Stanza 6 (Boss Golem)
        // ==========================================
        if (currentRoom == 6 && direction.equals("sud")) {
            if (monsters[5][bossX][bossY] != null && !monsters[5][bossX][bossY].isDead()) {
                message = "🔒 Il Golem di Pietra blocca l'uscita! Sconfiggilo prima!";
                return MoveResult.BLOCKED_EXIT;
            } else {
                targetRoom = 5;
                targetX = 7;
                targetY = 2;
                message = "🚪 Il Golem è stato sconfitto! La via verso le Caverne è aperta.";
                return MoveResult.ROOM_CHANGE;
            }
        }
        if (currentRoom == 6 && direction.equals("nord")) {
            if (monsters[5][bossX][bossY] != null && !monsters[5][bossX][bossY].isDead()) {
                message = "🔒 Il Golem di Pietra blocca l'uscita verso la Palude! Sconfiggilo prima!";
                return MoveResult.BLOCKED_EXIT;
            } else {
                targetRoom = 7;
                targetX = 7;
                targetY = 12;
                message = "🚪 Il Golem è stato sconfitto! La via verso la Palude Maleodorante è aperta.";
                return MoveResult.ROOM_CHANGE;
            }
        }

        return MoveResult.BLOCKED;
    }

    // ==========================================
    // METODO HELPER: Calcola il messaggio per le stanze dei boss
    // ==========================================
    private String getBossRoomMessage(int bossRoom, Monster[][][] monsters, Map<Integer, Long> bossDefeatedTimes) {
        int bossIndex = bossRoom - 1;
        int bossX = 7;
        int bossY = 4;

        String bossName = getBossName(bossRoom);

        // Boss vivo (non ancora sconfitto o respawnato)
        if (monsters[bossIndex][bossX][bossY] != null && !monsters[bossIndex][bossX][bossY].isDead()) {
            return "⚠️ " + bossName + " è nella sua tana! Preparati al combattimento!";
        }

        // Controlla se è stato sconfitto di recente
        Long defeatTime = bossDefeatedTimes.get(bossRoom);
        if (defeatTime != null) {
            long currentTime = System.currentTimeMillis();
            long respawnTimeMillis = BOSS_RESPAWN_MINUTES * 60 * 1000;
            long timeSinceDefeat = currentTime - defeatTime;

            if (timeSinceDefeat < respawnTimeMillis) {
                // Boss morto, in fase di respawn
                long minutesLeft = BOSS_RESPAWN_MINUTES - (timeSinceDefeat / (60 * 1000));
                long secondsLeft = 60 - ((timeSinceDefeat / 1000) % 60);
                return "⚠️ " + bossName + " si sta rigenerando!\nTempo rimanente: " +
                        String.format("%02d:%02d", minutesLeft, secondsLeft);
            }
        }

        // Boss respawnato (torna ad essere "vivo" logicamente)
        return "🚪 Ritorni verso la tana di " + bossName + "...";
    }

    private String getBossName(int room) {
        switch (room) {
            case 3:
                return "il Guardiano della Foresta";
            case 6:
                return "il Golem di Pietra";
            case 9:
                return "il Basilisco della Palude";
            default:
                return "il Boss";
        }
    }

    // Getters
    public int getTargetRoom() {
        return targetRoom;
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public String getMessage() {
        return message;
    }
}