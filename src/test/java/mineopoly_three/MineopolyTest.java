package mineopoly_three;

import mineopoly_three.action.TurnAction;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.strategy.ZaidStrategy;
import mineopoly_three.tiles.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class MineopolyTest {
  private ZaidStrategy zaidStrategy = new ZaidStrategy();
  private int boardSize = 4;
  private PlayerBoardView sampleBoardOne;
  private PlayerBoardView sampleBoardTwo;

  @Before
  public void setUp() {
    TileType[][] boardTileTypes =
        new TileType[][] {
          {TileType.RED_MARKET, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
          {TileType.BLUE_MARKET, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.RECHARGE, TileType.RED_MARKET, TileType.EMPTY, TileType.EMPTY},
                {TileType.RESOURCE_EMERALD, TileType.RED_MARKET, TileType.EMPTY, TileType.EMPTY}
        };

    sampleBoardOne =
        new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(), new Point(), 0);

    zaidStrategy.initialize(boardSize, 5, 80, 2000, sampleBoardOne, sampleBoardOne.getYourLocation(), true, null);
}

  @Test
  public void testValidMarketTileRedMovement() {
    zaidStrategy.setInventorySize(5);
    TurnAction action = zaidStrategy.getTurnAction(sampleBoardOne, null, 80, true);

    assertEquals(TurnAction.MOVE_RIGHT, action);
  }

  @Test
  public void testInValidMarketTileRedMovement() {
    zaidStrategy.setInventorySize(4);
    TurnAction action = zaidStrategy.getTurnAction(sampleBoardOne, null, 80, true);

    assertEquals(TurnAction.MINE, action);
  }

  @Test
  public void testResourceTileMiningEmerald() {
    TurnAction action = zaidStrategy.getTurnAction(sampleBoardOne, null, 80, true);

    assertEquals(TurnAction.MINE, action);
  }

  @Test
  public void testValidRechargeTileMovement() {
    TurnAction action = zaidStrategy.getTurnAction(sampleBoardOne, null, 20, true);

    assertEquals(TurnAction.MOVE_UP, action);
  }

  @Test
  public void testInValidRechargeTileMovement() {
    TurnAction action = zaidStrategy.getTurnAction(sampleBoardOne, null, 21, true);

    assertEquals(TurnAction.MINE, action);
  }

  @Before
  public void setUpSampleBoardTwo() {
    TileType[][] boardTileTypes =
            new TileType[][] {
                    {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                    {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                    {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                    {TileType.RESOURCE_RUBY, TileType.RESOURCE_DIAMOND, TileType.EMPTY, TileType.EMPTY}
            };

    sampleBoardTwo =
            new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(), new Point(), 0);

    zaidStrategy.initialize(boardSize, 5, 80, 2000, sampleBoardTwo, sampleBoardTwo.getYourLocation(), true, null);
  }

  @Test
  public void testValidResourceTileMovingDiamond() {
    TurnAction action = zaidStrategy.getTurnAction(sampleBoardTwo, null, 80, true);

    assertEquals(TurnAction.MOVE_RIGHT, action);
  }

  @Test
  public void testInvalidResourceTileMovingRuby() {
    TurnAction action = zaidStrategy.getTurnAction(sampleBoardTwo, null, 80, true);

    assertEquals(TurnAction.MOVE_RIGHT, action);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMarketTileBoard() {
    zaidStrategy.setMarketTiles(new ArrayList<>());
    TurnAction action = zaidStrategy.getTurnAction(sampleBoardTwo, null, 80, true);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidRechargeTileBoard() {
    zaidStrategy.setRechargeStations(new ArrayList<>());
    TurnAction action = zaidStrategy.getTurnAction(sampleBoardTwo, null, 80, true);
  }
}
