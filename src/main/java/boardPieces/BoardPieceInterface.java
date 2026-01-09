package boardPieces;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/* 
 * O piesă de joc reprezintă una dintre posibilele clădiri ce pot fi create pe BoardVisualizerWidget
 * O piesă de joc își știe poziția relativă pe tablă în termeni de coordonate x și y
 * Implementarea încapsulează de asemenea imaginile relevante, inclusiv costul, venitul, rezidenții și numele
 * Implementarea este responsabilă și pentru încapsularea unei imagini de afișat în interfața utilizator
 */

public interface BoardPieceInterface extends Serializable, Comparable<BoardPieceInterface> {
	/*
	 * Returnează poziția X a piesei încapsulate în grila modelului
	 */
	public int getXPosition();

	/*
	 * Returnează poziția Y a piesei încapsulate în grila modelului
	 */
	public int getYPosition();

	/*
	 * Returnează venitul pe care clădirea îl va genera, reprezentat în dolari
	 * pe zi
	 */
	public double getDailyIncome();

	/* Returnează numărul de rezidenți care locuiesc în această piesă de pe tablă */
	public int getNumResidents();

	/*
	 * Returnează prețul pe care utilizatorul trebuie să-l plătească pentru a construi această clădire
	 */
	public double getCostToBuild();

	/* Returnează numele piesei ca șir de caractere */
	public String getPieceName();

	/*
	 * Returnează o reprezentare imagine a clădirii pentru a fi utilizată de BoardVisualizerWidget
	 * 
	 * O caracteristică actuală este capacitatea de a oferi animație de bază pentru fiecare pictogramă
	 * BoardVisualizerWidget va alterna între aceste metode pe măsură ce ziua
	 * se schimbă. Dacă toate cele trei metode returnează imagini diferite, pictograma va apărea
	 * animată
	 * 
	 */
	public BufferedImage getPieceImage();

	/*
	 * Pe măsură ce unități succesive ale unei piese sunt construite, dorim ca prețul
	 * construcției să crească
	 */
	public void updateCost();

	/*
	 * Unele piese de pe tablă au oportunitatea de a angaja oameni. Această metodă returnează
	 * numărul de oameni pe care implementarea îi poate angaja
	 */
	public int getNumEmployeePositions();

}
