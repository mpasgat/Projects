import java.io.*;
import java.util.*;
/**
This class represents the main entry point for the game.
       **/
public class Main {
    /**
     * min size of the side of the board.
     */
    private static final int MINIMUM_SIZE_OF_SIDE_OF_THE_BOARD = 4;
    /**
     * max size of the side of the board.
     */
    private static final int MAXIMUM_SIZE_OF_SIDE_OF_THE_BOARD = 1000;
    /**
     * max number of insects on the board.
     */
    private static final int MAX_NUMBER_OF_INSECTS = 16;
    /**
     * max number of food points.
     */
    private static final int MAX_NUMBER_OF_FOOD_POINTS = 200;
    /**
    Represents the game board.
    Also helps to call methods from class Board
            **/
    private static Board gameBoard;
    /**
    Represents a list in the String type of insects in the game.
            **/
    private static final List<String> INSECTS = new ArrayList<>();
    /**
    Represents a list in the String type of colors available in the game.
     **/
    private static final List<String> COLORS = new ArrayList<>();
    /**
    Represents a list in the String type of positions on the game board.
     **/
    private static final List<String> POSITIONS = new ArrayList<>();
    /**
    Represents a list of in the Insect type insects in insertion order.
            **/
    private static final List<Insect> INSECTS_IN_INSERTION_ORDER_INSECT_TYPE = new ArrayList<>();

    /**
     * main where file operations will be held.
     * @param args
     */
    public static void main(String[] args) {
        try {
            File text = new File("input.txt");
            PrintWriter writer = new PrintWriter(new FileWriter("output.txt"));
            Scanner reader = new Scanner(text);
            int d = Integer.parseInt(reader.nextLine());
            if (d < MINIMUM_SIZE_OF_SIDE_OF_THE_BOARD || d > MAXIMUM_SIZE_OF_SIDE_OF_THE_BOARD) {
                try {
                    throw new InvalidBoardSizeException();
                } catch (InvalidBoardSizeException e) {
                    writer.println(e.getMessage());
                    writer.close();
                    System.exit(0);
                }
            }
            gameBoard = new Board(d);

            int n = Integer.parseInt(reader.nextLine());
            if (n < 1 || n > MAX_NUMBER_OF_INSECTS) {
                try {
                    throw new InvalidNumberOfInsectsException();
                } catch (InvalidNumberOfInsectsException e) {
                    writer.println(e.getMessage());
                    writer.close();
                    System.exit(0);
                }
            }
            int m = Integer.parseInt(reader.nextLine());
            if (m < 1 || m > MAX_NUMBER_OF_FOOD_POINTS) {
                try {
                    throw new InvalidNumberOfFoodPointsException();
                } catch (InvalidNumberOfFoodPointsException e) {
                    writer.println(e.getMessage());
                    writer.close();
                    System.exit(0);
                }
            }
            for (int i = 0; i < n; i++) {
                String[] currstr = reader.nextLine().split(" ");
                COLORS.add(currstr[0]);
                INSECTS.add(currstr[1]);
                List<String> colors = Arrays.asList("RED", "GREEN", "BLUE", "YELLOW");
                List<String> insects = Arrays.asList("ANT", "SPIDER", "BUTTERFLY", "GRASSHOPPER");
                if (!colors.contains(currstr[0].toUpperCase())) {
                    try {
                        throw new InvalidInsectColorException();
                    } catch (InvalidInsectColorException e) {
                        writer.println(e.getMessage());
                        writer.close();
                        System.exit(0);
                    }
                }
                InsectColor insectColor = InsectColor.toColor(currstr[0]);
                if (!insects.contains(currstr[1].toUpperCase())) {
                    try {
                        throw new InvalidInsectTypeException();
                    } catch (InvalidInsectTypeException e) {
                        writer.println(e.getMessage());
                        writer.close();
                        System.exit(0);
                    }
                }
                String coordinates_in_string = currstr[2] + " " + currstr[2 + 1];
                int xCoord = Integer.parseInt(currstr[2]);
                int yCoord = Integer.parseInt(currstr[2 + 1]);

                if ((xCoord < 1 || xCoord > d) || (yCoord < 1 || yCoord > d)) {
                    try {
                        throw new InvalidEntityPositionException();
                    } catch (InvalidEntityPositionException e) {
                        writer.println(e.getMessage());
                        writer.close();
                        System.exit(0);
                    }
                }
                POSITIONS.add(coordinates_in_string);
                EntityPosition entityPosition = new EntityPosition(xCoord, yCoord);
                Insect curInsect = null;
                switch (currstr[1].toLowerCase()) {
                    case "ant":
                        curInsect = new Ant(entityPosition, insectColor);
                        break;
                    case "grasshopper":
                        curInsect = new Grasshopper(entityPosition, insectColor);
                        break;
                    case "spider":
                        curInsect = new Spider(entityPosition, insectColor);
                        break;
                    case "butterfly":
                        curInsect = new Butterfly(entityPosition, insectColor);
                        break;
                    default:
                        break;
                }
                INSECTS_IN_INSERTION_ORDER_INSECT_TYPE.add(curInsect);
                gameBoard.addEntity(curInsect);
            }
            for (int i = 0; i < m; i++) {
                String[] currstr = reader.nextLine().split(" ");
                int value_of_food = Integer.parseInt(currstr[0]);
                String CoordInString = currstr[1] + " " + currstr[2];
                int xCoord = Integer.parseInt(currstr[1]);
                int yCoord = Integer.parseInt(currstr[2]);
                //String strCoor = currstr[1] + ", " + currstr[2];

                if ((xCoord < 1 || xCoord > d) || (yCoord < 1 || yCoord > d)) {
                    try {
                        throw new InvalidEntityPositionException();
                    } catch (InvalidEntityPositionException e) {
                        writer.println(e.getMessage());
                        writer.close();
                        System.exit(0);
                    }
                }
                POSITIONS.add(CoordInString);
                EntityPosition entityPosition = new EntityPosition(xCoord, yCoord);
                gameBoard.addEntity(new FoodPoint(entityPosition, value_of_food));
            }

            for (int i = 0; i < INSECTS.size(); i++) {
                for (int j = i + 1; j < INSECTS.size(); j++) {
                    if (INSECTS.get(i).equals(INSECTS.get(j)) && COLORS.get(i).equals(COLORS.get(j))) {
                        try {
                            throw new DuplicateInsectException();
                        } catch (DuplicateInsectException e) {
                            writer.println(e.getMessage());
                            writer.close();
                            System.exit(0);
                        }
                    }
                }
            }

            for (int i = 0; i < POSITIONS.size(); i++) {
                for (int j = i + 1; j < POSITIONS.size(); j++) {
                    if (POSITIONS.get(i).equals(POSITIONS.get(j))) {
                        try {
                            throw new TwoEntitiesOnSamePositionException();
                        } catch (TwoEntitiesOnSamePositionException e) {
                            writer.println(e.getMessage());
                            writer.close();
                            System.exit(0);
                        }
                    }
                }
            }
            int i = 0;
            for (Insect insect : INSECTS_IN_INSERTION_ORDER_INSECT_TYPE) {
                writer.println(COLORS.get(i) + " " + INSECTS.get(i) + " "
                        + gameBoard.getDirection(insect).getTextRepresentation() + " " + gameBoard.getDirectionSum(insect));
                i++;
            }
            writer.close();
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



enum Direction {
    /**
     * North is the text representation of N.
     */
    N("North"),
    /**
     * East is the text representation of E.
     */
    E("East"),
    /**
     * South is the text representation of S.
     */
    S("South"),
    /**
     * West is the text representation of W.
     */
    W("West"),
    /**
     * North-East is the text representation of NE.
     */
    NE("North-East"),
    /**
     * South-East is the text representation of SE.
     */
    SE("South-East"),
    /**
     * South-West is the text representation of SW.
     */
    SW("South-West"),
    /**
     * North-West is the text representation of NW.
     */
    NW("North-West");

    /**
     * textRepresentation of String type.
     */
    private String textRepresentation;

    public String getTextRepresentation() {
        return textRepresentation;
    }

    Direction(String text) {
        textRepresentation = text;
    }

}

class Board {
    /**
     * Creating map to store data about coordinates and entity(food or insect).
     */
    private Map<EntityPosition, BoardEntity> boardData = new HashMap<>();
    /**
     * size represents the size of the side of the board which is in form of the square.
     */
    private int size;

    /**
     * constructor which will give size of my board.
     * @param boardSize
     */
    public Board(int boardSize) {
        size = boardSize;
    }

    /**
     * adding entity to my board.
     * @param entity
     */
    public void addEntity(BoardEntity entity) {
        EntityPosition entityPosition = entity.getEntityPosition();
        boardData.put(entityPosition, entity);
    }

    /**
     * getting the position of entity.
     * @param position
     * @return gives the position
     */
    public BoardEntity getEntity(EntityPosition position) {
        return boardData.get(position);
    }

    /**
     * getting the best direction from all possible ones which will give the highest number of food points.
     * @param insect
     * @return gives the sum of collected food points
     */
    public Direction getDirection(Insect insect) {
        return insect.getBestDirection(boardData, size);
    }

    /**
     * getting the total number of food points received through an exact direction.
     * @param insect
     * @return gives one travel direction(dir)
     */
    public int getDirectionSum(Insect insect) {
        Direction dir  = getDirection(insect);
        return insect.travelDirection(dir, boardData, size);
    }
}


/**
 * boardEntity with entity position.
 */
abstract class BoardEntity {
    /**
     *
     */
    protected EntityPosition entityPosition;
    public EntityPosition getEntityPosition() {
        return entityPosition;
    }
}


class EntityPosition {
    /**
     * x coordinate.
     */
    private int x;
    public int getY() {
        return y;
    }

    /**
     * y coordinate.
     */
    private int y;
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityPosition that = (EntityPosition) o;
        return x == that.x && y == that.y;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}


/**
 * class FoodPoint helps to manage with value of food.
 */
class FoodPoint extends BoardEntity {
    /**
     * value represents food point.
     */
    protected int value;
    public int getValue() {
        return value;
    }
    public FoodPoint(EntityPosition position, int value) {
        this.value = value;
        entityPosition = position;
    }
}

/**
 * class Insect helps to deal with color and has abstract classes with direction and sum.
 */
abstract class Insect extends BoardEntity {
    /**
     * color of an Insect.
     */
    protected InsectColor color;
    public Insect(EntityPosition position, InsectColor color) {
        entityPosition = position;
        this.color = color;
     }
    public InsectColor getColor() {
        return color;
    }

    /**
     * method which helps to find best direction.
     * @param boardData
     * @param boardSize
     * @return best direction
     */

    public abstract Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize);

    /**
     * @param dir
     * @param boardData
     * @param boardSize
     * @return integer which is sum of food points
     */
    public abstract int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize);
}

enum InsectColor {
    /**
     * red color of an Insect.
     */
    RED,
    /**
     * green color of an Insect.
     */
    GREEN,
    /**
     * blue color of an Insect.
     */
    BLUE,
    /**
     * yellow color of an Insect.
     */
    YELLOW;
    public static InsectColor toColor(String s) {
        return InsectColor.valueOf(s.toUpperCase());
    }
}

class Grasshopper extends Insect {
    public Grasshopper(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }
    @Override
    public Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        Map<Integer, Direction> prefDirection = new HashMap<>(Direction.values().length);
        int valueEast = 0;

        for (int i = y; i < boardSize + 1; i += 2) {
            if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                    valueEast += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                }
            }
         }

        int valueWest = 0;
        for (int i = y; i > 0; i -= 2) {
            if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                    valueWest += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                }
            }
        }

        int valueSouth = 0;
        for (int i = x; i < boardSize + 1; i += 2) {
            if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                    valueSouth += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                }
            }
        }

        int valueNorth = 0;
        for (int i = x; i > 0; i -= 2) {
            if (boardData.containsKey(new EntityPosition(i, y)) && boardData.get(new EntityPosition(i, y)) != null) {
                if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                    valueNorth += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                }
            }
        }
        prefDirection.put(valueWest, Direction.W);
        prefDirection.put(valueSouth, Direction.S);
        prefDirection.put(valueEast, Direction.E);
        prefDirection.put(valueNorth, Direction.N);
        return prefDirection.get(Collections.max(prefDirection.keySet()));
    }
    @Override
    public int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        int result = 0;
        switch (dir) {
            case W:
                for (int i = y; i > 0; i -= 2) {
                    if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                        if (boardData.get(new EntityPosition(x, i)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(x, i))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                            boardData.put(new EntityPosition(x, i), null);
                        }
                    }
                }
                //System.out.println( ((FoodPoint) (boardData.get(new EntityPosition(4, 7)))).getValue());
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case E:
                for (int i = y; i < boardSize + 1; i += 2) {
                    if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                        if (boardData.get(new EntityPosition(x, i)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(x, i))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                            boardData.put(new EntityPosition(x, i), null);
                        }
                    }
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case S:
                for (int i = x; i < boardSize + 1; i += 2) {
                    if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                        if (boardData.get(new EntityPosition(i, y)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(i, y))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                            boardData.put(new EntityPosition(i, y), null);
                        }
                    }
                }
                //System.out.println( ((FoodPoint) (boardData.get(new EntityPosition(4, 7)))).getValue());
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case N:
                for (int i = x; i > 0; i -= 2) {
                    if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                        if (boardData.get(new EntityPosition(i, y)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(i, y))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                            boardData.put(new EntityPosition(i, y), null);
                        }
                    }
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            default:
                return result;
        }
    }
}

class Butterfly extends Insect implements OrthogonalMoving {
    public Butterfly(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    @Override
    public Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        Map<Integer, Direction> prefDirection = new HashMap<>(Direction.values().length / 2);
        int valueEast = 0;
        for (int i = y; i < boardSize + 1; i++) {
            if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                    valueEast += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                }
            }
        }

        int valueWest = 0;
        for (int i = y; i != 0; i--) {
            if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                    valueWest += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                }
            }
        }

        int valueSouth = 0;
        for (int i = x; i < boardSize + 1; i++) {
            if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                    valueSouth += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                }
            }
        }

        int valueNorth = 0;
        for (int i = x; i != 0; i--) {
            if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                    valueNorth += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                }
            }
        }
        prefDirection.put(valueWest, Direction.W);
        prefDirection.put(valueSouth, Direction.S);
        prefDirection.put(valueEast, Direction.E);
        prefDirection.put(valueNorth, Direction.N);
        return prefDirection.get(Collections.max(prefDirection.keySet()));
    }
    @Override
    public int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        int result = 0;
        switch (dir) {
            case W:
                for (int i = y; i != 0; i--) {
                    if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                        if (boardData.get(new EntityPosition(x, i)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(x, i))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                            boardData.put(new EntityPosition(x, i), null);
                        }
                    }
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case E:
                for (int i = y; i < boardSize + 1; i++) {
                    if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                        if (boardData.get(new EntityPosition(x, i)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(x, i))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                            boardData.put(new EntityPosition(x, i), null);
                        }
                    }
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case S:
                for (int i = x; i < boardSize + 1; i++) {
                    if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                        if (boardData.get(new EntityPosition(i, y)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(i, y))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                            boardData.put(new EntityPosition(i, y), null);
                        }
                    }
                }
                //System.out.println( ((FoodPoint) (boardData.get(new EntityPosition(4, 7)))).getValue());
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case N:
                for (int i = x; i != 0; i--) {
                    if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                        if (boardData.get(new EntityPosition(i, y)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(i, y))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                            boardData.put(new EntityPosition(i, y), null);
                        }
                    }
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            default:
                return result;


        }
    }
    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return 0;
    }
    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return 0;
    }
}
class Ant extends Insect implements OrthogonalMoving, DiagonalMoving {
    public Ant(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }

    public Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        int j = x;
        int k = y;
        Map<Integer, Direction> prefDir = new HashMap<>(Direction.values().length);
        int valueNE = 0;
        while (j > 0 && k < boardSize + 1) {
            if (boardData.get(new EntityPosition(j, k)) != null && boardData.containsKey(new EntityPosition(j, k))) {
                if (boardData.get(new EntityPosition(j, k)) instanceof FoodPoint) {
                    valueNE += ((FoodPoint) boardData.get(new EntityPosition(j, k))).getValue();
                }
            }
            j--;
            k++;
        }
        j = x;
        k = y;
        int valueNW = 0;
        while (j > 0 && k > 0) {
            if (boardData.get(new EntityPosition(j, k)) != null && boardData.containsKey(new EntityPosition(j, k))) {
                if (boardData.get(new EntityPosition(j, k)) instanceof FoodPoint) {
                    valueNW += ((FoodPoint) boardData.get(new EntityPosition(j, k))).getValue();
                }
            }
            j--;
            k--;
        }
        j = x;
        k = y;
        int valueWS = 0;
        while (j < boardSize + 1 && k > 0) {
            if (boardData.get(new EntityPosition(j, k)) != null && boardData.containsKey(new EntityPosition(j, k))) {
                if (boardData.get(new EntityPosition(j, k)) instanceof FoodPoint) {
                    valueWS += ((FoodPoint) boardData.get(new EntityPosition(j, k))).getValue();
                }
            }
            j++;
            k--;
        }
        int valueES = 0;
        j = x;
        k = y;
        while (j < boardSize + 1 && k < boardSize + 1) {
            if (boardData.get(new EntityPosition(j, k)) != null && boardData.containsKey(new EntityPosition(j, k))) {
                if (boardData.get(new EntityPosition(j, k)) instanceof FoodPoint) {
                    valueES += ((FoodPoint) boardData.get(new EntityPosition(j, k))).getValue();
                }
            }
            j++;
            k++;
        }
        int valueEast = 0;
        for (int i = y; i < boardSize + 1; i++) {
            if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                    valueEast += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                }
            }
        }

        int valueWest = 0;
        for (int i = y; i != 0; i--) {
            if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                    valueWest += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                }
            }
        }

        int valueSouth = 0;
        for (int i = x; i < boardSize + 1; i++) {
            if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                    valueSouth += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                }
            }
        }

        int valueNorth = 0;
        for (int i = x; i != 0; i--) {
            if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                    valueNorth += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                }
            }
        }


        prefDir.put(valueNW, Direction.NW);
        prefDir.put(valueWS, Direction.SW);
        prefDir.put(valueES, Direction.SE);
        prefDir.put(valueNE, Direction.NE);
        prefDir.put(valueWest, Direction.W);
        prefDir.put(valueSouth, Direction.S);
        prefDir.put(valueEast, Direction.E);
        prefDir.put(valueNorth, Direction.N);

        return prefDir.get(Collections.max(prefDir.keySet()));
    }

    public int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        int xi = x;
        int yi = y;
        int result = 0;
        switch (dir) {
            case SE:
                while (xi < boardSize + 1 && yi < boardSize + 1) {
                    if (boardData.get(new EntityPosition(xi, yi)) != null && boardData.containsKey(new EntityPosition(xi, yi))) {
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(xi, yi))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(xi, yi))).getValue();
                            boardData.put(new EntityPosition(xi, yi), null);
                        }
                    }
                    xi++;
                    yi++;
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case SW:
                while (xi < boardSize + 1 && yi > 0) {
                    if (boardData.get(new EntityPosition(xi, yi)) != null && boardData.containsKey(new EntityPosition(xi, yi))) {
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(xi, yi))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(xi, yi))).getValue();
                            boardData.put(new EntityPosition(xi, yi), null);
                        }
                    }
                    xi++;
                    yi--;
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case NE:
                while (xi > 0 && yi < boardSize + 1) {
                    if (boardData.get(new EntityPosition(xi, yi)) != null && boardData.containsKey(new EntityPosition(xi, yi))) {
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(xi, yi))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(xi, yi))).getValue();
                            boardData.put(new EntityPosition(xi, yi), null);
                        }
                    }
                    xi--;
                    yi++;
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case NW:
                while (xi > 0 && yi > 0) {
                    if (boardData.get(new EntityPosition(xi, yi)) != null && boardData.containsKey(new EntityPosition(xi, yi))) {
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(xi, yi))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(xi, yi))).getValue();
                            boardData.put(new EntityPosition(xi, yi), null);
                        }
                    }
                    xi--;
                    yi--;
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case W:
                for (int i = y; i != 0; i--) {
                    if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                        if (boardData.get(new EntityPosition(x, i)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(x, i))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                            boardData.put(new EntityPosition(x, i), null);
                        }
                    }
                }
                //System.out.println( ((FoodPoint) (boardData.get(new EntityPosition(4, 7)))).getValue());
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case E:
                for (int i = y; i < boardSize + 1; i++) {
                    if (boardData.get(new EntityPosition(x, i)) != null && boardData.containsKey(new EntityPosition(x, i))) {
                        if (boardData.get(new EntityPosition(x, i)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(x, i))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(x, i)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(x, i))).getValue();
                            boardData.put(new EntityPosition(x, i), null);
                        }
                    }
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case S:
                for (int i = x; i < boardSize + 1; i++) {
                    if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                        if (boardData.get(new EntityPosition(i, y)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(i, y))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                            boardData.put(new EntityPosition(i, y), null);
                        }
                    }
                }
                //System.out.println( ((FoodPoint) (boardData.get(new EntityPosition(4, 7)))).getValue());
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case N:
                for (int i = x; i != 0; i--) {
                    if (boardData.get(new EntityPosition(i, y)) != null && boardData.containsKey(new EntityPosition(i, y))) {
                        if (boardData.get(new EntityPosition(i, y)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(i, y))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(i, y)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(i, y))).getValue();
                            boardData.put(new EntityPosition(i, y), null);
                        }
                    }
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            default:
                return result;
        }
    }
    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return 0;
    }
    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return 0;
    }
    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return 0;
    }
    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return 0;
    }
}
class Spider extends Insect implements DiagonalMoving {
    public Spider(EntityPosition entityPosition, InsectColor color) {
        super(entityPosition, color);
    }
    @Override
    public Direction getBestDirection(Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        int i = x;
        int k = y;
        Map<Integer, Direction> prefDir = new HashMap<>(Direction.values().length / 2);
        int valueNE = 0;
        while (i > 0 && k < boardSize + 1) {
            if (boardData.get(new EntityPosition(i, k)) != null && boardData.containsKey(new EntityPosition(i, k))) {
                if (boardData.get(new EntityPosition(i, k)) instanceof FoodPoint) {
                    valueNE += ((FoodPoint) boardData.get(new EntityPosition(i, k))).getValue();
                }
            }
            i--;
            k++;
        }
        i = x;
        k = y;
        int valueNW = 0;
        while (i > 0 && k > 0) {
            if (boardData.get(new EntityPosition(i, k)) != null && boardData.containsKey(new EntityPosition(i, k))) {
                if (boardData.get(new EntityPosition(i, k)) instanceof FoodPoint) {
                    valueNW += ((FoodPoint) boardData.get(new EntityPosition(i, k))).getValue();
                }
            }
            i--;
            k--;
        }
        i = x;
        k = y;
        int valueWS = 0;
        while (i < boardSize + 1 && k > 0) {
            if (boardData.get(new EntityPosition(i, k)) != null && boardData.containsKey(new EntityPosition(i, k))) {
                if (boardData.get(new EntityPosition(i, k)) instanceof FoodPoint) {
                    valueWS += ((FoodPoint) boardData.get(new EntityPosition(i, k))).getValue();
                }
            }
            i++;
            k--;
        }
        int valueES = 0;
        i = x;
        k = y;
        while (i < boardSize + 1 && k < boardSize + 1) {
            if (boardData.get(new EntityPosition(i, k)) != null && boardData.containsKey(new EntityPosition(i, k))) {
                if (boardData.get(new EntityPosition(i, k)) instanceof FoodPoint) {
                    valueES += ((FoodPoint) boardData.get(new EntityPosition(i, k))).getValue();
                }
            }
            i++;
            k++;
        }
        prefDir.put(valueNW, Direction.NW);
        prefDir.put(valueWS, Direction.SW);
        prefDir.put(valueES, Direction.SE);
        prefDir.put(valueNE, Direction.NE);

        return prefDir.get(Collections.max(prefDir.keySet()));
    }

    public int travelDirection(Direction dir, Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        int xi = x;
        int yi = y;
        int result = 0;
        switch (dir) {
            case SE:
                while (xi < boardSize + 1 && yi < boardSize + 1) {
                    if (boardData.get(new EntityPosition(xi, yi)) != null && boardData.containsKey(new EntityPosition(xi, yi))) {
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(xi, yi))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(xi, yi))).getValue();
                            boardData.put(new EntityPosition(xi, yi), null);
                        }
                    }
                    xi++;
                    yi++;
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case SW:
                while (xi < boardSize + 1 && yi > 0) {
                    if (boardData.get(new EntityPosition(xi, yi)) != null && boardData.containsKey(new EntityPosition(xi, yi))) {
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(xi, yi))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(xi, yi))).getValue();
                            boardData.put(new EntityPosition(xi, yi), null);
                        }
                    }
                    xi++;
                    yi--;
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case NE:
                while (xi > 0 && yi < boardSize + 1) {
                    if (boardData.get(new EntityPosition(xi, yi)) != null && boardData.containsKey(new EntityPosition(xi, yi))) {
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(xi, yi))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(xi, yi))).getValue();
                            boardData.put(new EntityPosition(xi, yi), null);
                        }
                    }
                    xi--;
                    yi++;
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            case NW:
                while (xi > 0 && yi > 0) {
                    if (boardData.get(new EntityPosition(xi, yi)) != null && boardData.containsKey(new EntityPosition(xi, yi))) {
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof Insect) {
                            if (((Insect) boardData.get(new EntityPosition(xi, yi))).getColor()
                                    != ((Insect) boardData.get(new EntityPosition(x, y))).getColor()) {
                                boardData.put(new EntityPosition(x, y), null);
                                return result;
                            }
                        }
                        if (boardData.get(new EntityPosition(xi, yi)) instanceof FoodPoint) {
                            result += ((FoodPoint) boardData.get(new EntityPosition(xi, yi))).getValue();
                            boardData.put(new EntityPosition(xi, yi), null);
                        }
                    }
                    xi--;
                    yi--;
                }
                boardData.put(new EntityPosition(x, y), null);
                return result;
            default:
                return result;
        }
    }
    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return 0;
    }
    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<EntityPosition, BoardEntity> boardData, int boardSize) {
        return 0;

    }
}

interface OrthogonalMoving {
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<EntityPosition, BoardEntity> boardData, int boardSize);
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition,
                                  InsectColor color, Map<EntityPosition, BoardEntity> boardData, int boardSize);
}

interface DiagonalMoving {
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<EntityPosition, BoardEntity> boardData, int boardSize);
    public int travelDiagonally(Direction dir, EntityPosition entityPosition,
                                InsectColor color, Map<EntityPosition, BoardEntity> boardData, int boardSize);
}

class InvalidBoardSizeException extends Exception {
    public String getMessage() {
        return "Invalid board size";
    }
}
class InvalidNumberOfInsectsException extends Exception {
    public String getMessage() {
        return "Invalid number of insects";
    }
}

class InvalidNumberOfFoodPointsException extends Exception {
    public String getMessage() {
        return "Invalid number of food points";
    }
}
class InvalidInsectColorException extends Exception {
    public String getMessage() {
        return "Invalid insect color";
    }
}
class InvalidInsectTypeException extends Exception {
    public String getMessage() {
        return "Invalid insect type";
    }
}
class InvalidEntityPositionException extends Exception {
    public String getMessage() {
        return "Invalid entity position";
    }
}
class DuplicateInsectException extends Exception {
    public String getMessage() {
        return "Duplicate insects";
    }
}
class TwoEntitiesOnSamePositionException extends Exception {
    public String getMessage() {
        return "Two entities in the same position";
    }
}
