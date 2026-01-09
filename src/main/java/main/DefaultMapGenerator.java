package main;

import boardPieces.*;

public class DefaultMapGenerator implements MapGenerator {
    @Override
    public BoardPieceInterface[][] generateMap(int width, int height) {
        BoardPieceInterface[][] board = new BoardPieceInterface[height][width];
        
        /* Setează toate piesele ca piese de iarbă */
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x == 0) {
					/* Începe utilizatorul cu un rând de drum */
					board[y][x] = new RoadPiece(x, y);
				} else {
					board[y][x] = new GrassPiece(x, y);
				}
			}
		}
		/* Plasează puțină apă în partea de sus a tablei */
		for (int i = 0; i <= 3; i++) {
			for (int j = 1; j <= 4 - i; j++) {
				board[i][width - j] = new WaterPiece(i, width - j);
			}
		}

		/* Plasează puțină apă în mijlocul tablei */
		for (int x = (width / 2) - 1; x <= (width / 2) + 1; x++) {
			for (int y = (height / 2) - 1; y <= (height / 2) + 1; y++) {
				board[y][x] = new WaterPiece(x, y);
			}
		}
		
		/* Plasează un râu în stânga jos */
		for (int i = 0; i < 8; i++) {
			board[height - 1 - i][i] = new WaterPiece(i, height - 1 - i);
			if (i < 7) {
				board[height - 2 - i][i] = new WaterPiece(i, height - 2 - i);
			}
		}
		
		/* Plasează un iaz pe partea dreaptă */
		for (int y = 5; y <= 7; y++) {
			for (int x = width - 4; x <= width - 2; x++) {
				board[y][x] = new WaterPiece(x, y);
			}
		}
		
		/* Plasează un mic lac în colțul din dreapta jos */
		for (int y = height - 5; y <= height - 2; y++) {
			for (int x = width - 6; x <= width - 3; x++) {
				board[y][x] = new WaterPiece(x, y);
			}
		}
		
		/* Plasează un lac neregulat în stânga jos (formă de L) */
		int[][] lowerLeftLake = {{13, 1}, {14, 1}, {14, 2}, {15, 2}, {15, 3}};
        for (int[] coord : lowerLeftLake) {
            if (coord[0] < height && coord[1] < width) {
                board[coord[0]][coord[1]] = new WaterPiece(coord[1], coord[0]);
            }
        }
        
		/* Plasează o zonă de apă împrăștiată în partea de jos mijloc (neregulată) */
		int[][] lowerMiddleLake = {{15, 9}, {15, 10}, {15, 11}, {16, 10}, {16, 11}, {16, 12}, {17, 9}, {17, 10}, {18, 11}, {18, 12}};
		for (int[] pos : lowerMiddleLake) {
			if (pos[0] < height && pos[1] < width) {
				board[pos[0]][pos[1]] = new WaterPiece(pos[1], pos[0]);
			}
		}
		
		/* Plasează un mic iaz neregulat în partea de sus mijloc */
		int[][] upperMiddleLake = {{8, 16}, {8, 17}, {9, 17}, {9, 18}};
		for (int[] pos : upperMiddleLake) {
			if (pos[0] < height && pos[1] < width) {
				board[pos[0]][pos[1]] = new WaterPiece(pos[1], pos[0]);
			}
		}

		/* Oferă utilizatorului o piesă de parc în scopuri de angajare */
		board[width / 2 - 2][height / 2 - 1] = new ParkPiece(width / 2 - 2, height / 2 - 1);

        return board;
    }
}
