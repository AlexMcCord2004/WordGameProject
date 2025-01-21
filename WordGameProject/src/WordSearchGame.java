import java.util.*;

public interface WordSearchGame {
    void loadLexicon(String fileName);
    void setBoard(String[] letterArray);
    String getBoard();
    SortedSet<String> getAllValidWords(int minimumWordLength);
    int getScoreForWords(SortedSet<String> words, int minimumWordLength);
    boolean isValidWord(String wordToCheck);
    boolean isValidPrefix(String prefixToCheck);
    List<Integer> isOnBoard(String wordToCheck);
}
