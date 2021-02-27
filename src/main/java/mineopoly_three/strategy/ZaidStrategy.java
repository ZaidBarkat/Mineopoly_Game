package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.TileType;
import mineopoly_three.util.DistanceUtil;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

public class ZaidStrategy implements MinePlayerStrategy {
  private int inventorySize = 0;
  private Point[] marketTile = new Point[2];
  private ArrayList<Point> rechargeStations = new ArrayList<>();
  private ArrayList<Point> rubyLocations = new ArrayList<>();
  private ArrayList<Point> emeraldLocations = new ArrayList<>();
  private ArrayList<Point> resourceLocations = new ArrayList<>();

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
    marketTile[1] = startTileLocation;
    inventorySize = 0;

    for (int row = 0; row < boardSize; row++) {
      for (int col = 0; col < boardSize; col++) {
        if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RESOURCE_DIAMOND || startingBoard.getTileTypeAtLocation(row, col) == TileType.RESOURCE_EMERALD) {
          resourceLocations.add(new Point(row, col));
        } else if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RESOURCE_RUBY) {
          rubyLocations.add(new Point(row, col));
        } else if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RESOURCE_EMERALD) {
          emeraldLocations.add(new Point(row, col));
        } else if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RECHARGE) {
          rechargeStations.add(new Point(row, col));
        } else if (isRedPlayer) {
          if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RED_MARKET) {
            marketTile[0] = new Point(row, col);
          }
        } else if (startingBoard.getTileTypeAtLocation(row, col) == TileType.BLUE_MARKET) {
          marketTile[0] = new Point(row, col);
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
    Point yourLocationFromRecharge = differenceInDimension(rechargeStations.get(0), yourLocation);
    Point yourLocationFromMarketAbove = differenceInDimension(marketTile[0], yourLocation);
    Point yourLocationFromMarketBelow = differenceInDimension(marketTile[1], yourLocation);
    Point closestMarket;
    Point closestResource;

    if (currentCharge <= 20) {
      if (robotMovement(yourLocationFromRecharge) != null) {
        return robotMovement(yourLocationFromRecharge);
      }
    }
    for (Point rechargeStation: rechargeStations) {
      if (yourLocation.equals(rechargeStation) && currentCharge != 80) {
        return null;
      }
    }

    closestResource = findClosestResourceLocation(yourLocation);

    for (Map.Entry<Point, List<InventoryItem>> itemsOnGround :
        boardView.getItemsOnGround().entrySet()) {
      if (!itemsOnGround.getValue().isEmpty() && inventorySize < 5) {
        Point locationOfItem = itemsOnGround.getKey().getLocation();

        if (locationOfItem.equals(yourLocation)) {
          inventorySize++;
          resourceLocations.remove(closestResource);
          return TurnAction.PICK_UP_RESOURCE;
        }
      }
    }

    closestMarket =
        closestMarketLocation(
            yourLocationFromMarketBelow, yourLocationFromMarketAbove);
    if (goToMarket(closestMarket) != null) {
      return goToMarket(closestMarket);
    }

    Point movingToResource = differenceInDimension(closestResource, yourLocation);
    if (robotMovement(movingToResource) != null) {
      return robotMovement(movingToResource);
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
  public void onReceiveItem(InventoryItem itemReceived) {
//    if (prioritizeDiamond) {
//      diamondLocations.remove(resourcePrioritized);
//    } else {
//      emeraldLocations.remove(resourcePrioritized);
//    }
  }

  /**
   * Called when the player steps on a market tile with items to sell. Tells your strategy how much
   * all of the items sold for.
   *
   * @param totalSellPrice The combined sell price for all items in your strategy's inventory
   */
  @Override
  public void onSoldInventory(int totalSellPrice) {
//    if (totalSellPrice >= 2250) {
//      prioritizeDiamond = false;
//    } else {
//      prioritizeDiamond = true;
//    }
  }

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
    resourceLocations.clear();
    emeraldLocations.clear();
    rubyLocations.clear();
    rechargeStations.clear();
  }

  private TurnAction robotMovement(Point dimensions) {
    if (dimensions.y < 0) {
      return TurnAction.MOVE_DOWN;
    } else if (dimensions.y > 0) {
      return TurnAction.MOVE_UP;
    }
    if (dimensions.x < 0) {
      return TurnAction.MOVE_LEFT;
    } else if (dimensions.x > 0) {
      return TurnAction.MOVE_RIGHT;
    }
    return null;
  }

  public Point differenceInDimension(Point tileToBeReached, Point currentTile) {
    Point positionDifference =
        new Point(
            (int) tileToBeReached.getX() - (int) currentTile.getX(),
            (int) tileToBeReached.getY() - (int) currentTile.getY());
    return positionDifference;
  }

  public Point closestMarketLocation(Point marketBelow, Point marketAbove) {
    Point closestMarket;

    if (Math.abs(marketBelow.getY()) > Math.abs(marketAbove.getY())
        && Math.abs(marketBelow.getX()) > Math.abs(marketAbove.getX())) {
      closestMarket = marketAbove;
    } else {
      closestMarket = marketBelow;
    }

    return closestMarket;
  }

  public TurnAction goToMarket(Point closestMarket) {
    if (inventorySize == 5) {
      if (robotMovement(closestMarket) != null) {
        return robotMovement(closestMarket);
      } else if (closestMarket.x == 0 && closestMarket.y == 0) {
        inventorySize = 0;
      }
    }
    return null;
  }

  public TurnAction goToRecharge(Point rechargeStation, int currentCharge) {
    if (currentCharge <= 20) {
      if (robotMovement(rechargeStation) != null) {
        return robotMovement(rechargeStation);
      }
    }
    if (rechargeStation.x == 0 && rechargeStation.y == 0 && currentCharge != 80) {
      return null;
    }
    return TurnAction.MINE;
  }

  public Point findClosestResourceLocation(Point yourLocation) {
    Point firstLocation = resourceLocations.get(0);

    for (Point closestLocation : resourceLocations) {
      if (DistanceUtil.getManhattanDistance(firstLocation, yourLocation)
          > DistanceUtil.getManhattanDistance(closestLocation, yourLocation)) {
        firstLocation = closestLocation;
      }
    }
    return firstLocation;
  }

  public void setInventorySize(int inventorySize) {
    this.inventorySize = inventorySize;
  }
}
