package football.manager;

import football.model.Player;
import football.model.PlayerStatus;
import football.model.Position;
import football.model.RegularPlayer;
import football.model.StarPlayer;
import football.util.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayerManager {
    private final List<Player> players = new ArrayList<>();

    public Player addPlayer(String playerType, String playerId, String fullName, int age, String nationality,
                            Position position, int shirtNumber, long baseSalary, PlayerStatus status) {
        String normalizedId = InputValidator.normalizeId(playerId, "Player ID");
        String validName = InputValidator.requireText(fullName, "Full name");
        String validNationality = InputValidator.requireText(nationality, "Nationality");
        validatePlayerValues(age, position, shirtNumber, baseSalary, status);
        ensureUniquePlayerId(normalizedId);
        if (status == PlayerStatus.ACTIVE) {
            ensureActiveShirtAvailable(shirtNumber, normalizedId);
        }

        Player player;
        if ("1".equals(playerType) || "Regular Player".equalsIgnoreCase(playerType)
                || "Regular".equalsIgnoreCase(playerType)) {
            player = new RegularPlayer(normalizedId, validName, age, validNationality,
                    position, shirtNumber, baseSalary, status);
        } else if ("2".equals(playerType) || "Star Player".equalsIgnoreCase(playerType)
                || "Star".equalsIgnoreCase(playerType)) {
            player = new StarPlayer(normalizedId, validName, age, validNationality,
                    position, shirtNumber, baseSalary, status);
        } else {
            throw new IllegalArgumentException("Player type must be Regular Player or Star Player.");
        }

        players.add(player);
        return player;
    }

    public void updatePlayer(String playerId, Position newPosition, int newShirtNumber,
                             long newBaseSalary, PlayerStatus newStatus) {
        Player player = requirePlayer(playerId);
        validatePlayerValues(player.getAge(), newPosition, newShirtNumber, newBaseSalary, newStatus);
        if (newStatus == PlayerStatus.ACTIVE) {
            ensureActiveShirtAvailable(newShirtNumber, player.getPlayerId());
        }
        player.setPosition(newPosition);
        player.setShirtNumber(newShirtNumber);
        player.setBaseSalary(newBaseSalary);
        player.setStatus(newStatus);
    }

    public void deactivatePlayer(String playerId) {
        Player player = requirePlayer(playerId);
        player.setStatus(PlayerStatus.INACTIVE);
    }

    public Player requirePlayer(String playerId) {
        String normalizedId = InputValidator.normalizeId(playerId, "Player ID");
        return findPlayer(normalizedId)
                .orElseThrow(() -> new IllegalArgumentException("Player ID not found: " + normalizedId));
    }

    public Optional<Player> findPlayer(String playerId) {
        String normalizedId = InputValidator.normalizeId(playerId, "Player ID");
        return players.stream()
                .filter(player -> player.getPlayerId().equalsIgnoreCase(normalizedId))
                .findFirst();
    }

    public List<Player> getAllPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<Player> getActivePlayers() {
        return players.stream()
                .filter(Player::isActive)
                .collect(Collectors.toList());
    }

    public List<Player> searchPlayers(int searchType, String keyword) {
        String lowerKeyword = InputValidator.requireText(keyword, "Keyword").toLowerCase(Locale.ROOT);
        return players.stream()
                .filter(player -> matches(player, searchType, lowerKeyword))
                .collect(Collectors.toList());
    }

    public void clear() {
        players.clear();
    }

    private boolean matches(Player player, int searchType, String lowerKeyword) {
        switch (searchType) {
            case 1:
                return player.getFullName().toLowerCase(Locale.ROOT).contains(lowerKeyword);
            case 2:
                return player.getPosition().getDisplayName().toLowerCase(Locale.ROOT).contains(lowerKeyword);
            case 3:
                return player.getNationality().toLowerCase(Locale.ROOT).contains(lowerKeyword);
            case 4:
                return player.getStatus().getDisplayName().toLowerCase(Locale.ROOT).contains(lowerKeyword);
            default:
                throw new IllegalArgumentException("Search type must be from 1 to 4.");
        }
    }

    private void validatePlayerValues(int age, Position position, int shirtNumber,
                                      long baseSalary, PlayerStatus status) {
        InputValidator.validateAge(age);
        if (position == null) {
            throw new IllegalArgumentException("Position is required.");
        }
        InputValidator.validateShirtNumber(shirtNumber);
        InputValidator.validateBaseSalary(baseSalary);
        if (status == null) {
            throw new IllegalArgumentException("Status is required.");
        }
    }

    private void ensureUniquePlayerId(String playerId) {
        if (players.stream().anyMatch(player -> player.getPlayerId().equalsIgnoreCase(playerId))) {
            throw new IllegalArgumentException("Player ID already exists: " + playerId);
        }
    }

    private void ensureActiveShirtAvailable(int shirtNumber, String excludedPlayerId) {
        boolean used = players.stream()
                .anyMatch(player -> player.isActive()
                        && player.getShirtNumber() == shirtNumber
                        && !player.getPlayerId().equalsIgnoreCase(excludedPlayerId));
        if (used) {
            throw new IllegalArgumentException("Two active players cannot have the same shirt number.");
        }
    }
}
