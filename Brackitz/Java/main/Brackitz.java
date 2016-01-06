import java.util.ArrayDeque;

public class Brackitz {

    public enum BraceType {
        CURLY,
        SQUARE,
        PAREN
    }

    static BraceType openBraceType(char c) {
        switch(c) {
            case '{': return BraceType.CURLY;
            case '[': return BraceType.SQUARE;
            case '(': return BraceType.PAREN;
            default: return null;
        }
    }

    static BraceType closeBraceType(char c) {
        switch(c) {
            case '}': return BraceType.CURLY;
            case ']': return BraceType.SQUARE;
            case ')': return BraceType.PAREN;
            default: return null;
        }
    }

    enum CharClass { OPEN, CLOSE, NONE };

    CharClass charClass(char c) {
        switch(c) {
            case '{':
            case '(':
            case '[': return CharClass.OPEN;
            case '}':
            case ')':
            case ']': return CharClass.CLOSE;
        }
        return CharClass.NONE;
    }

    private final String input;
    private final ArrayDeque<BraceType> remainingBraces = new ArrayDeque<>();
    // Future optimization: Instead of stack of BraceType, stack of <BraceType, count> so that repeats don't eat memory.

    public Brackitz(String input) {
        this.input = input;
    }

    public boolean isValid() {
        for(char c: input.toCharArray()) {
            switch (charClass(c)) {
                case NONE:
                    break;

                case OPEN:
                    BraceType openBraceType = openBraceType(c);
                    remainingBraces.push(openBraceType);
                    break;

                case CLOSE:
                    BraceType closeBraceType = closeBraceType(c);
                    if (remainingBraces.isEmpty()) {
                        return false;
                    }
                    if (!remainingBraces.peek().equals(closeBraceType)) {
                        return false;
                    }
                    remainingBraces.pop();
            }
        }
        return remainingBraces.isEmpty();
    }
}
