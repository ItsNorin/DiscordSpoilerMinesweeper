package main;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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

	public final static int TEXT_BOX_WIDTH = 80;

	public final static String LABEL_STYLE_CSS = "-fx-text-fill: #FFFFFF;";
	public final static String TEXT_BOX_DEFAULT_CSS = "-fx-background-color: #40444b; -fx-text-fill: #FFFFFF;";
	public final static String TEXT_BOX_HOVER_CSS = "-fx-background-color: #5e636a; -fx-text-fill: #FFFFFF;";
	public final static String BUTTON_DEFAULT_CSS = "-fx-background-color: #2f3136; -fx-text-fill: #FFFFFF;";
	public final static String BUTTON_HOVER_CSS = "-fx-background-color: #7289DA; -fx-text-fill: #FFFFFF;";

	public static final TextField[] bombCountTextFields;
	public static final TextField bombTextField;
	public static final TextField maxMessageLength;
	public static final TextField sizeTextBox;
	public static final TextField mineDensityTextBox;
	public static final Label latestMessage;

	public static boolean includeDescription;

	static {
		includeDescription = true;

		sizeTextBox = makeTextField("10");
		mineDensityTextBox = makeTextField("1");
		latestMessage = new Label();
		maxMessageLength = makeTextField(MAX_MESSAGE_LENGTH);
		bombTextField = makeTextField(DEFUALT_BOMB);

		bombCountTextFields = new TextField[9];
		for (int i = 0; i < bombCountTextFields.length; i++)
			bombCountTextFields[i] = makeTextField(DEFAULT_BOMB_COUNTS_STR[i]);
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

		Button generateFieldButton = new Button();
		generateFieldButton.setText("Generate and copy field");
		addMouseOverStyleChange(generateFieldButton, BUTTON_HOVER_CSS, BUTTON_DEFAULT_CSS);
		generateFieldButton.setOnAction(e -> generateField());

		Button includeFieldDescriptionButton = new Button();
		includeFieldDescriptionButton.setText("Toggle: Including description");
		addMouseOverStyleChange(includeFieldDescriptionButton, BUTTON_HOVER_CSS, BUTTON_DEFAULT_CSS);
		includeFieldDescriptionButton.setOnAction(e -> {
			includeDescription = !includeDescription;
			if (includeDescription)
				includeFieldDescriptionButton.setText("Toggle: Including description");
			else
				includeFieldDescriptionButton.setText("Toggle: Not including description");
		});

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(5);
		grid.setPadding(new Insets(10, 10, 20, 10));

		grid.add(makeCenteredLabel("Enter field size (5-20)"), 0, 0);
		grid.add(sizeTextBox, 1, 0);

		grid.add(makeCenteredLabel("Mine density (0.5-4)"), 0, grid.getRowCount());
		grid.add(mineDensityTextBox, 1, grid.getRowCount() - 1);

		GridPane.setHalignment(generateFieldButton, HPos.CENTER);
		grid.add(generateFieldButton, 0, grid.getRowCount(), 2, 1);

		GridPane.setHalignment(latestMessage, HPos.CENTER);
		grid.add(latestMessage, 0, grid.getRowCount(), 2, 1);

		GridPane.setHalignment(includeFieldDescriptionButton, HPos.CENTER);
		grid.add(includeFieldDescriptionButton, 0, grid.getRowCount(), 2, 1);

		grid.add(makeCenteredLabel("Bomb"), 0, grid.getRowCount());
		grid.add(bombTextField, 1, grid.getRowCount() - 1);

		for (int i = 0; i < bombCountTextFields.length; i++) {
			grid.add(bombCountTextFields[i], 1, grid.getRowCount());
			grid.add(makeCenteredLabel(Integer.toString(i)), 0, grid.getRowCount() - 1);
		}

		grid.add(makeCenteredLabel("Max msg. length"), 0, grid.getRowCount());
		grid.add(maxMessageLength, 1, grid.getRowCount() - 1);

		root.getChildren().add(grid);

		stage.setResizable(false);
		stage.show();
	}

	public static void generateField() {
		final String sizeTextBoxStr = sizeTextBox.getText();
		final String mesgLengthStr = getTextFieldStr_setDefault(maxMessageLength, MAX_MESSAGE_LENGTH);
		final String mineDensityStr = getTextFieldStr_setDefault(mineDensityTextBox, "1");

		if (sizeTextBoxStr.length() <= 0) {
			latestMessage.setText("Enter a size!");
			latestMessage.setTextFill(Color.ORANGERED);
		} else {
			int msgLength = 0;
			try {
				msgLength = Integer.parseInt(mesgLengthStr);
			} catch (Exception e) {
			}

			double mineDensity = 1;
			try {
				mineDensity = Double.parseDouble(mineDensityStr);
			} catch (Exception e) {
			}
			
			mineDensity = Math.max(mineDensity, 0.5);
			mineDensity = Math.min(mineDensity, 4);
			mineDensityTextBox.setText(Double.toString((double)((int)(mineDensity*100))/100));

			if (msgLength < 100) {
				latestMessage.setText("Message length less than 100!");
				latestMessage.setTextFill(Color.ORANGERED);
			} else {
				int fieldSize = 0;
				try {
					fieldSize = Integer.parseInt(sizeTextBoxStr);
				} catch (Exception e) {
				}

				if (fieldSize < 5 || fieldSize > 20) {
					latestMessage.setText("Field size must be between 5 and 20!");
					latestMessage.setTextFill(Color.ORANGERED);
				} else {
					String field = getNewMineField(fieldSize, (int) (mineDensity * fieldSize * fieldSize / 9),
							msgLength);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection strSel = new StringSelection(field);
					clipboard.setContents(strSel, null);
					latestMessage.setText("Copied to clipboard!");
					latestMessage.setTextFill(Color.web("#819dff"));
				}
			}
		}
	}

	/**
	 * @param text Text the label will contain
	 * @return new centered and formatted label containing given text
	 */
	public static Label makeCenteredLabel(String text) {
		Label l = new Label(text);
		l.setStyle(LABEL_STYLE_CSS);
		GridPane.setHalignment(l, HPos.CENTER);
		return l;
	}

	/**
	 * @param text Text the text field will contain initially
	 * @return new properly formatted text field
	 */
	public static TextField makeTextField(String text) {
		TextField t = new TextField(text);
		t.setMinWidth(TEXT_BOX_WIDTH);
		t.setPrefWidth(TEXT_BOX_WIDTH);
		addMouseOverStyleChange(t, TEXT_BOX_HOVER_CSS, TEXT_BOX_DEFAULT_CSS);
		t.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER)
				generateField();
		});
		return t;

	}

	/**
	 * @param n             any node
	 * @param mouseOverCSS  CSS to be used when mouse is over node
	 * @param mouseLeaveCSS Default CSS, used whenever mouse isn't on node
	 */
	public static void addMouseOverStyleChange(Node n, String mouseOverCSS, String mouseLeaveCSS) {
		n.setOnMouseEntered(e -> n.setStyle(mouseOverCSS));
		n.setOnMouseExited(e -> n.setStyle(mouseLeaveCSS));
		n.setStyle(mouseLeaveCSS);
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

			// field description
			result = (includeDescription)
					? "Size: " + Integer.toString(size) + "\tMines: " + Integer.toString(noOfMines) + "\n"
					: "";

			ArrayList<Point> leaveUnspoiled = new ArrayList<Point>();
			addEmptyNeighbors(emptyTiles, start.x, start.y, leaveUnspoiled);
			leaveUnspoiled = expandByOne(leaveUnspoiled);
			leaveUnspoiled.add(start);

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
