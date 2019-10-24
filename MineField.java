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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Discord Spoiler MineSweeper text message generator. Generates and copies
 * fields of with entered contents to user's clip board
 * 
 * @author ItsNorin: <a href="http://github.com/ItsNorin">Github</a>
 */
public class MineField extends Application {
	public static final String DEFAULT_BOMB_COUNTS_STR[] = { ":zero:", ":one:", ":two:", ":three:", ":four:", ":five:",
			":six:", ":seven:", ":eight:" };

	public static final String DEFUALT_BOMB = ":boom:";
	public static final String SPOILER = "||";
	public static final String MAX_MESSAGE_LENGTH = "2000";

	public static final TextField[] bombCountTextFields;
	public static final TextField bombTextField;
	public static final TextField maxMessageLength;
	public static boolean includeDescription;

	public final static int TEXT_BOX_WIDTH = 80;
	public final static String LABEL_STYLE_CSS = "-fx-text-fill: #FFFFFF;";
	public final static String TEXT_BOX_CSS = "-fx-background-color: #40444b; -fx-text-fill: #FFFFFF;";
	public final static String BUTTON_CSS = "-fx-background-color: #2f3136; -fx-text-fill: #FFFFFF;";

	static {
		includeDescription = true;

		maxMessageLength = new TextField();
		maxMessageLength.setText(MAX_MESSAGE_LENGTH);
		setTextFieldFormat(maxMessageLength);

		bombTextField = new TextField();
		bombTextField.setText(DEFUALT_BOMB);
		setTextFieldFormat(bombTextField);

		bombCountTextFields = new TextField[9];
		for (int i = 0; i < bombCountTextFields.length; i++) {
			bombCountTextFields[i] = new TextField();
			bombCountTextFields[i].setText(DEFAULT_BOMB_COUNTS_STR[i]);
			setTextFieldFormat(bombCountTextFields[i]);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Pane root = new Pane();
		root.setStyle("-fx-background-color: #36393f");
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Discord Minesweeper");
		stage.initStyle(StageStyle.UTILITY);

		Label sizeTextBoxLabel = new Label();
		sizeTextBoxLabel.setText("Enter field size (5-20)");
		sizeTextBoxLabel.setStyle(LABEL_STYLE_CSS);

		TextField sizeTextBox = new TextField();
		sizeTextBox.setText("10");
		setTextFieldFormat(sizeTextBox);

		Label latestMessage = new Label();

		Button generateFieldButton = new Button();
		generateFieldButton.setText("Generate and copy field.");
		generateFieldButton.setStyle(BUTTON_CSS);

		generateFieldButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final String userText = sizeTextBox.getText();
				final String userMsgLength = getTextFieldStr_setDefault(maxMessageLength, MAX_MESSAGE_LENGTH);

				int fieldSize = -1;

				if (userText.length() <= 0) {
					latestMessage.setText("Enter a size!");
					latestMessage.setTextFill(Color.ORANGERED);
				} else if (Integer.parseInt(userMsgLength) < 100) {
					latestMessage.setText("Message length less than 100!");
					latestMessage.setTextFill(Color.ORANGERED);
				} else {
					fieldSize = Integer.parseInt(userText);
					if (fieldSize < 5 || fieldSize > 20) {
						latestMessage.setText("Field size must be between 5 and 20!");
						latestMessage.setTextFill(Color.ORANGERED);
					} else {
						String field = getNewMineField(fieldSize, fieldSize * fieldSize / 9,
								Integer.parseInt(userMsgLength));
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						StringSelection strSel = new StringSelection(field);
						clipboard.setContents(strSel, null);
						latestMessage.setText("Copied to clipboard!");
						latestMessage.setTextFill(Color.LIME);
					}
				}
			}
		});

		Button includeFieldDescriptionButton = new Button();
		includeFieldDescriptionButton.setText("Toggle: Including description");
		includeFieldDescriptionButton.setStyle(BUTTON_CSS);
		includeFieldDescriptionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				includeDescription = !includeDescription;
				if (includeDescription)
					includeFieldDescriptionButton.setText("Toggle: Including description");
				else
					includeFieldDescriptionButton.setText("Toggle: Not including description");
			}
		});

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(5);
		grid.setPadding(new Insets(10, 10, 20, 10));

		grid.add(sizeTextBoxLabel, 0, 0);
		grid.add(sizeTextBox, 1, 0);

		GridPane.setHalignment(generateFieldButton, HPos.CENTER);
		grid.add(generateFieldButton, 0, grid.getRowCount(), 2, 1);

		GridPane.setHalignment(latestMessage, HPos.CENTER);
		grid.add(latestMessage, 0, grid.getRowCount(), 2, 1);

		GridPane.setHalignment(includeFieldDescriptionButton, HPos.CENTER);
		grid.add(includeFieldDescriptionButton, 0, grid.getRowCount(), 2, 1);

		Label bombLabel = new Label();
		bombLabel.setText("Bomb");
		bombLabel.setStyle(LABEL_STYLE_CSS);
		GridPane.setHalignment(bombLabel, HPos.CENTER);
		grid.add(bombLabel, 0, grid.getRowCount());
		grid.add(bombTextField, 1, grid.getRowCount() - 1);

		for (int i = 0; i < bombCountTextFields.length; i++) {
			Label number = new Label();
			number.setText(Integer.toString(i));
			number.setStyle(LABEL_STYLE_CSS);
			GridPane.setHalignment(number, HPos.CENTER);
			grid.add(bombCountTextFields[i], 1, grid.getRowCount());
			grid.add(number, 0, grid.getRowCount() - 1);
		}

		Label maxMessageLengthLabel = new Label();
		maxMessageLengthLabel.setText("Max msg length");
		maxMessageLengthLabel.setStyle(LABEL_STYLE_CSS);
		GridPane.setHalignment(maxMessageLengthLabel, HPos.CENTER);
		grid.add(maxMessageLengthLabel, 0, grid.getRowCount());
		grid.add(maxMessageLength, 1, grid.getRowCount() - 1);

		root.getChildren().add(grid);

		stage.setResizable(false);
		stage.show();
	}

	public static void setTextFieldFormat(TextField t) {
		t.setStyle(TEXT_BOX_CSS);
		t.setMinWidth(TEXT_BOX_WIDTH);
		t.setPrefWidth(TEXT_BOX_WIDTH);
	}

	/**
	 * @param t           A text field
	 * @param defaultText default value to set if text field is empty
	 * @return contents of text field, defaultText if field is empty
	 */
	public static String getTextFieldStr_setDefault(final TextField t, final String defaultText) {
		if (t.getText().length() <= 0)
			t.setText(defaultText);
		return t.getText();
	}

	/**
	 * adds contiguous region of empty points from given x and y to emptyNeighbors
	 */
	protected static void addEmptyNeighbors(ArrayList<Point> empty, int x, int y, ArrayList<Point> emptyNeighbors) {
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++)
				if (i != j) {
					Point p = new Point(x + i, y + j);
					if (empty.contains(p) && !emptyNeighbors.contains(p)) {
						emptyNeighbors.add(p);
						addEmptyNeighbors(empty, p.x, p.y, emptyNeighbors);
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
	protected static int getNearbyBombCount(final ArrayList<Point> bombs, final Point p) {
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
				if (includeDescription)
					result += "Size: " + Integer.toString(size) + "\tMines: " + Integer.toString(noOfMines) + "\n";

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
					String cellStr;

					if (bombs.contains(p)) {
						cellStr = getTextFieldStr_setDefault(bombTextField, DEFUALT_BOMB);
					} else {
						int bombCount = getNearbyBombCount(bombs, p);
						cellStr = getTextFieldStr_setDefault(bombCountTextFields[bombCount],
								DEFAULT_BOMB_COUNTS_STR[bombCount]);
					}

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
