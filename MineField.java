package main;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MineField {
	public static final String EMOTE_NUMBERS[] = { ":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:",
			":seven:", ":eight:" };
	public static final String BOMB = ":bomb:";
	public static final String SPOILER = "||";

	protected static String getCellString(ArrayList<Point> field, Point p) {
		if (field.contains(p))
			return BOMB;

		int bombCount = 0;
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++)
				if (field.contains(new Point(p.x + i, p.y + j)))
					bombCount++;
		return EMOTE_NUMBERS[bombCount];
	}

	public static String getNewMineField(int size, int noOfMines) {
		String result = "";
		Random rand = new Random();
		Point start = new Point(0,0);
		ArrayList<Point> field = new ArrayList<Point>();

		// set mines
		for (int i = 0; i < noOfMines; i++) {
			Point p = new Point(rand.nextInt(size), rand.nextInt(size));
			if (!field.contains(p))
				field.add(p);
			else
				i--;
		}
		
		// find starting position
		do {
			start.x = rand.nextInt(size);
			start.y = rand.nextInt(size);
		} while (getCellString(field, start) != EMOTE_NUMBERS[0]);
		
		// create string
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++) {
				String cellStr = getCellString(field, new Point(x,y));
				if(!(x == start.x && y == start.y)) 
					cellStr = SPOILER + cellStr + SPOILER;
				result += cellStr + " ";	
			}
			result += "\n";
		}

		return result;
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int size = 0;
		int mines = 0;

		while (size <= 0 || size >= 15) {
			try {
				System.out.print("Enter size: ");
				size = in.nextInt();
			} catch (Exception e) {
			}
		}

		mines = size / 2 + new Random().nextInt(size);

		in.close();

		System.out.println("\n" + getNewMineField(size, mines));
	}
}
