import java.util.*;
import java.io.*;

public class WordSearchGameImpl implements WordSearchGame {
    private Set<String> lexicon;
    private String[] board;
    private boolean lexiconLoaded = false;

    @Override
    public void loadLexicon(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("File name is null");
        }

        System.out.println("Attempting to load file: " + new File(fileName).getAbsolutePath());

        lexicon = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNext()) {
                lexicon.add(scanner.next().toUpperCase());
            }
            lexiconLoaded = true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            System.out.println("Loading default lexicon instead.");
            loadDefaultLexicon(); // Load the default lexicon if the file is not found
        }
    }

    // Load a default lexicon if the file isn't found
    private void loadDefaultLexicon() {
        lexicon.add("APPLE");
        lexicon.add("BANANA");
        lexicon.add("ORANGE");
        lexicon.add("GRAPE");
        lexicon.add("PEAR");
        lexicon.add("CHERRY");
        lexiconLoaded = true;
    }

    @Override
    public void setBoard(String[] letterArray) {
        if (letterArray == null || !isSquare(letterArray.length)) {
            throw new IllegalArgumentException("Invalid board");
        }
        this.board = letterArray.clone();
    }

    private boolean isSquare(int num) {
        double squareRoot = Math.sqrt(num);
        return squareRoot == Math.floor(squareRoot);
    }

    @Override
    public String getBoard() {
        StringBuilder boardString = new StringBuilder();
        int boardSize = (int) Math.sqrt(board.length);

        for (int i = 0; i < board.length; i++) {
            boardString.append(board[i]);
            if ((i + 1) % boardSize == 0) {
                boardString.append("\n");
            } else {
                boardString.append(" ");
            }
        }
        return boardString.toString();
    }

    @Override
    public SortedSet<String> getAllValidWords(int minimumWordLength) {
        if (minimumWordLength < 1) {
            throw new IllegalArgumentException("Invalid minimum word length");
        }
        if (!lexiconLoaded) {
            throw new IllegalStateException("Lexicon not loaded");
        }

        SortedSet<String> validWords = new TreeSet<>();
        for (int i = 0; i < board.length; i++) {
            boolean[] visited = new boolean[board.length];
            dfs(i, "", visited, validWords, minimumWordLength);
        }
        return validWords;
    }

    private void dfs(int pos, String prefix, boolean[] visited, Set<String> validWords, int minLen) {
        if (pos < 0 || pos >= board.length || visited[pos]) {
            return;
        }

        String newWord = prefix + board[pos];
        visited[pos] = true;

        if (newWord.length() >= minLen && lexicon.contains(newWord)) {
            validWords.add(newWord);
        }

        if (isValidPrefix(newWord)) {
            int boardSize = (int) Math.sqrt(board.length);

            for (int row = -1; row <= 1; row++) {
                for (int col = -1; col <= 1; col++) {
                    int newRow = pos / boardSize + row;
                    int newCol = pos % boardSize + col;
                    int newPos = newRow * boardSize + newCol;

                    if (newRow >= 0 && newRow < boardSize && newCol >= 0 && newCol < boardSize) {
                        dfs(newPos, newWord, visited.clone(), validWords, minLen);
                    }
                }
            }
        }
        visited[pos] = false;
    }

    @Override
    public int getScoreForWords(SortedSet<String> words, int minimumWordLength) {
        if (minimumWordLength < 1) {
            throw new IllegalArgumentException("Invalid minimum word length");
        }
        if (!lexiconLoaded) {
            throw new IllegalStateException("Lexicon not loaded");
        }

        int score = 0;
        for (String word : words) {
            if (word.length() >= minimumWordLength && lexicon.contains(word)) {
                score += word.length() - minimumWordLength + 1;
            }
        }
        return score;
    }

    @Override
    public boolean isValidWord(String wordToCheck) {
        if (wordToCheck == null) {
            throw new IllegalArgumentException("Word is null");
        }
        if (!lexiconLoaded) {
            throw new IllegalStateException("Lexicon not loaded");
        }
        return lexicon.contains(wordToCheck.toUpperCase());
    }

    @Override
    public boolean isValidPrefix(String prefixToCheck) {
        if (prefixToCheck == null) {
            throw new IllegalArgumentException("Prefix is null");
        }
        if (!lexiconLoaded) {
            throw new IllegalStateException("Lexicon not loaded");
        }

        for (String word : lexicon) {
            if (word.startsWith(prefixToCheck.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Integer> isOnBoard(String wordToCheck) {
        if (wordToCheck == null) {
            throw new IllegalArgumentException("Word is null");
        }
        if (!lexiconLoaded) {
            throw new IllegalStateException("Lexicon not loaded");
        }

        List<Integer> path = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            boolean[] visited = new boolean[board.length];
            if (dfsFindWord(i, "", visited, wordToCheck.toUpperCase(), path)) {
                return path;
            }
        }
        return Collections.emptyList();
    }

    private boolean dfsFindWord(int pos, String prefix, boolean[] visited, String wordToCheck, List<Integer> path) {
        if (pos < 0 || pos >= board.length || visited[pos]) {
            return false;
        }

        String newWord = prefix + board[pos];
        visited[pos] = true;
        path.add(pos);

        if (newWord.equals(wordToCheck)) {
            return true;
        }

        if (isValidPrefix(newWord)) {
            int boardSize = (int) Math.sqrt(board.length);

            for (int row = -1; row <= 1; row++) {
                for (int col = -1; col <= 1; col++) {
                    int newRow = pos / boardSize + row;
                    int newCol = pos % boardSize + col;
                    int newPos = newRow * boardSize + newCol;

                    if (newRow >= 0 && newRow < boardSize && newCol >= 0 && newCol < boardSize) {
                        if (dfsFindWord(newPos, newWord, visited.clone(), wordToCheck, path)) {
                            return true;
                        }
                    }
                }
            }
        }

        visited[pos] = false;
        path.remove(path.size() - 1);
        return false;
    }

    /** Main method for testing. */
    public static void main(String[] args) {
        WordSearchGameImpl game = new WordSearchGameImpl();

        // Use a relative or absolute path to the file
        game.loadLexicon("words.txt"); // Adjust this path as needed
        game.setBoard(new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I"});

        System.out.println("Board:");
        System.out.println(game.getBoard());

        SortedSet<String> validWords = game.getAllValidWords(3);
        System.out.println("Valid words: " + validWords);

        int score = game.getScoreForWords(validWords, 3);
        System.out.println("Score: " + score);
    }
}
