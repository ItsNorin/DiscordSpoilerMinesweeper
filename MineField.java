package main;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MineField {
	public static final String EMOTE_NUMBERS[] = { ":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:",
			":seven:", ":eight:" };
	public static final String BOMB = ":boom:";
	public static final String SPOILER = "||";

	/** adds contiguous region of empty points from given x and y to emptyNeighbors*/
	protected static void addEmptyNeighbors(ArrayList<Point> empty, int x, int y, ArrayList<Point> emptyNeighbors) {
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				if (i != j) {
					Point p = new Point(x + i, y + j);
					if (empty.contains(p) && !emptyNeighbors.contains(p)) {
						emptyNeighbors.add(p);
						addEmptyNeighbors(empty, p.x, p.y, emptyNeighbors);
					}
				}
			}
	}

	/** Expands selection in +-1 in x and y */
	protected static ArrayList<Point> expandByOne(ArrayList<Point> selection) {
		ArrayList<Point> exp = new ArrayList<Point>(selection);
		for (Point p : selection)
			for (int i = -1; i <= 1; i++)
				for (int j = -1; j <= 1; j++) {
					Point np = new Point(p.x + i, p.y + j);
					if (!selection.contains(np))
						exp.add(np);
				}
		return exp;
	}

	/** number of bombs around given point*/
	protected static int getNearbyBombCount(ArrayList<Point> bombs, Point p) {
		int bombCount = 0;
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++)
				if (bombs.contains(new Point(p.x + i, p.y + j)))
					bombCount++;
		return bombCount;
	}

	/** makes new list of all empty tiles in field */
	protected static ArrayList<Point> getAllEmptyTiles(ArrayList<Point> bombs, int size) {
		ArrayList<Point> empty = new ArrayList<Point>();
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++) {
				Point p = new Point(x, y);
				if (getNearbyBombCount(bombs, p) == 0)
					empty.add(p);
			}
		return empty;
	}

	/** generates text version of minesweeper shorter than given maxStringLength. 
	 * Size and number of mines may be smaller than what is given if it is impossible to make a field of specified size*/
	public static String getNewMineField(int size, int noOfMines, final int maxStringLength) {
		String result = "";

		Random rand = new Random();
		Point start = new Point(0, 0);
		ArrayList<Point> bombs = new ArrayList<Point>(), emptyTiles;

		// try to make field of 1 size smaller until it fits discord's message length
		do {
			// try to make field with 1 less mine until there is a tile with no nearby bombs
			do {
				// field description
				result = "Size: " + Integer.toString(size) + "\tMines: " + Integer.toString(noOfMines) + "\n";
				// set mines
				bombs.clear();
				for (int i = 0; i < noOfMines; i++) {
					Point p = new Point(rand.nextInt(size), rand.nextInt(size));
					if (!bombs.contains(p))
						bombs.add(p);
					else
						i--;
				}

				// find starting position
				emptyTiles = getAllEmptyTiles(bombs, size);
				if (emptyTiles.size() == 0)
					noOfMines--;
				else
					start = emptyTiles.get(rand.nextInt(emptyTiles.size()));
			} while (emptyTiles.size() == 0);

			ArrayList<Point> leaveUnspoiled = new ArrayList<Point>();
			addEmptyNeighbors(emptyTiles, start.x, start.y, leaveUnspoiled);
			leaveUnspoiled = expandByOne(leaveUnspoiled);

			// create string
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					Point p = new Point(x, y);
					String cellStr = bombs.contains(p) ? BOMB : EMOTE_NUMBERS[getNearbyBombCount(bombs, p)];
					if (!leaveUnspoiled.contains(p))
						cellStr = SPOILER + cellStr + SPOILER;
					result += cellStr;
				}
				result += "\n";
			}
			size--;
		} while (result.length() > maxStringLength);
		return result;
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int size = 0;
		
		while (size <= 2 || size >= 16) {
			try {
				System.out.print("Enter size: ");
				size = in.nextInt();
			} catch (Exception e) {
			}
		}

		in.close();

		System.out.println("\n" + getNewMineField(size, size * size / 9, 2000));
	}
}
