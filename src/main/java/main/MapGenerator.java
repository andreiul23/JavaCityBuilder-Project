package main;

import boardPieces.BoardPieceInterface;

public interface MapGenerator {
    BoardPieceInterface[][] generateMap(int width, int height);
}
