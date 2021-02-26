package mineopoly_three.strategy;

import jdk.nashorn.internal.objects.Global;
import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.tiles.Tile;
import mineopoly_three.tiles.TileType;
import mineopoly_three.util.DistanceUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

public class ZaidStrategy implements MinePlayerStrategy {
  private int inventorySize = 0;
  private Point[] marketTile = new Point[2];
  private Point rechargeStation;
  private ArrayList<Point> diamondLocations = new ArrayList<>();

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
        if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RESOURCE_DIAMOND || startingBoard.getTileTypeAtLocation(row, col) == TileType.RESOURCE_RUBY || startingBoard.getTileTypeAtLocation(row, col) == TileType.RESOURCE_EMERALD) {
          diamondLocations.add(new Point(row, col));
        }
        if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RECHARGE) {
          rechargeStation = new Point(row, col);
        }
        if (isRedPlayer) {
          if (startingBoard.getTileTypeAtLocation(row, col) == TileType.RED_MARKET) {
            marketTile[0] = new Point(row, col);
          }
          }else {
          if (startingBoard.getTileTypeAtLocation(row, col) == TileType.BLUE_MARKET) {
            marketTile[0] = new Point(row, col);
          }
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
    int changeInY;
    int changeInX;
    int changeInYDifferent;
    int changeInXDifferent;
    double distanceY;
    double distanceX;
    double rechargeX;
    double rechargeY;

    rechargeY = rechargeStation.getY() - boardView.getYourLocation().getY();
    rechargeX = rechargeStation.getX() - boardView.getYourLocation().getX();

    if (currentCharge <= 20) {
      if (rechargeY < 0) {
        return TurnAction.MOVE_DOWN;
      } else if (rechargeY > 0) {
        return TurnAction.MOVE_UP;
      }
      if (rechargeX < 0) {
        return TurnAction.MOVE_LEFT;
      } else if (rechargeX > 0) {
        return TurnAction.MOVE_RIGHT;
      }
    }

    if (rechargeX == 0 && rechargeY == 0 && currentCharge != 80) {
      return null;
    }

    changeInY = (int) marketTile[0].getY() - (int) boardView.getYourLocation().getY();
    changeInX = (int) marketTile[0].getX() - (int) boardView.getYourLocation().getX();
    changeInYDifferent = (int) marketTile[1].getY() - (int) boardView.getYourLocation().getY();
    changeInXDifferent = (int) marketTile[1].getX() - (int) boardView.getYourLocation().getX();

    if (DistanceUtil.getManhattanDistance(changeInXDifferent, changeInYDifferent, (int) boardView.getYourLocation().getX(), (int) boardView.getYourLocation().getY()) < DistanceUtil.getManhattanDistance(changeInX, changeInY, (int) boardView.getYourLocation().getX(), (int) boardView.getYourLocation().getY())) {
      changeInY = changeInYDifferent;
      changeInX = changeInXDifferent;
    }

    if (inventorySize > 4) {
      if (changeInY < 0) {
        return TurnAction.MOVE_DOWN;
      } else if (changeInY > 0) {
        return TurnAction.MOVE_UP;
      }
      if (changeInX < 0) {
        return TurnAction.MOVE_LEFT;
      } else if (changeInX > 0) {
        return TurnAction.MOVE_RIGHT;
      }
      if (changeInY == 0 && changeInX == 0) {
        inventorySize = 0;
      }
    }

    Point location = diamondLocations.get(0);

    for (Point closestLocation: diamondLocations) {
      if (DistanceUtil.getManhattanDistance(location, boardView.getYourLocation()) > DistanceUtil.getManhattanDistance(closestLocation, boardView.getYourLocation())) {
        location = closestLocation;
      }
    }

    distanceY = boardView.getYourLocation().getY() - location.getY();
    distanceX = boardView.getYourLocation().getX() - location.getX();

    if (distanceY < 0) {
      return TurnAction.MOVE_UP;
    } else if (distanceY > 0) {
      return TurnAction.MOVE_DOWN;
    }
    if (distanceX < 0) {
      return TurnAction.MOVE_RIGHT;
    } else if (distanceX > 0) {
      return TurnAction.MOVE_LEFT;
    }

    for (Map.Entry<Point, List<InventoryItem>> itemsOnGround :
        boardView.getItemsOnGround().entrySet()) {
      if (!itemsOnGround.getValue().isEmpty() && inventorySize < 5) {
        if (itemsOnGround.getValue().get(0).getItemType().getResourceTileType() != null
            && itemsOnGround.getKey().getLocation().equals(boardView.getYourLocation())) {
          inventorySize++;
          diamondLocations.remove(location);
          return TurnAction.PICK_UP_RESOURCE;
        }
        }
      }

    if (distanceX == 0 && distanceY == 0) {
      return TurnAction.MINE;
    }
//
//
//    if (currentCharge <= 50) {
//      changeInY = rechargeStation.getY() - boardView.getYourLocation().getY();
//      changeInX = rechargeStation.getX() - boardView.getYourLocation().getX();
//
//      if (changeInY < 0) {
//        return TurnAction.MOVE_DOWN;
//      } else if (changeInY > 0) {
//        return TurnAction.MOVE_UP;
//      }
//      if (changeInX < 0) {
//        return TurnAction.MOVE_LEFT;
//      } else if (changeInX > 0) {
//        return TurnAction.MOVE_RIGHT;
//      }
//      if (changeInX == 0) {
//        return null;
//      }
//    }
//
//
//
//

//
//    inOneDirection++;
//
//    if (inOneDirection > 80) {
//      inOneDirection = 0;
//    }
//
//    if (inOneDirection < 20) {
//      if (boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_EMERALD
//          || boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_RUBY
//          || boardView.getTileTypeAtLocation(boardView.getYourLocation())
//              == TileType.RESOURCE_DIAMOND) {
//        return TurnAction.MINE;
//      }
//      return TurnAction.MOVE_DOWN;
//    } else if (inOneDirection < 40) {
//      if (boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_EMERALD
//          || boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_RUBY
//          || boardView.getTileTypeAtLocation(boardView.getYourLocation())
//              == TileType.RESOURCE_DIAMOND) {
//        return TurnAction.MINE;
//      }
//      return TurnAction.MOVE_RIGHT;
//    } else if (inOneDirection < 60) {
//      if (boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_EMERALD
//          || boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_RUBY
//          || boardView.getTileTypeAtLocation(boardView.getYourLocation())
//              == TileType.RESOURCE_DIAMOND) {
//        return TurnAction.MINE;
//      }
//      return TurnAction.MOVE_UP;
//    } else if (inOneDirection < 80) {
//      if (boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_EMERALD
//          || boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_RUBY
//          || boardView.getTileTypeAtLocation(boardView.getYourLocation())
//              == TileType.RESOURCE_DIAMOND) {
//        return TurnAction.MINE;
//      }
//      return TurnAction.MOVE_LEFT;
//    }

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
  }

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
  }
}
