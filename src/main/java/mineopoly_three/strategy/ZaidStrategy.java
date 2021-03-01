package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.tiles.TileType;
import mineopoly_three.util.DistanceUtil;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ZaidStrategy implements MinePlayerStrategy {
  private int inventorySize = 0;
  private int maxInventorySize;
  private int maxCharge;
  private int boardSize;
  private ArrayList<Point> marketTiles = new ArrayList<>();
  private ArrayList<Point> rechargeStations = new ArrayList<>();

  /**
   * Called at the start of every round
   *
   * @param boardSize The length and width of the square game board
   * @param maxInventorySize The maximum number of items that your player can carry at one time
   * @param maxCharge The amount of charge your robot starts with (number of tile moves before
   *     needing to recharge)
   * @param winningScore The first player to reach this score wins the round
   * @param startingBoard A view of the GameBoard at the start of the game. You can use this to
   *     pre-compute fixed information, like the locations of market or recharge tiles
   * @param startTileLocation A Point representing your starting location in (x, y) coordinates (0,
   *     0) is the bottom left and (boardSize - 1, boardSize - 1) is the top right
   * @param isRedPlayer True if this strategy is the red player, false otherwise
   * @param random A random number generator, if your strategy needs random numbers you should use
   *     this.
   */
  @Override
  public void initialize(
      int boardSize,
      int maxInventorySize,
      int maxCharge,
      int winningScore,
      PlayerBoardView startingBoard,
      Point startTileLocation,
      boolean isRedPlayer,
      Random random) {
    inventorySize = 0;
    this.maxInventorySize = maxInventorySize;
    this.maxCharge = maxCharge;
    this.boardSize = boardSize;

    for (int row = 0; row < boardSize; row++) {
      for (int col = 0; col < boardSize; col++) {
        if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RECHARGE) {
          rechargeStations.add(new Point(row, col));
        } else if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RED_MARKET) {
          marketTiles.add(new Point(row, col));
        }
      }
    }
  }

  /**
   * The main part of your strategy, this method returns what action your player should do on this
   * turn
   *
   * @param boardView A PlayerBoardView object representing all the information about the board and
   *     the other player that your strategy is allowed to access
   * @param economy The GameEngine's economy object which holds current prices for resources
   * @param currentCharge The amount of charge your robot has (number of tile moves before needing
   *     to recharge)
   * @param isRedTurn For use when two players attempt to move to the same spot on the same turn If
   *     true: The red player will move to the spot, and the blue player will do nothing If false:
   *     The blue player will move to the spot, and the red player will do nothing
   * @return The TurnAction enum for the action that this strategy wants to perform on this game
   *     turn
   */
  @Override
  public TurnAction getTurnAction(
      PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
    Point yourLocation = boardView.getYourLocation();
    Point closestMarket = findClosestMarketLocation(yourLocation);
    Point closestResource = findClosestResourceTile(yourLocation, findAllResourceTiles(boardView));

    TurnAction goToResource = computeMovement(closestResource, yourLocation);
    TurnAction goToRecharge = computeMovement(rechargeStations.get(0), yourLocation);
    TurnAction goToMarket = computeMovement(closestMarket, yourLocation);

    List<InventoryItem> itemOnGround = boardView.getItemsOnGround().get(yourLocation);

    if (currentCharge <= maxCharge / 4 && goToRecharge != null) {
      return goToRecharge;
    } else if (isOnRecharge(yourLocation, currentCharge)) {
      return null;
    }

    if (!itemOnGround.isEmpty() && inventorySize < 5) {
      inventorySize++;
      return TurnAction.PICK_UP_RESOURCE;
    }

    if (inventorySize == maxInventorySize && goToMarket != null) {
      return goToMarket;
    } else if (closestMarket.equals(yourLocation)) {
      inventorySize = 0;
    }

    if (goToResource != null) {
      return goToResource;
    } else if (closestResource.equals(yourLocation)) {
      return TurnAction.MINE;
    }

    return null;
  }

  /**
   * Called when the player receives an item from performing a TurnAction that gives an item. At the
   * moment this is only from using PICK_UP on top of a mined resource
   *
   * @param itemReceived The item received from the player's TurnAction on their last turn
   */
  @Override
  public void onReceiveItem(InventoryItem itemReceived) {}

  /**
   * Called when the player steps on a market tile with items to sell. Tells your strategy how much
   * all of the items sold for.
   *
   * @param totalSellPrice The combined sell price for all items in your strategy's inventory
   */
  @Override
  public void onSoldInventory(int totalSellPrice) {}

  /**
   * Gets the name of this strategy. The amount of characters that can actually be displayed on a
   * screen varies, although by default at screen size 750 it's about 16-20 characters depending on
   * character size
   *
   * @return The name of your strategy for use in the competition and rendering the scoreboard on
   *     the GUI
   */
  @Override
  public String getName() {
    return "ZaidStrategy";
  }

  /**
   * Called at the end of every round to let players reset, and tell them how they did if the
   * strategy does not track that for itself
   *
   * @param pointsScored The total number of points this strategy scored
   * @param opponentPointsScored The total number of points the opponent's strategy scored
   */
  @Override
  public void endRound(int pointsScored, int opponentPointsScored) {
    inventorySize = 0;
    boardSize = 0;
    maxInventorySize = 0;
    maxCharge = 0;
    rechargeStations.clear();
    marketTiles.clear();
  }

  private boolean isOnRecharge(Point yourLocation, int currentCharge) {
    for (Point rechargeStation : rechargeStations) {
      if (yourLocation.equals(rechargeStation) && currentCharge != maxCharge) {
        return true;
      }
    }
    return false;
  }

  private TurnAction computeMovement(Point targetLocation, Point initialLocation) {
    if (targetLocation.y < initialLocation.y) {
      return TurnAction.MOVE_DOWN;
    } else if (targetLocation.y > initialLocation.y) {
      return TurnAction.MOVE_UP;
    }
    if (targetLocation.x < initialLocation.x) {
      return TurnAction.MOVE_LEFT;
    } else if (targetLocation.x > initialLocation.x) {
      return TurnAction.MOVE_RIGHT;
    }
    return null;
  }

  private Point findClosestMarketLocation(Point yourLocation) {
    Point closestMarket;

    if (DistanceUtil.getManhattanDistance(marketTiles.get(0), yourLocation)
        > DistanceUtil.getManhattanDistance(marketTiles.get(1), yourLocation)) {
      closestMarket = marketTiles.get(1);
    } else {
      closestMarket = marketTiles.get(0);
    }

    return closestMarket;
  }

  private Point findClosestResourceTile(Point yourLocation, ArrayList<Point> resources) {
    Point firstLocation = resources.get(0);

    for (Point closestLocation : resources) {
      if (DistanceUtil.getManhattanDistance(firstLocation, yourLocation)
          > DistanceUtil.getManhattanDistance(closestLocation, yourLocation)) {
        firstLocation = closestLocation;
      }
    }
    return firstLocation;
  }

  private ArrayList<Point> findAllResourceTiles(PlayerBoardView boardView) {
    ArrayList<Point> resources = new ArrayList<>();
    for (int row = 0; row < boardSize; row++) {
      for (int col = 0; col < boardSize; col++) {
        if (boardView.getTileTypeAtLocation(row, col) == TileType.RESOURCE_DIAMOND
            || boardView.getTileTypeAtLocation(row, col) == TileType.RESOURCE_EMERALD) {
          resources.add(new Point(row, col));
        }
      }
    }
    return resources;
  }

  public void setInventorySize(int inventorySize) {
    this.inventorySize = inventorySize;
  }
}
