package mineopoly_three;

import mineopoly_three.action.TurnAction;
import mineopoly_three.competition.ZaidStrategyCompetition;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.strategy.ZaidStrategy;
import mineopoly_three.tiles.TileType;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MineopolyTest {
  private final int boardSize = 4;
  private PlayerBoardView sampleBoardOne;
  private PlayerBoardView sampleBoardTwo;
  private PlayerBoardView sampleBoardThree;
  private ZaidStrategyCompetition zaidStrategyOne;
  private ZaidStrategy zaidStrategyTwo;
  private ZaidStrategy zaidStrategyThree;

  @Before
  public void setUpSampleBoardOne() {
    zaidStrategyOne = new ZaidStrategyCompetition();

    TileType[][] boardTileTypes =
        new TileType[][] {
          {TileType.RED_MARKET, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_DIAMOND},
          {TileType.BLUE_MARKET, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
          {TileType.RECHARGE, TileType.RED_MARKET, TileType.BLUE_MARKET, TileType.EMPTY},
          {TileType.RESOURCE_EMERALD, TileType.RED_MARKET, TileType.EMPTY, TileType.EMPTY}
        };

    sampleBoardOne =
        new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(), new Point(), 0);

    zaidStrategyOne.initialize(
        boardSize, 5, 80, 2000, sampleBoardOne, sampleBoardOne.getYourLocation(), false, null);
  }

  @Test
  public void testValidMarketTileMovementXAxis() {
    zaidStrategyOne.setInventorySize(5);
    TurnAction action = zaidStrategyOne.getTurnAction(sampleBoardOne, null, 80, false);

    assertEquals(TurnAction.MOVE_UP, action);
  }

  @Test
  public void testInValidMarketTileRedMovement() {
    zaidStrategyOne.setInventorySize(4);
    TurnAction action = zaidStrategyOne.getTurnAction(sampleBoardOne, null, 80, true);

    assertEquals(TurnAction.MINE, action);
  }

  @Test
  public void testResourceTileMiningEmerald() {
    TurnAction action = zaidStrategyOne.getTurnAction(sampleBoardOne, null, 80, true);

    assertEquals(TurnAction.MINE, action);
  }

  @Test
  public void testValidRechargeTileMovement() {
    TurnAction action = zaidStrategyOne.getTurnAction(sampleBoardOne, null, 20, true);

    assertEquals(TurnAction.MOVE_UP, action);
  }

  @Test
  public void testInValidRechargeTileMovement() {
    TurnAction action = zaidStrategyOne.getTurnAction(sampleBoardOne, null, 21, true);

    assertEquals(TurnAction.MINE, action);
  }

  @Before
  public void setUpSampleBoardTwo() {
    zaidStrategyTwo = new ZaidStrategy();

    TileType[][] boardTileTypes =
        new TileType[][] {
          {TileType.RED_MARKET, TileType.RED_MARKET, TileType.EMPTY, TileType.RESOURCE_EMERALD},
          {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
          {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
          {TileType.RECHARGE, TileType.RESOURCE_DIAMOND, TileType.EMPTY, TileType.EMPTY}
        };

    sampleBoardTwo =
        new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(), new Point(), 0);

    zaidStrategyTwo.initialize(
        boardSize, 5, 80, 2000, sampleBoardTwo, sampleBoardTwo.getYourLocation(), true, null);
  }

  @Test
  public void testValidResourceTileMovingDiamond() {
    TurnAction action = zaidStrategyTwo.getTurnAction(sampleBoardTwo, null, 80, true);

    assertEquals(TurnAction.MOVE_RIGHT, action);
  }

  @Test
  public void testValidMarketTileMovementYAxis() {
    zaidStrategyTwo.setInventorySize(5);
    TurnAction action = zaidStrategyTwo.getTurnAction(sampleBoardTwo, null, 80, true);

    assertEquals(TurnAction.MOVE_UP, action);
  }

  @Test
  public void testValidRechargeTileCharging() {
    TurnAction action = zaidStrategyTwo.getTurnAction(sampleBoardTwo, null, 20, true);

    assertNull(action);
  }

  @Test
  public void testInValidRechargeTileCharging() {
    TurnAction action = zaidStrategyTwo.getTurnAction(sampleBoardTwo, null, 80, true);

    assertEquals(TurnAction.MOVE_RIGHT, action);
  }

  @Before
  public void setUpSampleBoardThree() {
    zaidStrategyThree = new ZaidStrategy();

    TileType[][] boardTileTypes =
        new TileType[][] {
          {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
          {TileType.RESOURCE_RUBY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
          {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
          {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY}
        };

    sampleBoardThree =
        new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(), new Point(), 0);

    zaidStrategyThree.initialize(
        boardSize, 5, 80, 2000, sampleBoardThree, sampleBoardThree.getYourLocation(), true, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMarketAndRechargeBoard() {
    zaidStrategyThree.getTurnAction(sampleBoardThree, null, 80, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidResourceTileBoard() {
    zaidStrategyThree.getTurnAction(sampleBoardThree, null, 80, true);
  }

  @Test
  public void testValidPickUpTile() {
    HashMap<Point, List<InventoryItem>> itemToPickUp = new HashMap<>();
    List<InventoryItem> listToPopulate = new ArrayList<>();
    InventoryItem item = new InventoryItem(ItemType.DIAMOND);
    listToPopulate.add(item);
    itemToPickUp.put(new Point(0, 0), listToPopulate);

    TileType[][] boardTileTypes =
        new TileType[][] {
          {TileType.RECHARGE, TileType.RED_MARKET},
          {TileType.RESOURCE_DIAMOND, TileType.RED_MARKET}
        };

    PlayerBoardView sampleBoardPickUp =
        new PlayerBoardView(boardTileTypes, itemToPickUp, new Point(), new Point(), 0);

    zaidStrategyThree.initialize(
        2, 5, 80, 3000, sampleBoardPickUp, sampleBoardPickUp.getYourLocation(), true, null);
    TurnAction action = zaidStrategyThree.getTurnAction(sampleBoardPickUp, null, 80, true);

    assertEquals(TurnAction.PICK_UP_RESOURCE, action);
  }
}
