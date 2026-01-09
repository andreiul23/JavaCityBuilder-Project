package main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import boardPieces.ApartmentPiece;
import boardPieces.FactoryPiece;
import boardPieces.ParkPiece;
import boardPieces.RetailPiece;

public class ModelPlacementRulesTest {

    @Test(expected = PositionOccupiedException.class)
    public void factoryRequiresRoadAndWater() throws Exception {
        Model model = new Model();
        model.addToBalance(20000.0); // Asigură fonduri suficiente pentru test
        model.construct(new FactoryPiece(5, 5)); // fără drum+apă adiacente -> eșec
    }

    @Test
    public void factorySucceedsWhenTouchingRoadAndWater() throws Exception {
        Model model = new Model();
        model.addToBalance(20000.0); // Asigură fonduri suficiente pentru test
        int baseline = countBuilt(model);
        model.construct(new FactoryPiece(1, 18)); // atinge drum la (0,18) și apă la (2,18)
        assertEquals(baseline + 1, countBuilt(model));
    }

    @Test(expected = PositionOccupiedException.class)
    public void apartmentRequiresRoad() throws Exception {
        Model model = new Model();
        model.addToBalance(10000.0);
        model.construct(new ApartmentPiece(5, 5));
    }

    @Test
    public void apartmentBuildsNextToRoad() throws Exception {
        Model model = new Model();
        model.addToBalance(10000.0);
        int baseline = countBuilt(model);
        model.construct(new ApartmentPiece(1, 10)); // atinge coloana drumului
        assertEquals(baseline + 1, countBuilt(model));
    }

    @Test(expected = PositionOccupiedException.class)
    public void retailRequiresRoad() throws Exception {
        Model model = new Model();
        model.addToBalance(5000.0); // Asigură fonduri suficiente pentru test
        model.construct(new RetailPiece(5, 5));
    }

    @Test
    public void retailBuildsNextToRoad() throws Exception {
        Model model = new Model();
        model.addToBalance(5000.0); // Asigură fonduri suficiente pentru test
        int baseline = countBuilt(model);
        model.construct(new RetailPiece(1, 12));
        assertEquals(baseline + 1, countBuilt(model));
    }

    @Test(expected = PositionOccupiedException.class)
    public void parkRequiresWater() throws Exception {
        Model model = new Model();
        model.addToBalance(5000.0);
        model.construct(new ParkPiece(5, 5));
    }

    @Test
    public void parkBuildsNextToWater() throws Exception {
        Model model = new Model();
        model.addToBalance(5000.0);
        int baseline = countBuilt(model);
        model.construct(new ParkPiece(3, 19)); // atinge apa râului la (2,19)
        assertEquals(baseline + 1, countBuilt(model));
    }

    private static int countBuilt(Model model) {
        int count = 0;
        for (boardPieces.BoardPieceInterface[] row : model.getBoard()) {
            for (boardPieces.BoardPieceInterface piece : row) {
                if (!(piece instanceof boardPieces.GrassPiece) && !(piece instanceof boardPieces.WaterPiece)) {
                    count++;
                }
            }
        }
        return count;
    }
}
