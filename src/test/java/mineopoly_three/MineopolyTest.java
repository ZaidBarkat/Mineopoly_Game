package mineopoly_three;

import mineopoly_three.game.GameBoard;
import mineopoly_three.game.GameEngine;
import mineopoly_three.strategy.MinePlayerStrategy;
import mineopoly_three.strategy.ZaidStrategy;
import mineopoly_three.tiles.MarketTile;
import mineopoly_three.tiles.Tile;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class MineopolyTest {
  ZaidStrategy zaidStrategy = new ZaidStrategy();
  int boardSize = 10;
  GameEngine gameEngine;
  GameBoard gameBoard;

  @Before
  public void setUp() {
    gameEngine = new GameEngine(boardSize, zaidStrategy, zaidStrategy, 1);
    gameBoard = gameEngine.getBoard();

    zaidStrategy.initialize(
        boardSize,
        5,
        80,
        3000,
        gameBoard.convertToView(gameEngine.getRedPlayer(), gameEngine.getBluePlayer()),
        gameBoard.getRedStartTileLocation(),
        true,
        new Random());
  }

  @Test
  public void sanityCheck() {
    Point tileLocation = new Point(0, 0);
    Point previousMarketLocation = new Point(2, 7);
    Tile marketTile = new MarketTile(tileLocation, true);
    Tile changeToEmptyTile = new MarketTile(previousMarketLocation, true);
    gameBoard.setTileAtTileLocation(marketTile);
    gameBoard.setTileAtTileLocation(changeToEmptyTile);
    zaidStrategy.setInventorySize(5);

    zaidStrategy.getTurnAction(
        gameBoard.convertToView(gameEngine.getRedPlayer(), gameEngine.getBluePlayer()),
        gameEngine.getEconomy(),
        80,
        true);

    //zaidStrategy.goToMarket(tileLocation);

    System.out.println(gameBoard.getRedStartTileLocation());
    assertEquals(
        new Point(0, 0),
        gameBoard
            .convertToView(gameEngine.getRedPlayer(), gameEngine.getBluePlayer())
            .getYourLocation());
  }
}
