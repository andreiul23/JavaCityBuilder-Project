package main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import boardPieces.ApartmentPiece;
import boardPieces.FactoryPiece;
import boardPieces.HousePiece;
import boardPieces.ParkPiece;
import boardPieces.RetailPiece;
import boardPieces.RoadPiece;

public class ModelCostsResetTest {

	@Test
    public void resetsStaticCostsOnNewModel() {
        // Mută costurile statice departe de cele implicite
        HousePiece.costToConstruct = 1234.0;
        ApartmentPiece.costToConstruct = 5678.0;
        FactoryPiece.costToConstruct = 9999.0;
        RetailPiece.costToConstruct = 8888.0;
        ParkPiece.costToConstruct = 7777.0;
        RoadPiece.costToConstruct = 6666.0;
        Model.COST_TO_DEMOLISH = 4242.0;

        // Noul model ar trebui să reseteze totul la prețurile de bază
        new Model();

        assertEquals(800.0, HousePiece.costToConstruct, 0.001);
        assertEquals(3000.0, ApartmentPiece.costToConstruct, 0.001);
        assertEquals(10000.0, FactoryPiece.costToConstruct, 0.001);
        assertEquals(6000.0, RetailPiece.costToConstruct, 0.001);
        assertEquals(2500.0, ParkPiece.costToConstruct, 0.001);
        assertEquals(200.0, RoadPiece.costToConstruct, 0.001);
        assertEquals(1250.0, Model.COST_TO_DEMOLISH, 0.001);
    }
}
