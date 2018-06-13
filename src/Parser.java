import java.util.*;

class Parser {
    Map<String, Object[]> tableOfVariables = new HashMap<>();//[var, [type, value]]
    List<String> tokens_polis = new ArrayList<>();
    private Stack<String> stack = new Stack<>();
    private List<Token> tokens = new ArrayList<>();
    private int position = 0;
    private int p1;
    private int p2;

    boolean lang(List<Token> tokens) {
        boolean lang = false;
        int majorTokens = 1;

        for (Token token : tokens) {
            if (token.getLexeme() != Lexeme.WS) {
                this.tokens.add(token);
            }
        }
        while (this.tokens.size() != position) {

            if (!expr()) {
                System.err.println("Error: ErrorSyntax in majorToken: " + majorTokens);
                System.exit(4);
            } else {
                majorTokens++;
                lang = true;
            }
        }

        return lang;
    }

    private boolean expr() {
        return init() || assign() || setAssign() || ifModule() || forModule();
    }

    private boolean assign() {
        boolean assign = false;
        int old_position = position;

        if (assignOperation()) {
            if (getCurrentTokenLexemeInc() == Lexeme.SEM) {
                assign = true;
            }
        }
        position = assign ? position : old_position;
        return assign;
    }

    private boolean assignOperation() {
        boolean assignOperation = false;
        int old_position = position;
        boolean add = false;
        String op, var;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            add = tokens_polis.add(getLastTokenValue());
            var = getLastTokenValue();

            if (getCurrentTokenLexemeInc() == Lexeme.ASSIGN_OP) {
                if (!tableOfVariables.get(var)[0].equals("set")) {
                    op = getLastTokenValue();
                    if (value()) {
                        while (!stack.empty()) {
                            tokens_polis.add(stack.pop());
                        }
                        tokens_polis.add(op);
                        assignOperation = true;
                    }
                } else {
                    System.err.println("Error: Try to assign set variable");
                    System.exit(666);
                }
            }
        }
        if (!assignOperation) {
            position = old_position;
            if (add) {
                tokens_polis.remove(tokens_polis.size() - 1);
            }
        }
        return assignOperation;
    }

    private boolean init() {
        boolean init = false;
        int old_position = position;
        String type, var;

        if (getCurrentTokenLexemeInc() == Lexeme.TYPE) {
            type = getLastTokenValue();
            if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
                var = getLastTokenValue();
                if (getCurrentTokenLexemeInc() == Lexeme.SEM) {
                    if (type.equals("set")) {
                        tableOfVariables.put(var, valueCreate(type, new HashSet()));
                    } else {
                        tableOfVariables.put(var, valueCreate(type, ""));
                    }
                    init = true;
                }
            }
        }
        position = init ? position : old_position;
        return init;
    }

    private boolean value() {
        if (val()) {
            while (valueOperation()) {
            }
            return true;
        }
        return false;
    }

    private boolean valueOperation() {
        boolean valueOperation = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.OP) {
            String arithmeticOP = getLastTokenValue();
            //
            if (!stack.empty()) {
                while (getPriority(arithmeticOP) <= getPriority(stack.peek())) {
                    tokens_polis.add(stack.pop());
                    if (stack.empty()) {
                        break;
                    }
                }
            }
            //
            stack.push(arithmeticOP);
            if (val()) {
                valueOperation = true;
            }
        }
        position = valueOperation ? position : old_position;
        return valueOperation;
    }

    private boolean val() {
        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            if (!tableOfVariables.containsKey(getLastTokenValue())) {
                System.err.println("Error: Variety " + getLastTokenValue() + " not initialize");
                System.exit(6);
            }
            tokens_polis.add(getLastTokenValue());
            return true;
        } else {
            position--;
        }
        if (getCurrentTokenLexemeInc() == Lexeme.DIGIT) {
            tokens_polis.add(getLastTokenValue());
            return true;
        } else {
            position--;
        }
        return breakValue();
    }

    private boolean breakValue() {
        boolean breakValue = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_R_SQU) {
            stack.push(getLastTokenValue());
            if (value()) {
                if (getCurrentTokenLexemeInc() == Lexeme.R_R_SQU) {
                    while (!stack.peek().equals("(")) {
                        tokens_polis.add(stack.pop());
                    }
                    stack.pop();
                    breakValue = true;
                }
            }
        }
        position = breakValue ? position : old_position;
        return breakValue;
    }

    private boolean setAssign() {
        return setClear() || setAdd() || setRemove();
    }

    private boolean forModule() {
        boolean forModule = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.FOR) {
            if (exprFor()) {
                if (body()) {
                    forModule = true;
                    tokens_polis.set(p1, String.valueOf(tokens_polis.size() + 2));//перепрыгиваем p2&!
                    tokens_polis.add(String.valueOf(p2));
                    tokens_polis.add("!");
                }
            }
        }
        position = forModule ? position : old_position;
        return forModule;
    }

    private boolean exprFor() {
        boolean exprFor = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_R_SQU) {
            if (assign()) {
                if (logExprFor()) {
                    if (assignOperation()) {
                        if (getCurrentTokenLexemeInc() == Lexeme.R_R_SQU) {
                            exprFor = true;
                        }
                    }
                }
            }
        }
        position = exprFor ? position : old_position;
        return exprFor;
    }

    private boolean logExprFor() {
        boolean logExprFor = false;
        int old_position = position;

        p2 = tokens_polis.size();
        if (logExpr()) {
            if (getCurrentTokenLexemeInc() == Lexeme.SEM) {
                logExprFor = true;
                p1 = tokens_polis.size();
                tokens_polis.add("p1");
                tokens_polis.add("!F");
            }
        }
        position = logExprFor ? position : old_position;
        return logExprFor;
    }

    private boolean ifModule() {
        boolean ifModule = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.IF) {
            if (exprIf()) {
                if (body()) {
                    ifModule = true;
                    tokens_polis.set(p1, String.valueOf(tokens_polis.size() + 2));//перепрыгиваем p2&!
                    p2 = tokens_polis.size();
                    tokens_polis.add("p2");
                    tokens_polis.add("!");
                    if (tokens.size() != position && getCurrentTokenLexemeInc() == Lexeme.ELSE) {
                        if (body()) {
                        }
                    }
                    tokens_polis.set(p2, String.valueOf(tokens_polis.size()));
                }
            }
        }
        position = ifModule ? position : old_position;
        return ifModule;
    }

    private boolean body() {
        boolean body = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_F_SQU) {
            while (init() || assign() || setAssign()) {
            }
            if (getCurrentTokenLexemeInc() == Lexeme.R_F_SQU) {
                body = true;
            }
        }
        position = body ? position : old_position;
        return body;
    }

    private boolean exprIf() {
        boolean exprIf = false;
        int old_position = position;

        if (getCurrentTokenLexemeInc() == Lexeme.L_R_SQU) {
            if (logExpr()) {
                if (getCurrentTokenLexemeInc() == Lexeme.R_R_SQU) {
                    exprIf = true;

                    p1 = tokens_polis.size();
                    tokens_polis.add("p1");
                    tokens_polis.add("!F");
                }
            }
        }
        position = exprIf ? position : old_position;
        return exprIf;
    }

    private boolean logExpr() {
        boolean logExpr = false;
        int old_position = position;
        String op = "";
        ArrayList<String> stack_old_position = new ArrayList<>();

        if (assignOperation() || value()) {
            if (getCurrentTokenLexemeInc() == Lexeme.LOG_OP) {
                op = getLastTokenValue();
                while (!stack.empty()) {
                    String pop = stack.pop();
                    tokens_polis.add(pop);
                    stack_old_position.add(pop);
                }
                if (assignOperation() || value()) {
                    while (!stack.empty()) {
                        tokens_polis.add(stack.pop());
                    }
                    logExpr = true;
                    tokens_polis.add(op);
                    stack_old_position.clear();
                }
            }
        }
        if (!logExpr) {
            position = old_position;
            if (op.length() != 0) {
                for (int i = stack_old_position.size() - 1; i >= 0; i--) {
                    stack.push(stack_old_position.get(i));
                    tokens_polis.remove(tokens_polis.size() - 1);
                }
                stack_old_position.clear();
            }
        }
        return logExpr;
    }

    private boolean setAdd() {
        boolean setAdd = false;
        int old_position = position;
        boolean add = false;
        String op, var;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            add = tokens_polis.add(getLastTokenValue());
            var = getLastTokenValue();

            if (getCurrentTokenLexemeInc() == Lexeme.ADD) {
                if (tableOfVariables.get(var)[0].equals("set")) {
                    op = getLastTokenValue();
                    if (value()) {
                        if (getCurrentTokenLexemeInc() == Lexeme.SEM) {
                            while (!stack.empty()) {
                                tokens_polis.add(stack.pop());
                            }
                            tokens_polis.add(op);
                            setAdd = true;
                        }
                    }
                } else {
                    System.err.println("Error: Try to add to not Set variable");
                    System.exit(301);
                }
            }
        }
        if (!setAdd) {
            position = old_position;
            if (add) {
                tokens_polis.remove(tokens_polis.size() - 1);
            }
        }
        return setAdd;
    }

    private boolean setRemove() {
        boolean setRemove = false;
        int old_position = position;
        boolean add = false;
        String op, var;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            add = tokens_polis.add(getLastTokenValue());
            var = getLastTokenValue();

            if (getCurrentTokenLexemeInc() == Lexeme.REMOVE) {
                if (tableOfVariables.get(var)[0].equals("set")) {
                    op = getLastTokenValue();
                    if (value()) {
                        if (getCurrentTokenLexemeInc() == Lexeme.SEM) {
                            while (!stack.empty()) {
                                tokens_polis.add(stack.pop());
                            }
                            tokens_polis.add(op);
                            setRemove = true;
                        }
                    }
                } else {
                    System.err.println("Error: Cant remove not Set variable");
                    System.exit(301);
                }
            }
        }
        if (!setRemove) {
            position = old_position;
            if (add) {
                tokens_polis.remove(tokens_polis.size() - 1);
            }
        }
        return setRemove;
    }

    private boolean setClear() {
        boolean setClear = false;
        int old_position = position;
        boolean add = false;
        String op, var;

        if (getCurrentTokenLexemeInc() == Lexeme.VAR) {
            add = tokens_polis.add(getLastTokenValue());
            var = getLastTokenValue();

            if (getCurrentTokenLexemeInc() == Lexeme.CLEAR) {
                if (tableOfVariables.get(var)[0].equals("set")) {
                    op = getLastTokenValue();
                    if (getCurrentTokenLexemeInc() == Lexeme.SEM) {
                        if (!stack.empty()) {
                            System.out.println("STACK is not empty");
                        }
                        while (!stack.empty()) {
                            tokens_polis.add(stack.pop());
                        }
                        tokens_polis.add(op);
                        setClear = true;
                    }
                } else {
                    System.err.println("Error: Cant clear not Set variable");
                    System.exit(300);
                }
            }
        }
        if (!setClear) {
            position = old_position;
            if (add) {
                tokens_polis.remove(tokens_polis.size() - 1);
            }
        }
        return setClear;
    }

    private Lexeme getCurrentTokenLexemeInc() {
        try {
            return tokens.get(position++).getLexeme();
        } catch (IndexOutOfBoundsException ex) {
            position--;
            System.err.println("Error: Lexeme \"" + tokens.get(--position).getLexeme() + "\" expected");
            System.exit(3);
        }
        return null;
    }

    private String getLastTokenValue() {
        return tokens.get(position - 1).getValue();
    }

    Object[] valueCreate(String type, Object value) {
        Object[] ret = new Object[2];

        ret[0] = type;
        ret[1] = value;
        return ret;
    }

    String printTOV(Map<String, Object[]> tov) {
        StringBuilder s = new StringBuilder();
        Set<String> keys = tov.keySet();
        String[] Dkeys = keys.toArray(new String[0]);

        s.append("[");
        for (int i = 0; i < Dkeys.length - 1; i++) {
            Object[] values = tov.get(Dkeys[i]);
            String value0 = values[0].toString();
            String value1 = values[1].toString();

            s.append("[");
            s.append(Dkeys[i]);
            s.append(", [");
            s.append(value0);
            s.append(", ");
            if (value1.equals("")) {
                s.append("null");
            } else {
                s.append(value1);
            }
            s.append("]], ");
        }
        String value0 = tov.get(Dkeys[Dkeys.length - 1])[0].toString();
        String value1 = tov.get(Dkeys[Dkeys.length - 1])[1].toString();

        s.append(Dkeys[Dkeys.length - 1]);
        s.append(", [");
        s.append(value0);
        s.append(", ");
        if (value1.equals("")) {
            s.append("null");
        } else {
            s.append(value1);
        }
        s.append("]]");
        s.append("]");
        return s.toString();
    }

    private int getPriority(String str) {
        switch (str) {
            case "+":
                return 1;
            case "*":
                return 2;
            case "^":
                return 2;
            case "-":
                return 1;
            case "/":
                return 2;
            case "%":
                return 2;
            case "=":
                return 0;
            case "(":
                return 0;
            default:
                System.err.println("Error: In symbol " + str);
                System.exit(5);
                return 0;
        }
    }
}