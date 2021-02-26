package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.tiles.Tile;
import mineopoly_three.tiles.TileType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

public class ZaidStrategy implements MinePlayerStrategy {
  private List<Point> itemLocations = new ArrayList<>();
  private int inOneDirection = 0;
  private int inventorySize = 0;
  private Point marketTile;

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
    marketTile = startTileLocation;
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

    for (List<InventoryItem> point : boardView.getItemsOnGround().values()) {
      if (!point.isEmpty() && inventorySize < 5) {
        if (point.get(0).getItemType().getResourceTileType() == TileType.RESOURCE_DIAMOND
            || point.get(0).getItemType().getResourceTileType() == TileType.RESOURCE_EMERALD
            || point.get(0).getItemType().getResourceTileType() == TileType.RESOURCE_RUBY) {
          inventorySize++;
          return TurnAction.PICK_UP_RESOURCE;
        }
      }
    }

    double changeInY = marketTile.getY() - boardView.getYourLocation().getY();
    double changeInX = marketTile.getX() - boardView.getYourLocation().getX();

    if (boardView.getYourLocation().equals(marketTile)) {
      inventorySize = 0;
    }

    if (inventorySize == 5) {
      System.out.println("hmm");
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
    }

    inOneDirection++;

    if (inOneDirection > 40) {
      inOneDirection = 0;
    }

    if (inOneDirection < 10) {
      if (boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_EMERALD
          || boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_RUBY
          || boardView.getTileTypeAtLocation(boardView.getYourLocation())
              == TileType.RESOURCE_DIAMOND) {
        return TurnAction.MINE;
      }
      return TurnAction.MOVE_DOWN;
    } else if (inOneDirection < 20) {
      if (boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_EMERALD
          || boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_RUBY
          || boardView.getTileTypeAtLocation(boardView.getYourLocation())
              == TileType.RESOURCE_DIAMOND) {
        return TurnAction.MINE;
      }
      return TurnAction.MOVE_RIGHT;
    } else if (inOneDirection < 30) {
      if (boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_EMERALD
          || boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_RUBY
          || boardView.getTileTypeAtLocation(boardView.getYourLocation())
              == TileType.RESOURCE_DIAMOND) {
        return TurnAction.MINE;
      }
      return TurnAction.MOVE_UP;
    } else if (inOneDirection < 40) {
      if (boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_EMERALD
          || boardView.getTileTypeAtLocation(boardView.getYourLocation()) == TileType.RESOURCE_RUBY
          || boardView.getTileTypeAtLocation(boardView.getYourLocation())
              == TileType.RESOURCE_DIAMOND) {
        return TurnAction.MINE;
      }
      return TurnAction.MOVE_LEFT;
    }
    return null;

    //    if (tileToRight == TileType.RESOURCE_DIAMOND) {
    //      return TurnAction.MOVE_RIGHT;
    //    } else if (boardView.getTileTypeAtLocation(boardView.getYourLocation())
    //            == TileType.RESOURCE_DIAMOND) {
    //        return TurnAction.MINE;
    //    }else if (tileToLeft == TileType.RESOURCE_DIAMOND) {
    //      return TurnAction.MOVE_LEFT;
    //    } else if (tileUp == TileType.RESOURCE_DIAMOND) {
    //      return TurnAction.MOVE_UP;
    //    } else if (tileDown == TileType.RESOURCE_DIAMOND) {
    //      return TurnAction.MOVE_DOWN;
    //    }

  }

  /**
   * Called when the player receives an item from performing a TurnAction that gives an item. At the
   * moment this is only from using PICK_UP on top of a mined resource
   *
   * @param itemReceived The item received from the player's TurnAction on their last turn
   */
  @Override
  public void onReceiveItem(InventoryItem itemReceived) {
    if (inventorySize == 5) {
      inventorySize = 0;
    }
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