package main;

import java.util.Random;
import java.util.Scanner;

public class MineField {

	public static int[][] setMines(int size, int noOfMines) {
		Random rand = new Random();

		int mineLand[][] = new int[size][size];

		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				mineLand[x][y] = 0;

		for (int i = 0; i < noOfMines; i++) {
			int x = rand.nextInt(size);
			int y = rand.nextInt(size);
			if (mineLand[x][y] != -1)
				mineLand[x][y] = -1; // -1 == bomb
			else
				i--;
		}

		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				if (mineLand[x][y] == -1)
					for (int i = -1; i <= 1; i++)
						for (int j = -1; j <= 1; j++) 
							try {
								if (mineLand[x + i][y + j] != -1) 
									mineLand[x + i][y + j]++;
							} catch (Exception e) {}

		return mineLand;
	}

	public static String toString(int mineField[][]) {
		String result = "";
		Random rand = new Random();

		final String emoteNumbers[] = {":zero:",":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:"};
		final String bomb = ":bomb:";
		final String spoiler = "||";
		
		int startX, startY;
		
		do {
			startX = rand.nextInt(mineField.length);
			startY = rand.nextInt(mineField.length);
		} while(mineField[startX][startY] != 0);
		
		for(int x = 0; x < mineField.length; x++) {
			for(int y = 0; y < mineField[x].length; y++) {
				if(x == startX && y == startY)
					result += ((mineField[x][y] == -1) ? bomb : emoteNumbers[mineField[x][y]]) + " ";
				else
					result += spoiler + ((mineField[x][y] == -1) ? bomb : emoteNumbers[mineField[x][y]]) + spoiler + " ";
			}
			result += "\n";
		}
		return result;
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int size = 0;
		int mines = 0;
		
		while(size <= 0 || size >= 15) {
			try {
				System.out.print("Enter size: ");
				size = in.nextInt();
			} catch(Exception e) {}
		}
		
		mines = size / 2 + new Random().nextInt(size);
		
		in.close();
		
		System.out.println("\n" + toString(setMines(size, mines)));
	}
}
