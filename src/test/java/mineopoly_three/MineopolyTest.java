package mineopoly_three;

import mineopoly_three.action.Action;
import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.game.GameBoard;
import mineopoly_three.game.GameEngine;
import mineopoly_three.item.ItemType;
import mineopoly_three.strategy.MinePlayerStrategy;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.strategy.ZaidStrategy;
import mineopoly_three.tiles.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class MineopolyTest {
  private ZaidStrategy zaidStrategy = new ZaidStrategy();
  private int boardSize = 4;
  private PlayerBoardView boardView;

  @Before
  public void setUp() {
    TileType[][] boardTileTypes =
        new TileType[][] {
          {TileType.RED_MARKET, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
          {TileType.BLUE_MARKET, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.RECHARGE, TileType.RED_MARKET, TileType.EMPTY, TileType.EMPTY},
                {TileType.RESOURCE_EMERALD, TileType.RED_MARKET, TileType.EMPTY, TileType.EMPTY}
        };

    boardView =
        new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(), new Point(), 0);

    zaidStrategy.initialize(boardSize, 5, 80, 2000, boardView, boardView.getYourLocation(), true, null);
}

  @Test
  public void testValidMarketTileRedMovement() {
    zaidStrategy.setInventorySize(5);
    TurnAction action = zaidStrategy.getTurnAction(boardView, null, 80, true);

    assertEquals(TurnAction.MOVE_RIGHT, action);
  }

  @Test
  public void testInValidMarketTileRedMovement() {
    zaidStrategy.setInventorySize(4);
    TurnAction action = zaidStrategy.getTurnAction(boardView, null, 80, true);

    assertEquals(TurnAction.MINE, action);
  }

  @Test
  public void testResourceTileMining() {
    TurnAction action = zaidStrategy.getTurnAction(boardView, null, 80, true);

    assertEquals(TurnAction.MINE, action);
  }

  @Test
  public void testResourceTilePickingUp() {
    zaidStrategy.getTurnAction(boardView, null, 80, true);
    boardView.getItemsOnGround().get(boardView.getYourLocation());
    zaidStrategy.getTurnAction(boardView, null, 80, true);
    zaidStrategy.getTurnAction(boardView, null, 80, true);
    TurnAction action = zaidStrategy.getTurnAction(boardView, null, 80, true);

    assertEquals(TurnAction.PICK_UP_RESOURCE, action);
  }

  @Test
  public void testValidRechargeTileMovement() {
    TurnAction action = zaidStrategy.getTurnAction(boardView, null, 20, true);

    assertEquals(TurnAction.MOVE_UP, action);
  }

  @Test
  public void testInValidRechargeTileMovement() {
    TurnAction action = zaidStrategy.getTurnAction(boardView, null, 21, true);

    assertEquals(TurnAction.MINE, action);
  }
}
