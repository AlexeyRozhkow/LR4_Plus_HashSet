import java.util.regex.Pattern;

public enum Lexeme {
    TYPE(Pattern.compile("^int|set$")),
    ADD(Pattern.compile("^add$")),
    REMOVE(Pattern.compile("^remove$")),
    //CONTAINS(Pattern.compile("^contains$")),
    //ISEMPTY(Pattern.compile("^isempty$")),
    CLEAR(Pattern.compile("^clear$")),
    IF(Pattern.compile("^if$")),
    FOR(Pattern.compile("^for$")),
    ELSE(Pattern.compile("^else$")),
    VAR(Pattern.compile("^[a-z]+$")),
    ASSIGN_OP(Pattern.compile("^=$")),
    DIGIT(Pattern.compile("^0|[1-9][0-9]*")),
    OP(Pattern.compile("^\\+|-|\\*|/|%|\\^$")),
    WS(Pattern.compile("^\\s+")),
    L_F_SQU(Pattern.compile("^\\{$")),
    R_F_SQU(Pattern.compile("^}$")),
    L_R_SQU(Pattern.compile("^\\($")),
    R_R_SQU(Pattern.compile("^\\)$")),
    LOG_OP(Pattern.compile("^<|>|<=|>=|!=|==$")),
    INV(Pattern.compile("!")),
    SEM(Pattern.compile("^;$"));
    /*(Pattern.compile("")),*/
    Pattern pattern;

    Lexeme(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
