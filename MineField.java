package main;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Discord Spoiler Minesweeper text message generator Generates and copies
 * fields of chosen size to user's clipboard
 * 
 * @author ItsNorin: <a href="http://github.com/ItsNorin">Github</a>
 */
public class MineField extends Application {
	public static final String DEFAULT_BOMB_COUNTS_STR[] = { ":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:",
			":seven:", ":eight:" };
	
	public static final String DEFUALT_BOMB = ":boom:";
	public static final String SPOILER = "||";
	
	public static final TextField[] bombCountTextFields;
	public static final TextField bombTextField;
	
	static {
		bombTextField = new TextField();
		bombTextField.setText(DEFUALT_BOMB);
		bombCountTextFields = new TextField[9];
		for(int i = 0; i < bombCountTextFields.length; i++) {
			bombCountTextFields[i] = new TextField();
			bombCountTextFields[i].setText(DEFAULT_BOMB_COUNTS_STR[i]);
		}
	}
	
	

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Pane root = new Pane();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Minesweeper");

		Label sizeTextBoxMessagea = new Label();
		sizeTextBoxMessagea.setText("Enter field size (5-16)");

		TextField sizeTextBox = new TextField();
		sizeTextBox.setPrefWidth(50);
		sizeTextBox.setMinWidth(50);
		sizeTextBox.setText("15");

		Label latestMessage = new Label();

		Button button = new Button();
		button.setText("Generate and copy field.");

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final String userText = sizeTextBox.getText();

				int fieldSize = -1;

				if (userText.length() > 0)
					fieldSize = Integer.parseInt(userText);
				else {
					latestMessage.setText("Enter a size!");
					latestMessage.setTextFill(Color.RED);
				}

				if (fieldSize >= 5 && fieldSize <= 16) {
					String field = getNewMineField(fieldSize, fieldSize * fieldSize / 9, 2000);
					;
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection strSel = new StringSelection(field);
					clipboard.setContents(strSel, null);
					latestMessage.setText("Copied to clipboard!");
					latestMessage.setTextFill(Color.GREEN);
				} else {
					latestMessage.setText("Field size must be between 5 and 16!");
					latestMessage.setTextFill(Color.RED);
				}
			}
		});

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.add(sizeTextBoxMessagea, 0, 0, 2, 1);
		grid.add(sizeTextBox, 0, 1);
		grid.add(button, 1, 1);
		grid.add(latestMessage, 0, 2, 2, 1);
		
		Label bombLabel = new Label();
		bombLabel.setText("Bomb");
		grid.add(bombLabel, 0, 3);
		grid.add(bombTextField, 1, 3);
		
		for(int i = 0; i < bombCountTextFields.length; i++) {
			Label number = new Label();
			number.setText(Integer.toString(i));
			grid.add(bombCountTextFields[i], 1, 4+i);
			grid.add(number, 0, 4+i);
		}

		root.getChildren().add(grid);

		stage.setResizable(false);
		stage.show();
	}
	
	/**
	 * adds contiguous region of empty points from given x and y to emptyNeighbors
	 */
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

	/** number of bombs around given point */
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

	public static String getBombStr() {
		String text = DEFUALT_BOMB;
		final String tfStr = bombTextField.getText();
		if(tfStr.length() > 0)
			text = tfStr;
		else
			bombTextField.setText(text);
		return text;
	}
	
	public static String getBombCountStr(int n) {
		String text = DEFAULT_BOMB_COUNTS_STR[n];
		final String tfStr = bombCountTextFields[n].getText();
		if(tfStr.length() > 0)
			text = tfStr;
		else
			bombCountTextFields[n].setText(text);
		return text;
	}
	
	/**
	 * generates text version of minesweeper shorter than given maxStringLength.
	 * Size and number of mines may be smaller than what is given if it is
	 * impossible to make a field of specified size
	 */
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
					String cellStr = bombs.contains(p) ? getBombStr() : getBombCountStr(getNearbyBombCount(bombs, p));
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
}
